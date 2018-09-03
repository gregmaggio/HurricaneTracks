package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.BasinDAO;
import ca.datamagic.hurricanetracks.dto.BasinDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class BasinTask extends AsyncTaskBase<Void, Void, List<BasinDTO>> {
    private static Logger _logger = LogFactory.getLogger(BasinTask.class);
    private static BasinDAO _dao = new BasinDAO();

    @Override
    protected AsyncTaskResult<List<BasinDTO>> doInBackground(Void... voids) {
        _logger.info("Retrieving basins...");
        try {
            return new AsyncTaskResult<List<BasinDTO>>(_dao.basins());
        } catch (Throwable t) {
            return new AsyncTaskResult<List<BasinDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<BasinDTO>> result) {
        _logger.info("...basins retrieved.");
        fireCompleted(result);
    }
}
