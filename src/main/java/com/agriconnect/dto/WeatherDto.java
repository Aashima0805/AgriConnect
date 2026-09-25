package com.agriconnect.dto;

public class WeatherDto {

    private String status; // "success" or "error"
    private String message;
    private String location;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Double windSpeed;
    private String condition;
    private String description;
    private Integer pressure;
    private String advisory;

    public WeatherDto() {
    }

    public static WeatherDto success(String location, Double temperature, Double feelsLike, Integer humidity, Double windSpeed, String condition, String description, Integer pressure, String advisory) {
        WeatherDto dto = new WeatherDto();
        dto.setStatus("success");
        dto.setLocation(location);
        dto.setTemperature(temperature);
        dto.setFeelsLike(feelsLike);
        dto.setHumidity(humidity);
        dto.setWindSpeed(windSpeed);
        dto.setCondition(condition);
        dto.setDescription(description);
        dto.setPressure(pressure);
        dto.setAdvisory(advisory);
        return dto;
    }

    public static WeatherDto error(String message) {
        WeatherDto dto = new WeatherDto();
        dto.setStatus("error");
        dto.setMessage(message);
        return dto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public String getAdvisory() {
        return advisory;
    }

    public void setAdvisory(String advisory) {
        this.advisory = advisory;
    }
}
