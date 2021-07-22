package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.StormDAO;
import ca.datamagic.hurricanetracks.dto.StormDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class StormTask extends AsyncTaskBase<Void, Void, List<StormDTO>> {
    private static final Logger logger = LogFactory.getLogger(StormTask.class);
    private static StormDAO dao = new StormDAO();
    private String basin = null;
    private Integer year = null;

    public StormTask(String basin, Integer year) {
        this.basin = basin;
        this.year = year;
    }

    public void setBasin(String newVal) {
        this.basin = newVal;
    }

    public void setYear(Integer newVal) {
        this.year = newVal;
    }

    @Override
    protected AsyncTaskResult<List<StormDTO>> doInBackground(Void... voids) {
        logger.info("Retrieving storms...");
        try {
            logger.info("basin: " + this.basin);
            logger.info("year: " + Integer.toString(this.year.intValue()));
            return new AsyncTaskResult<List<StormDTO>>(this.dao.storms(this.basin, this.year));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormDTO>> result) {
        logger.info("...storms retrieved.");
        fireCompleted(result);
    }
}
