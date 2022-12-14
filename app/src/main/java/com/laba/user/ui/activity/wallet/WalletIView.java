package com.laba.user.ui.activity.wallet;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.AddWallet;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);
    void onError(Throwable e);
}
