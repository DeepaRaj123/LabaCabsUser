package com.laba.user.ui.activity.setting;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
