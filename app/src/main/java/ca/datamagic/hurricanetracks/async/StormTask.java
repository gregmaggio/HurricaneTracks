package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.StormDAO;
import ca.datamagic.hurricanetracks.dto.StormDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class StormTask extends AsyncTaskBase<Void, Void, List<StormDTO>> {
    private static Logger _logger = LogFactory.getLogger(StormTask.class);
    private static StormDAO _dao = new StormDAO();
    private String _basin = null;
    private Integer _year = null;

    public StormTask(String basin, Integer year) {
        _basin = basin;
        _year = year;
    }

    @Override
    protected AsyncTaskResult<List<StormDTO>> doInBackground(Void... voids) {
        _logger.info("Retrieving storms...");
        try {
            _logger.info("basin: " + _basin);
            _logger.info("year: " + Integer.toString(_year.intValue()));
            return new AsyncTaskResult<List<StormDTO>>(_dao.storms(_basin, _year));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormDTO>> result) {
        _logger.info("...storms retrieved.");
        fireCompleted(result);
    }
}
