package ca.datamagic.hurricanetracks.dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormKeyDTO;

public class SearchDAO extends BaseDAO {
    public List<StormKeyDTO> search(String searchText) throws IOException, JSONException {
        String json = get(MessageFormat.format("http://datamagic.ca/Hurricane/api/search/{0}", searchText));
        JSONArray array = new JSONArray(json);
        List<StormKeyDTO> stormKeys = new ArrayList<StormKeyDTO>();
        for (int ii = 0; ii < array.length(); ii++) {
            stormKeys.add(new StormKeyDTO(array.getJSONObject(ii)));
        }
        return stormKeys;
    }
}
