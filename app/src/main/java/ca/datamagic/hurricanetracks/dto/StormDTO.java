package ca.datamagic.hurricanetracks.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class StormDTO {
    private Integer _stormNo = null;
    private String _stormName = null;
    private Integer _tracks = null;

    public StormDTO() {

    }

    public StormDTO(Integer stormNo, String stormName, Integer tracks) {
        _stormNo = stormNo;
        _stormName = stormName;
        _tracks = tracks;
    }

    public StormDTO(JSONObject obj) throws JSONException {
        _stormNo = obj.getInt("stormNo");
        _stormName = obj.getString("stormName");
        _tracks = obj.getInt("tracks");
    }

    public Integer getStormNo() {
        return _stormNo;
    }

    public String getStormName() {
        return _stormName;
    }

    public Integer getTracks() {
        return _tracks;
    }

    public void setStormNo(Integer newVal) {
        _stormNo = newVal;
    }

    public void setStormName(String newVal) {
        _stormName = newVal;
    }

    public void setTracks(Integer newVal) {
        _tracks = newVal;
    }
}
