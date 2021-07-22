package ca.datamagic.hurricanetracks.dao;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.logging.LogFactory;
import ca.datamagic.hurricanetracks.util.IOUtils;

public class BaseDAO {
    private static final Logger logger = LogFactory.getLogger(BaseDAO.class);
    private static String appKey = null;

    public static synchronized String getAppKey() {
        return appKey;
    }

    public static synchronized void setAppKey(String newVal) {
        appKey = newVal;
    }

    protected TableResult runQuery(String query) throws IOException, InterruptedException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(appKey.getBytes()))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        BigQuery bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(query)
                        // Use standard SQL syntax for queries.
                        // See: https://cloud.google.com/bigquery/sql-reference/
                        .setUseLegacySql(false)
                        .build();
        // Create a job ID so that we can safely retry.
        JobId jobId = JobId.of(UUID.randomUUID().toString().toUpperCase());
        Job queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
        queryJob = queryJob.waitFor();

        // Check for errors
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }

        // Get the results
        return queryJob.getQueryResults();
    }

    protected String get(String urlSpec) throws IOException {
        logger.info("get: " + urlSpec);
        URL url = new URL(urlSpec);
        InputStream input = null;
        try {
            input = url.openStream();
            return IOUtils.readEntireStream(input);
        } finally {
            if (input != null) {
                IOUtils.closeQuietly(input);
            }
        }
    }
}
