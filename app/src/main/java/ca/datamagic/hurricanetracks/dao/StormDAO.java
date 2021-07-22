package ca.datamagic.hurricanetracks.dao;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormDTO;

public class StormDAO extends BaseDAO {
    public List<StormDTO> storms(String basin, Integer year) throws IOException, InterruptedException {
        List<StormDTO> storms = new ArrayList<StormDTO>();
        if ((basin != null) && (basin.length() > 0) && (year != null)) {
            String query = MessageFormat.format("SELECT number, name, COUNT(iso_time) as tracks FROM `bigquery-public-data.noaa_hurricanes.hurricanes` WHERE basin = {0} AND season = {1} GROUP BY number, name ORDER BY number, name", "'" + basin + "'", "'" + Integer.toString(year) + "'");
            TableResult result = runQuery(query);
            for (FieldValueList row : result.iterateAll()) {
                storms.add(new StormDTO((int) row.get("number").getLongValue(), row.get("name").getStringValue(), (int) row.get("tracks").getLongValue()));
            }
        }
        return storms;
    }
}
