package com.laba.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Service implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("provider_name")
    @Expose
    private String providerName;

//    @SerializedName("description")
//    @Expose
//    private String description;

    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("fixed")
    @Expose
    private Double fixed;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("minute")
    @Expose
    private Integer minute;
    @SerializedName("hour")
    @Expose
    private String hour;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("calculator")
    @Expose
    private String calculator;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("available")
    @Expose
    private int available;

//    public boolean testAvailable = new Random().nextBoolean();

    public boolean isAvailable() {
        return available > 0;
//        return testAvailable;
    }

    transient
    private String estimatedFare;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getFixed() {
        return fixed;
    }

    public void setFixed(Double fixed) {
        this.fixed = fixed;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getCalculator() {
        return calculator;
    }

    public void setCalculator(String calculator) {
        this.calculator = calculator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEstimatedTime() {
        return estimatedFare;
    }

    public void setEstimatedTime(String estimatedFare) {
        this.estimatedFare = estimatedFare;
    }
}
