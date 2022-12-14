package com.laba.user.ui.activity.main;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.AddressResponse;
import com.laba.user.data.network.model.DataResponse;
import com.laba.user.data.network.model.InitSettingsResponse;
import com.laba.user.data.network.model.Provider;
import com.laba.user.data.network.model.User;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIView extends MvpView{
    void onSuccess(User user);
    void onSuccess(Object object);
    void onSuccess(DataResponse dataResponse);
    void onSuccessLogout(Object object);
    void onSuccess(AddressResponse response);
    void onSuccess(List<Provider> objects);
    void onSuccess(InitSettingsResponse initSettingsResponse);
    void onError(Throwable e);
    void onCheckStatusError(Throwable e);

}
