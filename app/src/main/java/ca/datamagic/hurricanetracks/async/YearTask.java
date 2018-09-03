package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.BasinDAO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class YearTask extends AsyncTaskBase<Void, Void, List<Integer>> {
    private static Logger _logger = LogFactory.getLogger(YearTask.class);
    private static BasinDAO _dao = new BasinDAO();
    private String _basin = null;

    public YearTask(String basin) {
        _basin = basin;
    }

    @Override
    protected AsyncTaskResult<List<Integer>> doInBackground(Void... voids) {
        _logger.info("Retrieving years...");
        try {
            _logger.info("basin: " + _basin);
            return new AsyncTaskResult<List<Integer>>(_dao.years(_basin));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<Integer>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<Integer>> result) {
        _logger.info("...years retrieved.");
        fireCompleted(result);
    }
}
