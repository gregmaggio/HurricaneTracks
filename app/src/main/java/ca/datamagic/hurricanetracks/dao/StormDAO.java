package ca.datamagic.hurricanetracks.dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormDTO;

public class StormDAO extends BaseDAO {
    public List<StormDTO> storms(String basin, Integer year) throws IOException, JSONException {
        String json = get(MessageFormat.format("https://datamagic.ca/Hurricane/api/storm/{0}/{1}", basin, Integer.toString(year.intValue())));
        JSONArray array = new JSONArray(json);
        List<StormDTO> storms = new ArrayList<StormDTO>();
        for (int ii = 0; ii < array.length(); ii++) {
            storms.add(new StormDTO(array.getJSONObject(ii)));
        }
        return storms;
    }
}
