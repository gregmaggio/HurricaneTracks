package ca.datamagic.hurricanetracks.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class StormTrackDTO {
    private Integer _stormNo = null;
    private String _stormName = null;
    private Integer _trackNo = null;
    private Integer _year = null;
    private Integer _month = null;
    private Integer _day = null;
    private Integer _hours = null;
    private Integer _minutes = null;
    private String _recordIdentifier = null;
    private Double _latitude = null;
    private String _latitudeHemisphere = null;
    private Double _longitude = null;
    private String _longitudeHemisphere = null;
    private Double _maxWindSpeed = null;
    private Double _minPressure = null;
    private Double _x = null;
    private Double _y = null;
    private String _status = null;
    private Integer _category = null;

    public StormTrackDTO() {

    }

    public StormTrackDTO(JSONObject obj) throws JSONException {
        _stormNo = obj.getInt("stormNo");
        _stormName = obj.getString("stormName");
        _trackNo = obj.getInt("trackNo");
        _year = obj.getInt("year");
        _month = obj.getInt("month");
        _day = obj.getInt("day");
        _hours = obj.getInt("hours");
        _minutes = obj.getInt("minutes");
        _recordIdentifier = obj.getString("recordIdentifier");
        _latitude = obj.getDouble("latitude");
        _latitudeHemisphere = obj.getString("latitudeHemisphere");
        _longitude = obj.getDouble("longitude");
        _longitudeHemisphere = obj.getString("longitudeHemisphere");
        _maxWindSpeed = obj.getDouble("maxWindSpeed");
        _minPressure = obj.getDouble("minPressure");
        _x = obj.getDouble("x");
        _y = obj.getDouble("y");
        _status = obj.getString("status");
        if (_maxWindSpeed != null) {
            if ((_maxWindSpeed.intValue() >= 74) && (_maxWindSpeed.intValue() <= 95)) {
                _category = 1;
            } else if ((_maxWindSpeed.intValue() >= 96) && (_maxWindSpeed.intValue() <= 110)) {
                _category = 2;
            } else if ((_maxWindSpeed.intValue() >= 111) && (_maxWindSpeed.intValue() <= 129)) {
                _category = 3;
            } else if ((_maxWindSpeed.intValue() >= 130) && (_maxWindSpeed.intValue() <= 156)) {
                _category = 4;
            } else if (_maxWindSpeed.intValue() >= 157) {
                _category = 5;
            }
        }
    }

    public Integer getStormNo() {
        return _stormNo;
    }

    public String getStormName() {
        return _stormName;
    }

    public Integer getTrackNo() {
        return _trackNo;
    }

    public Integer getYear() {
        return _year;
    }

    public Integer getMonth() {
        return _month;
    }

    public Integer getDay() {
        return _day;
    }

    public Integer getHours() {
        return _hours;
    }

    public Integer getMinutes() {
        return _minutes;
    }

    public String getRecordIdentifier() {
        return _recordIdentifier;
    }

    public Double getLatitude() {
        return _latitude;
    }

    public String getLatitudeHemisphere() {
        return _latitudeHemisphere;
    }

    public Double getLongitude() {
        return _longitude;
    }

    public String getLongitudeHemisphere() {
        return _longitudeHemisphere;
    }

    public Double getMaxWindSpeed() {
        return _maxWindSpeed;
    }

    public Double getMinPressure() {
        return _minPressure;
    }

    public Double getX() {
        return _x;
    }

    public Double getY() {
        return _y;
    }

    public String getStatus() {
        return _status;
    }

    public Integer getCategory() {
        return _category;
    }

    public void setStormNo(Integer newVal) {
        _stormNo = newVal;
    }

    public void setStormName(String newVal) {
        _stormName = newVal;
    }

    public void setTrackNo(Integer newVal) {
        _trackNo = newVal;
    }

    public void setYear(Integer newVal) {
        _year = newVal;
    }

    public void setMonth(Integer newVal) {
        _month = newVal;
    }

    public void setDay(Integer newVal) {
        _day = newVal;
    }

    public void setHours(Integer newVal) {
        _hours = newVal;
    }

    public void setMinutes(Integer newVal) {
        _minutes = newVal;
    }

    public void setRecordIdentifier(String newVal) {
        _recordIdentifier = newVal;
    }

    public void setLatitude(Double newVal) {
        _latitude = newVal;
    }

    public void setLatitudeHemisphere(String newVal) {
        _latitudeHemisphere = newVal;
    }

    public void setLongitude(Double newVal) {
        _longitude = newVal;
    }

    public void setLongitudeHemisphere(String newVal) {
        _longitudeHemisphere = newVal;
    }

    public void setMaxWindSpeed(Double newVal) {
        _maxWindSpeed = newVal;
    }

    public void setMinPressure(Double newVal) {
        _minPressure = newVal;
    }

    public void setX(Double newVal) {
        _x = newVal;
    }

    public void setY(Double newVal) {
        _y = newVal;
    }

    public void setStatus(String newVal) {
        _status = newVal;
    }

    public void setCategory(Integer newVal) {
        _category = newVal;
    }
}
