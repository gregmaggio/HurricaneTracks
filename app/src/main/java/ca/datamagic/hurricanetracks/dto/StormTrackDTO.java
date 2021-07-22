package ca.datamagic.hurricanetracks.dto;

public class StormTrackDTO {
    private Integer stormNo = null;
    private String stormName = null;
    private Integer trackNo = null;
    private Integer year = null;
    private Integer month = null;
    private Integer day = null;
    private Integer hours = null;
    private Integer minutes = null;
    private Integer seconds = null;
    private Double latitude = null;
    private Double longitude = null;
    private Double maxWindSpeed = null;
    private Double minPressure = null;
    private String status = null;
    private Integer category = null;

    public StormTrackDTO() {

    }

    public StormTrackDTO(Integer stormNo, String stormName, Integer trackNo, Integer year, Integer month, Integer day, Integer hours, Integer minutes, Integer seconds, Double latitude, Double longitude, Double maxWindSpeed, Double minPressure, String status) {
        this.stormNo = stormNo;
        this.stormName = stormName;
        this.trackNo = trackNo;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxWindSpeed = maxWindSpeed;
        this.minPressure = minPressure;
        this.status = status;
        if (this.maxWindSpeed != null) {
            if ((this.maxWindSpeed.intValue() >= 74) && (this.maxWindSpeed.intValue() <= 95)) {
                this.category = 1;
            } else if ((this.maxWindSpeed.intValue() >= 96) && (this.maxWindSpeed.intValue() <= 110)) {
                this.category = 2;
            } else if ((this.maxWindSpeed.intValue() >= 111) && (this.maxWindSpeed.intValue() <= 129)) {
                this.category = 3;
            } else if ((this.maxWindSpeed.intValue() >= 130) && (this.maxWindSpeed.intValue() <= 156)) {
                this.category = 4;
            } else if (this.maxWindSpeed.intValue() >= 157) {
                this.category = 5;
            }
        }
    }

    public Integer getStormNo() {
        return this.stormNo;
    }

    public String getStormName() {
        return this.stormName;
    }

    public Integer getTrackNo() {
        return this.trackNo;
    }

    public Integer getYear() {
        return this.year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public Integer getDay() {
        return this.day;
    }

    public Integer getHours() {
        return this.hours;
    }

    public Integer getMinutes() {
        return this.minutes;
    }

    public Integer getSeconds() {
        return this.seconds;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getMaxWindSpeed() {
        return this.maxWindSpeed;
    }

    public Double getMinPressure() {
        return this.minPressure;
    }

    public String getStatus() {
        return this.status;
    }

    public Integer getCategory() {
        return this.category;
    }

    public void setStormNo(Integer newVal) {
        this.stormNo = newVal;
    }

    public void setStormName(String newVal) {
        this.stormName = newVal;
    }

    public void setTrackNo(Integer newVal) {
        this.trackNo = newVal;
    }

    public void setYear(Integer newVal) {
        this.year = newVal;
    }

    public void setMonth(Integer newVal) {
        this.month = newVal;
    }

    public void setDay(Integer newVal) {
        this.day = newVal;
    }

    public void setHours(Integer newVal) {
        this.hours = newVal;
    }

    public void setMinutes(Integer newVal) {
        this.minutes = newVal;
    }

    public void setSeconds(Integer newVal) {
        this.seconds = newVal;
    }

    public void setLatitude(Double newVal) {
        this.latitude = newVal;
    }

    public void setLongitude(Double newVal) {
        this.longitude = newVal;
    }

    public void setMaxWindSpeed(Double newVal) {
        this.maxWindSpeed = newVal;
    }

    public void setMinPressure(Double newVal) {
        this.minPressure = newVal;
    }

    public void setStatus(String newVal) {
        this.status = newVal;
    }

    public void setCategory(Integer newVal) {
        this.category = newVal;
    }
}
