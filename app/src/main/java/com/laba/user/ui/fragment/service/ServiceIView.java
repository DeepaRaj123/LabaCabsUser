package com.laba.user.ui.fragment.service;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.EstimateFare;
import com.laba.user.data.network.model.Service;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIView extends MvpView{
    void onSuccess(List<Service> serviceList);
    void onSuccess(EstimateFare estimateFare);
    void onError(Throwable e);
    void onSuccess(Object object);
}
