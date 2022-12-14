package com.laba.user.base;

import android.app.Activity;

import com.laba.user.MvpApplication;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private final CompositeDisposable mCompositeDisposable;
    private V mMvpView;

    public BasePresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public Activity activity() {
        return getMvpView().activity();
    }

    @Override
    public MvpApplication appContext() {
        return MvpApplication.getInstance();
    }

    @Override
    public void attachView(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void onDetach() {
        mCompositeDisposable.dispose();
        mMvpView = null;
    }

    @Override
    public void refreshToken(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient.getAPIClient().refreshLogin(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> mMvpView.onSuccessRefreshToken(token),
                        throwable -> mMvpView.onErrorRefreshToken(throwable)));
    }

    @Override
    public void logout(String id) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .logout(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(mMvpView::onSuccessLogout, mMvpView::onError));
    }

    private boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

}
