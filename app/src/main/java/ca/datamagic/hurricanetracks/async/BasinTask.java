package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.BasinDAO;
import ca.datamagic.hurricanetracks.dto.BasinDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class BasinTask extends AsyncTaskBase<Void, Void, List<BasinDTO>> {
    private static final Logger logger = LogFactory.getLogger(BasinTask.class);
    private static BasinDAO dao = new BasinDAO();

    @Override
    protected AsyncTaskResult<List<BasinDTO>> doInBackground(Void... voids) {
        logger.info("Retrieving basins...");
        try {
            return new AsyncTaskResult<List<BasinDTO>>(dao.basins());
        } catch (Throwable t) {
            return new AsyncTaskResult<List<BasinDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<BasinDTO>> result) {
        logger.info("...basins retrieved.");
        fireCompleted(result);
    }
}
