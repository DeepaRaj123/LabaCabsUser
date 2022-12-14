package com.laba.user.ui.activity.login;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.ForgotResponse;
import com.laba.user.data.network.model.Token;

public interface LoginIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(ForgotResponse object);
    void onError(Throwable e);
}
