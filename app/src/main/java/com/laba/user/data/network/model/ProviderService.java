package com.laba.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 07-06-2018.
 */
public class ProviderService {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("provider_id")
    @Expose
    private Integer providerId;
    @SerializedName("service_type_id")
    @Expose
    private Integer serviceTypeId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("service_number")
    @Expose
    private String serviceNumber;
    @SerializedName("service_model")
    @Expose
    private String serviceModel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getServiceModel() {
        return serviceModel;
    }

    public void setServiceModel(String serviceModel) {
        this.serviceModel = serviceModel;
    }

}
