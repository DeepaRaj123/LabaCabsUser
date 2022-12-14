package com.laba.user.ui.activity.social;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView{
    void onSuccess(Token token);
    void onError(Throwable e);
}
