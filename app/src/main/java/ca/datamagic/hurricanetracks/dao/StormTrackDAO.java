package ca.datamagic.hurricanetracks.dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.StormTrackDTO;

public class StormTrackDAO extends BaseDAO {
    public List<StormTrackDTO> tracks(String basin, Integer year, Integer stormNo) throws IOException, JSONException {
        String json = get(MessageFormat.format("http://datamagic.ca/Hurricane/api/storm/{0}/{1}/{2}", basin, Integer.toString(year.intValue()), Integer.toString(stormNo.intValue())));
        JSONArray array = new JSONArray(json);
        List<StormTrackDTO> stormTracks = new ArrayList<StormTrackDTO>();
        for (int ii = 0; ii < array.length(); ii++) {
            stormTracks.add(new StormTrackDTO(array.getJSONObject(ii)));
        }
        return stormTracks;
    }
}
