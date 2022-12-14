package com.laba.user.data.network.model;

import com.google.gson.annotations.SerializedName;

public class InitSettingsResponse{

	@SerializedName("version_ios_user")
	private String versionIosUser;

	@SerializedName("broadcast_request")
	private String broadcastRequest;

	@SerializedName("fb_app_version")
	private String fbAppVersion;

	@SerializedName("surge_percentage")
	private String surgePercentage;

	@SerializedName("track_distance")
	private String trackDistance;

	@SerializedName("demo_mode")
	private String demoMode;

	@SerializedName("CARD")
	private String cARD;

	@SerializedName("site_logo")
	private String siteLogo;

	@SerializedName("daily_target")
	private String dailyTarget;

	@SerializedName("fb_app_id")
	private String fbAppId;

	@SerializedName("site_email_logo")
	private String siteEmailLogo;

	@SerializedName("manual_request")
	private String manualRequest;

	@SerializedName("default_lang")
	private String defaultLang;

	@SerializedName("scheduled_cancel_time_exceed")
	private String scheduledCancelTimeExceed;

	@SerializedName("price_per_kilometer")
	private String pricePerKilometer;

	@SerializedName("map_key")
	private String mapKey;

	@SerializedName("referral_amount")
	private String referralAmount;

	@SerializedName("provider_commission_percentage")
	private String providerCommissionPercentage;

	@SerializedName("per_page")
	private String perPage;

	@SerializedName("commission_percentage")
	private String commissionPercentage;

	@SerializedName("send_email")
	private String sendEmail;

	@SerializedName("CASH")
	private String cASH;

	@SerializedName("referral_count")
	private String referralCount;

	@SerializedName("social_login")
	private String socialLogin;

	@SerializedName("fleet_commission_percentage")
	private String fleetCommissionPercentage;

	@SerializedName("fb_app_secret")
	private String fbAppSecret;

	@SerializedName("provider_select_timeout")
	private String providerSelectTimeout;

	@SerializedName("store_twitter_link")
	private String storeTwitterLink;

	@SerializedName("site_icon")
	private String siteIcon;

	@SerializedName("booking_prefix")
	private String bookingPrefix;

	@SerializedName("distance")
	private String distance;

	@SerializedName("stripe_publishable_key")
	private String stripePublishableKey;

	@SerializedName("store_facebook_link")
	private String storeFacebookLink;

	@SerializedName("contact_email")
	private String contactEmail;

	@SerializedName("site_copyright")
	private String siteCopyright;

	@SerializedName("version_android_provider")
	private String versionAndroidProvider;

	@SerializedName("base_price")
	private String basePrice;

	@SerializedName("currency")
	private String currency;

	@SerializedName("price_per_minute")
	private String pricePerMinute;

	@SerializedName("stripe_secret_key")
	private String stripeSecretKey;

	@SerializedName("sos_number")
	private String sosNumber;

	@SerializedName("provider_search_radius")
	private String providerSearchRadius;

	@SerializedName("version_ios_provider")
	private String versionIosProvider;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("store_link_ios_provider")
	private String storeLinkIosProvider;

	@SerializedName("referral")
	private String referral;

	@SerializedName("surge_trigger")
	private String surgeTrigger;

	@SerializedName("tax_percentage")
	private String taxPercentage;

	@SerializedName("store_link_android_provider")
	private String storeLinkAndroidProvider;

	@SerializedName("store_link_ios_user")
	private String storeLinkIosUser;

	@SerializedName("site_title")
	private String siteTitle;

	@SerializedName("version_android_user")
	private String versionAndroidUser;

	@SerializedName("store_link_android_user")
	private String storeLinkAndroidUser;

	public void setVersionIosUser(String versionIosUser){
		this.versionIosUser = versionIosUser;
	}

	public String getVersionIosUser(){
		return versionIosUser;
	}

	public void setBroadcastRequest(String broadcastRequest){
		this.broadcastRequest = broadcastRequest;
	}

	public String getBroadcastRequest(){
		return broadcastRequest;
	}

	public void setFbAppVersion(String fbAppVersion){
		this.fbAppVersion = fbAppVersion;
	}

	public String getFbAppVersion(){
		return fbAppVersion;
	}

	public void setSurgePercentage(String surgePercentage){
		this.surgePercentage = surgePercentage;
	}

