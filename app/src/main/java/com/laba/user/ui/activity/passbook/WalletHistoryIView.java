package com.laba.user.ui.activity.passbook;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);
    void onError(Throwable e);
}
