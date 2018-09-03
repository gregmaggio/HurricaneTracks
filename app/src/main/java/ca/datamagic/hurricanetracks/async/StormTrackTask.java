package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.StormTrackDAO;
import ca.datamagic.hurricanetracks.dto.StormTrackDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class StormTrackTask extends AsyncTaskBase<Void, Void, List<StormTrackDTO>> {
    private static Logger _logger = LogFactory.getLogger(StormTrackTask.class);
    private static StormTrackDAO _dao = new StormTrackDAO();
    private String _basin = null;
    private Integer _year = null;
    private Integer _stormNo = null;

    public StormTrackTask(String basin, Integer year, Integer stormNo) {
        _basin = basin;
        _year = year;
        _stormNo = stormNo;
    }

    @Override
    protected AsyncTaskResult<List<StormTrackDTO>> doInBackground(Void... voids) {
        _logger.info("Retrieving storm tracks...");
        try {
            _logger.info("basin: " + _basin);
            _logger.info("year: " + Integer.toString(_year.intValue()));
            _logger.info("stormNo: " + Integer.toString(_stormNo.intValue()));
            return new AsyncTaskResult<List<StormTrackDTO>>(_dao.tracks(_basin, _year, _stormNo));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormTrackDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormTrackDTO>> result) {
        _logger.info("...storm tracks retrieved.");
        fireCompleted(result);
    }
}