	public String getSurgePercentage(){
		return surgePercentage;
	}

	public void setTrackDistance(String trackDistance){
		this.trackDistance = trackDistance;
	}

	public String getTrackDistance(){
		return trackDistance;
	}

	public void setDemoMode(String demoMode){
		this.demoMode = demoMode;
	}

	public String getDemoMode(){
		return demoMode;
	}

	public void setCARD(String cARD){
		this.cARD = cARD;
	}

	public String getCARD(){
		return cARD;
	}

	public void setSiteLogo(String siteLogo){
		this.siteLogo = siteLogo;
	}

	public String getSiteLogo(){
		return siteLogo;
	}

	public void setDailyTarget(String dailyTarget){
		this.dailyTarget = dailyTarget;
	}

	public String getDailyTarget(){
		return dailyTarget;
	}

	public void setFbAppId(String fbAppId){
		this.fbAppId = fbAppId;
	}

	public String getFbAppId(){
		return fbAppId;
	}

	public void setSiteEmailLogo(String siteEmailLogo){
		this.siteEmailLogo = siteEmailLogo;
	}

	public String getSiteEmailLogo(){
		return siteEmailLogo;
	}

	public void setManualRequest(String manualRequest){
		this.manualRequest = manualRequest;
	}

	public String getManualRequest(){
		return manualRequest;
	}

	public void setDefaultLang(String defaultLang){
		this.defaultLang = defaultLang;
	}

	public String getDefaultLang(){
		return defaultLang;
	}

	public void setScheduledCancelTimeExceed(String scheduledCancelTimeExceed){
		this.scheduledCancelTimeExceed = scheduledCancelTimeExceed;
	}

	public String getScheduledCancelTimeExceed(){
		return scheduledCancelTimeExceed;
	}

	public void setPricePerKilometer(String pricePerKilometer){
		this.pricePerKilometer = pricePerKilometer;
	}

	public String getPricePerKilometer(){
		return pricePerKilometer;
	}

	public void setMapKey(String mapKey){
		this.mapKey = mapKey;
	}

	public String getMapKey(){
		return mapKey;
	}

	public void setReferralAmount(String referralAmount){
		this.referralAmount = referralAmount;
	}

	public String getReferralAmount(){
		return referralAmount;
	}

	public void setProviderCommissionPercentage(String providerCommissionPercentage){
		this.providerCommissionPercentage = providerCommissionPercentage;
	}

	public String getProviderCommissionPercentage(){
		return providerCommissionPercentage;
	}

	public void setPerPage(String perPage){
		this.perPage = perPage;
	}

	public String getPerPage(){
		return perPage;
	}

	public void setCommissionPercentage(String commissionPercentage){
		this.commissionPercentage = commissionPercentage;
	}

	public String getCommissionPercentage(){
		return commissionPercentage;
	}

	public void setSendEmail(String sendEmail){
		this.sendEmail = sendEmail;
	}

	public String getSendEmail(){
		return sendEmail;
	}

	public void setCASH(String cASH){
		this.cASH = cASH;
	}

	public String getCASH(){
		return cASH;
	}

	public void setReferralCount(String referralCount){
		this.referralCount = referralCount;
	}

	public String getReferralCount(){
		return referralCount;
	}

	public void setSocialLogin(String socialLogin){
		this.socialLogin = socialLogin;
	}

	public String getSocialLogin(){
		return socialLogin;
	}

	public void setFleetCommissionPercentage(String fleetCommissionPercentage){
		this.fleetCommissionPercentage = fleetCommissionPercentage;
	}

	public String getFleetCommissionPercentage(){
		return fleetCommissionPercentage;
	}

	public void setFbAppSecret(String fbAppSecret){
		this.fbAppSecret = fbAppSecret;
	}

	public String getFbAppSecret(){
		return fbAppSecret;
	}

	public void setProviderSelectTimeout(String providerSelectTimeout){
		this.providerSelectTimeout = providerSelectTimeout;
	}

	public String getProviderSelectTimeout(){
		return providerSelectTimeout;
	}

	public void setStoreTwitterLink(String storeTwitterLink){
		this.storeTwitterLink = storeTwitterLink;
	}

	public String getStoreTwitterLink(){
		return storeTwitterLink;
	}

