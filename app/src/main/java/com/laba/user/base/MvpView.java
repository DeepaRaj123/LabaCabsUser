package com.laba.user.base;

import android.app.Activity;

import com.laba.user.data.network.model.Token;

public interface MvpView {

    Activity activity();

    void showLoading();

    void hideLoading() throws Exception;

    void onErrorRefreshToken(Throwable throwable);

    void onSuccessRefreshToken(Token token);

    void onSuccessLogout(Object object);

    void onError(Throwable throwable);
}
