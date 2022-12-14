package com.laba.user.ui.activity.profile;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Part;

public class ProfilePresenter<V extends ProfileIView> extends BasePresenter<V> implements ProfileIPresenter<V> {
    @Override
    public void update(HashMap<String, RequestBody> obj,
                       @Part MultipartBody.Part filename) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile(obj, filename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> getMvpView().onSuccessUpdate(user),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void profile() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> getMvpView().onSuccess(user),
                        throwable -> getMvpView().onError(throwable)));
    }
}