	public void setSiteIcon(String siteIcon){
		this.siteIcon = siteIcon;
	}

	public String getSiteIcon(){
		return siteIcon;
	}

	public void setBookingPrefix(String bookingPrefix){
		this.bookingPrefix = bookingPrefix;
	}

	public String getBookingPrefix(){
		return bookingPrefix;
	}

	public void setDistance(String distance){
		this.distance = distance;
	}

	public String getDistance(){
		return distance;
	}

	public void setStripePublishableKey(String stripePublishableKey){
		this.stripePublishableKey = stripePublishableKey;
	}

	public String getStripePublishableKey(){
		return stripePublishableKey;
	}

	public void setStoreFacebookLink(String storeFacebookLink){
		this.storeFacebookLink = storeFacebookLink;
	}

	public String getStoreFacebookLink(){
		return storeFacebookLink;
	}

	public void setContactEmail(String contactEmail){
		this.contactEmail = contactEmail;
	}

	public String getContactEmail(){
		return contactEmail;
	}

	public void setSiteCopyright(String siteCopyright){
		this.siteCopyright = siteCopyright;
	}

	public String getSiteCopyright(){
		return siteCopyright;
	}

	public void setVersionAndroidProvider(String versionAndroidProvider){
		this.versionAndroidProvider = versionAndroidProvider;
	}

	public String getVersionAndroidProvider(){
		return versionAndroidProvider;
	}

	public void setBasePrice(String basePrice){
		this.basePrice = basePrice;
	}

	public String getBasePrice(){
		return basePrice;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setPricePerMinute(String pricePerMinute){
		this.pricePerMinute = pricePerMinute;
	}

	public String getPricePerMinute(){
		return pricePerMinute;
	}

	public void setStripeSecretKey(String stripeSecretKey){
		this.stripeSecretKey = stripeSecretKey;
	}

	public String getStripeSecretKey(){
		return stripeSecretKey;
	}

	public void setSosNumber(String sosNumber){
		this.sosNumber = sosNumber;
	}

	public String getSosNumber(){
		return sosNumber;
	}

	public void setProviderSearchRadius(String providerSearchRadius){
		this.providerSearchRadius = providerSearchRadius;
	}

	public String getProviderSearchRadius(){
		return providerSearchRadius;
	}

	public void setVersionIosProvider(String versionIosProvider){
		this.versionIosProvider = versionIosProvider;
	}

	public String getVersionIosProvider(){
		return versionIosProvider;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setStoreLinkIosProvider(String storeLinkIosProvider){
		this.storeLinkIosProvider = storeLinkIosProvider;
	}

	public String getStoreLinkIosProvider(){
		return storeLinkIosProvider;
	}

	public void setReferral(String referral){
		this.referral = referral;
	}

	public String getReferral(){
		return referral;
	}

	public void setSurgeTrigger(String surgeTrigger){
		this.surgeTrigger = surgeTrigger;
	}

	public String getSurgeTrigger(){
		return surgeTrigger;
	}

	public void setTaxPercentage(String taxPercentage){
		this.taxPercentage = taxPercentage;
	}

	public String getTaxPercentage(){
		return taxPercentage;
	}

	public void setStoreLinkAndroidProvider(String storeLinkAndroidProvider){
		this.storeLinkAndroidProvider = storeLinkAndroidProvider;
	}

	public String getStoreLinkAndroidProvider(){
		return storeLinkAndroidProvider;
	}

	public void setStoreLinkIosUser(String storeLinkIosUser){
		this.storeLinkIosUser = storeLinkIosUser;
	}

	public String getStoreLinkIosUser(){
		return storeLinkIosUser;
	}

	public void setSiteTitle(String siteTitle){
		this.siteTitle = siteTitle;
	}

	public String getSiteTitle(){
		return siteTitle;
	}

	public void setVersionAndroidUser(String versionAndroidUser){
		this.versionAndroidUser = versionAndroidUser;
	}

	public String getVersionAndroidUser(){
		return versionAndroidUser;
	}

	public void setStoreLinkAndroidUser(String storeLinkAndroidUser){
		this.storeLinkAndroidUser = storeLinkAndroidUser;
	}

	public String getStoreLinkAndroidUser(){
		return storeLinkAndroidUser;
	}
}