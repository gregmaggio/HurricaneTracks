package ca.datamagic.hurricanetracks.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class StormKeyDTO {
    private String _stormKey = null;
    private String _basin = null;
    private Integer _year = null;
    private Integer _stormNo = null;
    private String _stormName = null;

    public StormKeyDTO() {

    }

    public StormKeyDTO(JSONObject obj) throws JSONException {
        _stormKey = obj.getString("stormKey");
        _basin = obj.getString("basin");
        _year = obj.getInt("year");
        _stormNo = obj.getInt("stormNo");
        _stormName = obj.getString("stormName");
    }

    public String getStormKey() {
        return _stormKey;
    }

    public String getBasin() {
        return _basin;
    }

    public Integer getYear() {
        return _year;
    }

    public Integer getStormNo() {
        return _stormNo;
    }

    public String getStormName() {
        return _stormName;
    }

    public void setStormKey(String newVal) {
        _stormKey = newVal;
    }

    public void setBasin(String newVal) {
        _basin = newVal;
    }

    public void setYear(Integer newVal) {
        _year = newVal;
    }

    public void setStormNo(Integer newVal) {
        _stormNo = newVal;
    }

    public void setStormName(String newVal) {
        _stormName = newVal;
    }
}
