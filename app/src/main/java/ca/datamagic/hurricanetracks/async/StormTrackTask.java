package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.StormTrackDAO;
import ca.datamagic.hurricanetracks.dto.StormTrackDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class StormTrackTask extends AsyncTaskBase<Void, Void, List<StormTrackDTO>> {
    private static final Logger logger = LogFactory.getLogger(StormTrackTask.class);
    private static StormTrackDAO dao = new StormTrackDAO();
    private String basin = null;
    private Integer year = null;
    private Integer stormNo = null;

    public StormTrackTask(String basin, Integer year, Integer stormNo) {
        this.basin = basin;
        this.year = year;
        this.stormNo = stormNo;
    }

    public void setBasin(String newVal) {
        this.basin = newVal;
    }

    public void setYear(Integer newVal) {
        this.year = newVal;
    }

    public void setStormNo(Integer newVal) {
        this.stormNo = newVal;
    }

    @Override
    protected AsyncTaskResult<List<StormTrackDTO>> doInBackground(Void... voids) {
        logger.info("Retrieving storm tracks...");
        try {
            logger.info("basin: " + this.basin);
            logger.info("year: " + Integer.toString(this.year.intValue()));
            logger.info("stormNo: " + Integer.toString(this.stormNo.intValue()));
            return new AsyncTaskResult<List<StormTrackDTO>>(dao.tracks(this.basin, this.year, this.stormNo));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormTrackDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormTrackDTO>> result) {
        logger.info("...storm tracks retrieved.");
        fireCompleted(result);
    }
}
