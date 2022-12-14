package com.laba.user.base;

import android.app.Activity;

import com.laba.user.MvpApplication;

import java.util.HashMap;

public interface MvpPresenter<V extends MvpView> {

    Activity activity();

    MvpApplication appContext();

    void attachView(V mvpView);

    void onDetach();

    void refreshToken(HashMap<String, Object> obj);

    void logout(String id);

}
