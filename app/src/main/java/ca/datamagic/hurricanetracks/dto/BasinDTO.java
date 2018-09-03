package ca.datamagic.hurricanetracks.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class BasinDTO {
    private String _name = null;
    private String _description = null;
    private Double _centerX = null;
    private Double _centerY = null;

    public BasinDTO() {

    }

    public BasinDTO(JSONObject obj) throws JSONException {
        _name = obj.getString("name");
        _description = obj.getString("description");
        _centerX = obj.getDouble("centerX");
        _centerY = obj.getDouble("centerY");
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public Double getCenterX() {
        return _centerX;
    }

    public Double getCenterY() {
        return _centerY;
    }

    public void setName(String newVal) {
        _name = newVal;
    }

    public void setDescription(String newVal) {
        _description = newVal;
    }

    public void setCenterX(Double newVal) {
        _centerX = newVal;
    }

    public void setCenterY(Double newVal) {
        _centerY = newVal;
    }
}
