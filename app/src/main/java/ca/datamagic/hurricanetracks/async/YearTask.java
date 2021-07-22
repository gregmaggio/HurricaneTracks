package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.BasinDAO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class YearTask extends AsyncTaskBase<Void, Void, List<Integer>> {
    private static final Logger logger = LogFactory.getLogger(YearTask.class);
    private static BasinDAO dao = new BasinDAO();
    private String basin = null;

    public YearTask(String basin) {
        this.basin = basin;
    }

    public void setBasin(String newVal) {
        this.basin = newVal;
    }

    @Override
    protected AsyncTaskResult<List<Integer>> doInBackground(Void... voids) {
        logger.info("Retrieving years...");
        try {
            logger.info("basin: " + this.basin);
            return new AsyncTaskResult<List<Integer>>(dao.years(this.basin));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<Integer>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<Integer>> result) {
        logger.info("...years retrieved.");
        fireCompleted(result);
    }
}
