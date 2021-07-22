package ca.datamagic.hurricanetracks.dto;

public class BasinDTO {
    private String name = null;
    private String description = null;
    private Double centerX = null;
    private Double centerY = null;

    public BasinDTO() {

    }

    public BasinDTO(String name, String description, Double centerX, Double centerY) {
        this.name = name;
        this.description = description;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Double getCenterX() {
        return this.centerX;
    }

    public Double getCenterY() {
        return this.centerY;
    }

    public void setName(String newVal) {
        this.name = newVal;
    }

    public void setDescription(String newVal) {
        this.description = newVal;
    }

    public void setCenterX(Double newVal) {
        this.centerX = newVal;
    }

    public void setCenterY(Double newVal) {
        this.centerY = newVal;
    }
}
