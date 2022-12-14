package com.laba.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 27-06-2018.
 */
public class AddressResponse {
    @SerializedName("home")
    @Expose
    private List<Address> home = null;
    @SerializedName("work")
    @Expose
    private List<Address> work = null;
    @SerializedName("others")
    @Expose
    private List<Address> others = null;
    @SerializedName("recent")
    @Expose
    private List<Address> recent = null;

    public List<Address> getHome() {
        return home;
    }

    public void setHome(List<Address> home) {
        this.home = home;
    }

    public List<Address> getWork() {
        return work;
    }

    public void setWork(List<Address> work) {
        this.work = work;
    }

    public List<Address> getOthers() {
        return others;
    }

    public void setOthers(List<Address> others) {
        this.others = others;
    }

    public List<Address> getRecent() {
        return recent;
    }

    public void setRecent(List<Address> recent) {
        this.recent = recent;
    }

}
