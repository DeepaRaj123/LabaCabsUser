package com.laba.user.ui.fragment.invoice;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
//import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.laba.user.R;
import com.laba.user.base.BaseBottomSheetDialogFragment;
import com.laba.user.common.Constants;
import com.laba.user.common.Utilities;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Message;
import com.laba.user.data.network.model.Payment;
import com.laba.user.ui.activity.main.MainActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.laba.user.base.BaseActivity.DATUM;

//      TODO: Payment Gateway
//import com.braintreepayments.api.BraintreeFragment;
//import com.braintreepayments.api.PayPal;
//import com.braintreepayments.api.exceptions.InvalidArgumentException;
//import com.braintreepayments.api.models.PayPalRequest;

public class InvoiceBottomSheet extends BaseBottomSheetDialogFragment implements InvoiceIView {

    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.pay_now)
    Button payNow;
    @BindView(R.id.done)
    Button done;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.travel_time)
    TextView travelTime;
    @BindView(R.id.fixed)
    TextView fixed;
    @BindView(R.id.distance_fare)
    TextView distanceFare;
    @BindView(R.id.tax)
    TextView tax;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.wallet_detection)
    TextView walletDetection;

    private InvoicePresenter<InvoiceBottomSheet> presenter = new InvoicePresenter<>();
//    BraintreeFragment mBrainTreeFragment;

    public InvoiceBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        //      TODO: Payment Gateway
//        try {
//            mBrainTreeFragment = BraintreeFragment.newInstance(activity(), BuildConfig.PAYPAL_CLIENT_TOKEN);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//            Toast.makeText(activity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
        if (DATUM != null) initView(DATUM);
    }

    @SuppressLint("StringFormatInvalid")
    private void initView(@NonNull Datum datum) {
        bookingId.setText(datum.getBookingId());
        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM))
            distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
        else
            distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.mile)));
        travelTime.setText(getString(R.string._min, datum.getTravelTime()));

        if (datum.getPaid() == 0) {
            if (!datum.getPaymentMode().equalsIgnoreCase("CASH")) {
                payNow.setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
            }
        } else if (datum.getPaid() == 1) {
            payNow.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
        }

        initPaymentView(datum.getPaymentMode());

        Payment payment = datum.getPayment();
        if (payment != null) {
            fixed.setText(getNewNumberFormat(payment.getFixed()));
            distanceFare.setText(getNewNumberFormat(payment.getDistance()));
            tax.setText(getNewNumberFormat(payment.getTax()));
            total.setText(getNewNumberFormat(payment.getTotal()));
            payable.setText(getNewNumberFormat(payment.getPayable()));
            walletDetection.setText(getNewNumberFormat(payment.getWallet()));

        }
    }

    void initPaymentView(String value) {
        switch (value) {
            case Utilities.PaymentMode.cash:
                paymentMode.setText(getString(R.string.cash));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case Utilities.PaymentMode.card:
                paymentMode.setText(getString(R.string.card));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                break;
            case Utilities.PaymentMode.payPal:
                paymentMode.setText(getString(R.string.paypal));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case Utilities.PaymentMode.wallet:
                paymentMode.setText(getString(R.string.wallet));
                paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            case Utilities.PaymentMode.razorpay:
                paymentMode.setText(getString(R.string.razorpay));
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            default:
                break;
        }

    }

    @OnClick({R.id.payment_mode, R.id.pay_now, R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_mode:
                break;
            case R.id.pay_now:
                if (DATUM != null) {
                    Datum datum = DATUM;
                    switch (datum.getPaymentMode()) {
                        case "CARD":
                            showLoading();
                            presenter.payment(datum.getId(), 0.0);
                            break;
                        case "PAYPAL":
                            //      TODO: Payment Gateway
//                            PayPalRequest request = new PayPalRequest(String.valueOf(datum.getPayment().getPayable()))
//                                    .currencyCode(SharedHelper.getKey(activity(), "currency_code"))
//                                    .intent(PayPalRequest.INTENT_AUTHORIZE);
//                            PayPal.requestOneTimePayment(mBrainTreeFragment, request);
                            break;
                        case Utilities.PaymentMode.razorpay:
                            int   mAmount = (int) (datum.getPayment().getPayable()) * 100;
                            ((MainActivity) Objects.requireNonNull(getActivity())).initPayment(mAmount);
                            break;
                    }
                }
                break;
            case R.id.done:
                ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING");
                break;
        }
    }

    @Override
    public void onSuccess(Message message) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(getContext(), R.string.you_have_successfully_paid, Toast.LENGTH_SHORT).show();
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING");
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }
}
