package com.laba.user.ui.fragment.service_flow;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.DataResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIView extends MvpView{
    void onSuccess(DataResponse dataResponse);
    void onSuccess(Object object);
    void onError(Throwable e);
}
