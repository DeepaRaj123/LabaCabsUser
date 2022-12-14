package com.laba.user.data.network;

import com.laba.user.data.network.model.AddWallet;
import com.laba.user.data.network.model.AddressResponse;
import com.laba.user.data.network.model.Card;
import com.laba.user.data.network.model.Coupon;
import com.laba.user.data.network.model.DataResponse;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.EstimateFare;
import com.laba.user.data.network.model.ForgotResponse;
import com.laba.user.data.network.model.Help;
import com.laba.user.data.network.model.InitSettingsResponse;
import com.laba.user.data.network.model.Message;
import com.laba.user.data.network.model.MyOTP;
import com.laba.user.data.network.model.PromoResponse;
import com.laba.user.data.network.model.Provider;
import com.laba.user.data.network.model.RegisterResponse;
import com.laba.user.data.network.model.Rental;
import com.laba.user.data.network.model.Service;
import com.laba.user.data.network.model.Token;
import com.laba.user.data.network.model.User;
import com.laba.user.data.network.model.WalletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("initsetup")
    Observable<InitSettingsResponse> initSettings();

    @FormUrlEncoded
    @POST("api/user/signup")
    Observable<RegisterResponse> register(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/auth/google")
    Observable<Token> loginGoogle(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/auth/facebook")
    Observable<Token> loginFacebook(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/oauth/token")
    Observable<Token> login(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/oauth/token")
    Observable<Token> refreshLogin(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/forgot/password")
    Observable<ForgotResponse> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("api/user/reset/password")
    Observable<Object> resetPassword(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/verify")
    Observable<Object> verifyEmail(@Field("email") String email);

    @GET("api/user/details")
    Observable<User> profile();

    @FormUrlEncoded
    @POST("api/user/logout")
    Observable<Object> logout(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/user/change/password")
    Observable<Object> changePassword(@FieldMap HashMap<String, Object> params);

    @GET("api/user/request/check")
    Observable<DataResponse> checkStatus();

    @GET("api/user/show/providers")
    Observable<List<Provider>> providers(@QueryMap HashMap<String, Object> params);

    @Multipart
    @POST("api/user/update/profile")
    Observable<User> profile(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

//    @FormUrlEncoded
//    @POST("api/user/update/profile")
//    Observable<User> profile(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @GET("api/user/services")
    Observable<List<Service>> services(@QueryMap HashMap<String, Object> params);

    @GET("api/user/rental/logic")
    Observable<List<Rental>> rentals();

    @GET("api/user/estimated/fare")
    Call<EstimateFare> estimateFare(@QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/send/request")
    Observable<Object> sendRequest(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/cancel/request")
    Observable<Object> cancelRequest(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("/api/user/update/request")
    Observable<Object> updateRequest(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/payment")
    Observable<Message> payment(@Field("request_id") Integer params, @Field("tips") Double tips);

    @FormUrlEncoded
    @POST("api/user/rate/provider")
    Observable<Object> rate(@FieldMap HashMap<String, Object> params);

    @GET("api/user/trips")
    Observable<List<Datum>> pastTrip();

    @GET("api/user/trip/details")
    Observable<List<Datum>> pastTripDetails(@Query("request_id") Integer requestId);

    @GET("api/user/upcoming/trips")
    Observable<List<Datum>> upcomingTrip();

    @GET("api/user/upcoming/trip/details")
    Observable<List<Datum>> upcomingTripDetails(@Query("request_id") Integer requestId);

    @FormUrlEncoded
    @POST("api/user/cancel/request")
    Observable<Object> dispute(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/card")
    Observable<Object> card(@Field("stripe_token") String stripeToken);

    @GET("api/user/card")
    Observable<List<Card>> card();

    @FormUrlEncoded
    @POST("api/user/card/destroy")
    Observable<Object> deleteCard(@Field("card_id") String cardId, @Field("_method") String method);

    @FormUrlEncoded
    @POST("api/user/add/money")
    Observable<AddWallet> addMoney(@FieldMap HashMap<String, Object> params);

    @GET("api/user/help")
    Observable<Help> help();

    @FormUrlEncoded
    @POST("api/user/promocode/add")
    Observable<Object> coupon(@Field("promocode") String promoCode);

    @GET("api/user/promocodes")
    Observable<List<Coupon>> coupon();

    @FormUrlEncoded
    @POST("api/user/location")
    Observable<Object> addAddress(@FieldMap HashMap<String, Object> params);

    @GET("api/user/location")
    Observable<AddressResponse> address();

    @DELETE("api/user/location" + "/" + "{id}")
    Observable<Object> deleteAddress(@Path("id") Integer id);

    @GET("api/user/wallet/passbook")
    Observable<WalletResponse> wallet();

    @GET("api/user/promocodes_list")
    Observable<PromoResponse> promocodesList();

    @FormUrlEncoded
    @POST("/api/user/chat")
    Observable<Object> postChatItem(
            @Field("sender") String sender,
            @Field("user_id") String user_id,
            @Field("message") String message);

    @FormUrlEncoded
    @POST("/api/user/update/language")
    Observable<Object> postChangeLanguage(@Field("language") String language);

    @FormUrlEncoded
    @POST("api/user/otp")
    Observable<MyOTP> sendOtp(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("api/user/add/razor/money")
    Observable<AddWallet> addWalletMoney(@Field("payment_id") String mId);

    @FormUrlEncoded
    @POST("api/user/payment/rzp")
    Observable<AddWallet> updatePayment(@Field("request_id") Integer mId, @Field("payment_id") String mPayId);

}