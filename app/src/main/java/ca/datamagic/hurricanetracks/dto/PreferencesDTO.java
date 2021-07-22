package ca.datamagic.hurricanetracks.dto;

import java.util.Calendar;

public class PreferencesDTO {
    private String basin = "NA";
    private Integer year = Calendar.getInstance().get(Calendar.YEAR);
    private Integer stormNo = null;

    public String getBasin() {
        return this.basin;
    }

    public void setBasin(String newVal) {
        this.basin = newVal;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer newVal) {
        this.year = newVal;
    }

    public Integer getStormNo() {
        return this.stormNo;
    }

    public void setStormNo(Integer newVal) {
        this.stormNo = newVal;
    }
}
