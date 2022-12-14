package com.laba.user.ui.fragment.searching;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.laba.user.R;
import com.laba.user.base.BaseBottomSheetDialogFragment;
import com.laba.user.data.network.model.Datum;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.laba.user.base.BaseActivity.DATUM;

public class SearchingFragment extends BaseBottomSheetDialogFragment implements SearchingIView {

    private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();

    public SearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_searching;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
        presenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        alertCancel();
    }

    private void alertCancel() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (DATUM != null) {
                        Datum datum = DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());
                        showLoading();
                        presenter.cancelRequest(map);
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }



    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("INTENT_FILTER"));
        dismissAllowingStateLoss();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
