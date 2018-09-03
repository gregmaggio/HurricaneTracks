package ca.datamagic.hurricanetracks.async;

import java.util.List;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.dao.SearchDAO;
import ca.datamagic.hurricanetracks.dto.StormKeyDTO;
import ca.datamagic.hurricanetracks.logging.LogFactory;

public class SearchTask extends AsyncTaskBase<Void, Void, List<StormKeyDTO>> {
    private static Logger _logger = LogFactory.getLogger(SearchTask.class);
    private static SearchDAO _dao = new SearchDAO();
    private String _searchText = null;

    public SearchTask(String searchText) {
        _searchText = searchText;
    }

    @Override
    protected AsyncTaskResult<List<StormKeyDTO>> doInBackground(Void... voids) {
        _logger.info("Performing search...");
        try {
            return new AsyncTaskResult<List<StormKeyDTO>>(_dao.search(_searchText));
        } catch (Throwable t) {
            return new AsyncTaskResult<List<StormKeyDTO>>(t);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<StormKeyDTO>> result) {
        _logger.info("...search performed.");
        fireCompleted(result);
    }
}
