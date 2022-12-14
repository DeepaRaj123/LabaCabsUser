package com.laba.user.data.network.model;

/**
 * Created by Esack N on 1/24/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coupon {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("promocode_id")
    @Expose
    private Integer promocodeId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("promocode")
    @Expose
    private Promocode promocode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Integer promocodeId) {
        this.promocodeId = promocodeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Promocode getPromocode() {
        return promocode;
    }

    public void setPromocode(Promocode promocode) {
        this.promocode = promocode;
    }

}

