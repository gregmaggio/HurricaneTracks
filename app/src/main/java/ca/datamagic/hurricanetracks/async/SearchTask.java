package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.SearchDAO;
import ca.datamagic.hurricanetracks.dto.StormKeyDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class SearchTask extends AsyncTaskBase<Void, Void, List<StormKeyDTO>> {
    private static Logger logger = LogFactory.getLogger(SearchTask.class);
    private static SearchDAO dao = new SearchDAO();
    private String searchText = null;

    public SearchTask(String searchText) {
        this.searchText = searchText;
    }

    @Override
    protected AsyncTaskResult<List<StormKeyDTO>> doInBackground(Void... voids) {
        logger.info("Performing search...");
        try {
            return new AsyncTaskResult<List<StormKeyDTO>>(dao.search(this.searchText));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormKeyDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormKeyDTO>> result) {
        logger.info("...search performed.");
        fireCompleted(result);
    }
}
