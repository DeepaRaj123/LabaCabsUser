package com.laba.user.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.laba.user.R;
import com.laba.user.data.network.model.Token;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment implements MvpView {

    View view;

    public abstract int getLayoutId();

    public abstract void initView(View view);

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }
        progressDialog = new ProgressDialog(activity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        return view;
    }

    @Override
    public Activity activity() {
        return getActivity();
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }


    public void handleError(Throwable e) {
        ((BaseActivity) getActivity()).handleError(e);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onSuccessRefreshToken(Token token) {
        ((BaseActivity) getActivity()).onSuccessRefreshToken(token);
    }

    @Override
    public void onErrorRefreshToken(Throwable throwable) {
        ((BaseActivity) getActivity()).onErrorRefreshToken(throwable);
    }

    @Override
    public void onSuccessLogout(Object object) {
        ((BaseActivity) getActivity()).onSuccessLogout(object);
    }

    @Override
    public void onError(Throwable throwable) {
        ((BaseActivity) getActivity()).onError(throwable);
    }


    public String getNewNumberFormat(double d) {
        return ((BaseActivity) getActivity()).getNewNumberFormat(d);
    }

}
