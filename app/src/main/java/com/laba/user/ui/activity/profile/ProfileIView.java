package com.laba.user.ui.activity.profile;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.User;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIView extends MvpView{
    void onSuccess(User user);
    void onSuccessUpdate(User user);
    void onError(Throwable e);
}
