package ca.datamagic.hurricanetracks.dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.BasinDTO;

public class BasinDAO extends BaseDAO {
    public List<BasinDTO> basins() throws IOException, JSONException {
        String json = get("http://datamagic.ca/Hurricane/api/basins");
        JSONArray array = new JSONArray(json);
        List<BasinDTO> basins = new ArrayList<BasinDTO>();
        for (int ii = 0; ii < array.length(); ii++) {
            basins.add(new BasinDTO(array.getJSONObject(ii)));
        }
        return basins;
    }

    public List<Integer> years(String basin) throws IOException, JSONException {
        String json = get(MessageFormat.format("http://datamagic.ca/Hurricane/api/basin/{0}/years", basin));
        JSONArray array = new JSONArray(json);
        List<Integer> years = new ArrayList<Integer>();
        for (int ii = 0; ii < array.length(); ii++) {
            years.add(new Integer(array.getInt(ii)));
        }
        return years;
    }
}
