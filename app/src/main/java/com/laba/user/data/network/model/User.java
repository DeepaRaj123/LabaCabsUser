package com.laba.user.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("login_by")
    @Expose
    private String loginBy;
    @SerializedName("social_unique_id")
    @Expose
    private String socialUniqueId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("stripe_cust_id")
    @Expose
    private String stripeCustId;
    @SerializedName("wallet_balance")
    @Expose
    private Double walletBalance;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("sos")
    @Expose
    private String sos;
    @SerializedName("app_contact")
    @Expose
    private String appContact;
    @SerializedName("measurement")
    @Expose
    private String measurement;
    @SerializedName("stripe_secret_key")
    @Expose
    private String stripeSecretKey;
    @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLoginBy() {
        return loginBy;
    }

    public void setLoginBy(String loginBy) {
        this.loginBy = loginBy;
    }

    public String getSocialUniqueId() {
        return socialUniqueId;
    }

    public void setSocialUniqueId(String socialUniqueId) {
        this.socialUniqueId = socialUniqueId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStripeCustId() {
        return stripeCustId;
    }

    public void setStripeCustId(String stripeCustId) {
        this.stripeCustId = stripeCustId;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public String getAppContact() {
        return appContact;
    }

    public void setAppContact(String appContact) {
        this.appContact = appContact;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getStripeSecretKey() {
        return stripeSecretKey;
    }

    public void setStripeSecretKey(String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }
}
