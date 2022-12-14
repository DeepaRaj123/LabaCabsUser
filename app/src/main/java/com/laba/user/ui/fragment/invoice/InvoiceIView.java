package com.laba.user.ui.fragment.invoice;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.Message;

public interface InvoiceIView extends MvpView{
    void onSuccess(Message message);
    void onSuccess(Object o);
    void onError(Throwable e);
}
