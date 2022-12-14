package com.laba.user.ui.dialog;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.laba.user.R;
import com.laba.user.base.BaseBottomSheetDialogFragment;
import com.laba.user.ui.activity.main.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleSuccessfulDialog extends BaseBottomSheetDialogFragment {

    @Override
    public int getLayoutId() {
        return R.layout.dialog_schedule_confirmation;
    }

    public ScheduleSuccessfulDialog(FragmentManager fragmentManager) {
        show(fragmentManager, "ScheduleSuccessfulDialog");
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
    }

    @OnClick({R.id.confirm})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.confirm) {
            close();
        }
    }

    void close() {
        ((MainActivity) requireActivity()).dismissDialog("ScheduleSuccessfulDialog");
    }
}