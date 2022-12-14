package com.laba.user.ui.fragment.service_flow;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.FieldMap;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */
public class ServiceFlowPresenter<V extends ServiceFlowIView> extends BasePresenter<V> implements ServiceFlowIPresenter<V> {

    @Override
    public void checkStatus() {
        getCompositeDisposable().add(APIClient.getAPIClient().checkStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(checkStatusResponse -> getMvpView().onSuccess(checkStatusResponse),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
