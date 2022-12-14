package com.laba.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataResponse {

    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<>();

    public DataResponse() {
    }

    @SerializedName("sos")
    @Expose
    private String sos;

    @SerializedName("cash")
    @Expose
    private int cash;

    @SerializedName("card")
    @Expose
    private int card;

    @SerializedName("currency")
    @Expose
    private String currency;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
