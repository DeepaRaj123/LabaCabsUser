package com.laba.user.ui.fragment.rate;

import android.annotation.SuppressLint;
import android.content.Intent;
//import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.laba.user.R;
import com.laba.user.base.BaseBottomSheetDialogFragment;
import com.laba.user.common.Utilities;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Provider;
import com.laba.user.ui.fragment.invoice.InvoiceFragment;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.laba.user.base.BaseActivity.DATUM;
import static com.laba.user.chat.ChatActivity.chatPath;

public class RatingDialogFragment extends BaseBottomSheetDialogFragment
        implements RatingIView {

    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.ratings_name)
    TextView ratingsName;
    private RatingPresenter<RatingDialogFragment> presenter = new RatingPresenter<>();

    public RatingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rating_dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View view) {
        InvoiceFragment.isInvoiceCashToCard = false;
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);

        if (DATUM != null) {
            Provider provider = DATUM.getProvider();
            if (provider != null) {
                Glide.with(activity()).load(provider.getAvatar()).
                        apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).
                                dontAnimate().error(R.drawable.ic_user_placeholder)).into(avatar);
                ratingsName.setText(getString(R.string.ratings) + " " +
                        provider.getFirstName() + " " + provider.getLastName());
            }
        }
    }

    @Override
    public void onSuccess(Object object) {
        Toast.makeText(activity(), getString(R.string.ride_complete_successfull), Toast.LENGTH_SHORT).show();
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        dismiss();
        Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("INTENT_FILTER"));
        if (!TextUtils.isEmpty(chatPath)) clearChatDB();
    }

    private void clearChatDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(chatPath);
        myRef.removeValue();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            map.put("rating", Math.round(rating.getRating()));
            map.put("comment", Utilities.getEncodeMessage(comment.getText().toString()));
            showLoading();
            presenter.rate(map);
        }
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
