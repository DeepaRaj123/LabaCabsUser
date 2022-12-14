package com.laba.user.ui.fragment.invoice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PayPalRequest;
import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.Constants;
import com.laba.user.common.Utilities;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Message;
import com.laba.user.data.network.model.Payment;
import com.laba.user.ui.activity.main.MainActivity;
import com.razorpay.PaymentResultListener;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.laba.user.base.BaseActivity.DATUM;
import static com.laba.user.base.BaseActivity.RIDE_REQUEST;
import static com.laba.user.data.SharedHelper.getKey;
import static com.laba.user.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class InvoiceFragment extends BaseFragment implements InvoiceIView {

    public static boolean isInvoiceCashToCard = false;
    @BindView(R.id.payment_mode)
    TextView tvPaymentMode;
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
    @BindView(R.id.time_fare)
    TextView timeFare;
    @BindView(R.id.llDistanceFareContainer)
    LinearLayout llDistanceFareContainer;
    @BindView(R.id.llTimeFareContainer)
    LinearLayout llTimeFareContainer;
    @BindView(R.id.llTipContainer)
    LinearLayout llTipContainer;
    @BindView(R.id.llWalletDeductionContainer)
    LinearLayout llWalletDeductionContainer;
    @BindView(R.id.llDiscountContainer)
    LinearLayout llDiscountContainer;
    @BindView(R.id.llPaymentContainer)
    LinearLayout llPaymentContainer;
    @BindView(R.id.llTravelTime)
    LinearLayout llTravelTime;
    @BindView(R.id.llBaseFare)
    LinearLayout llBaseFare;
    @BindView(R.id.llPayable)
    LinearLayout llPayable;
    //    @BindView(R.id.tvChange)
//    TextView tvChange;
    @BindView(R.id.tvGiveTip)
    TextView tvGiveTip;
    @BindView(R.id.tvTipAmt)
    TextView tvTipAmt;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;
    private InvoicePresenter<InvoiceFragment> presenter = new InvoicePresenter<>();
    private BraintreeFragment mBrainTreeFragment;
    private Payment payment;
    private String payingMode;
    private Double tips = 0.0;

    public InvoiceFragment() {
    }

    public static InvoiceFragment newInstance() {
        return new InvoiceFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);

        if (DATUM != null) initView(DATUM);

        return view;
    }

    @SuppressLint("StringFormatInvalid")
    private void initView(@NonNull Datum datum) {
        bookingId.setText(datum.getBookingId());
        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0)
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.KM));
            else
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
        } else {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0)
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.MILES));
            else
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.mile)));
        }
        try {
            if (datum.getTravelTime() != null && Double.parseDouble(datum.getTravelTime()) > 0) {
                llTravelTime.setVisibility(View.VISIBLE);
                travelTime.setText(datum.getTravelTime() + " " + getString(R.string._min));
            } else llTravelTime.setVisibility(View.GONE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            llTravelTime.setVisibility(View.VISIBLE);
            travelTime.setText(datum.getTravelTime() + " " + getString(R.string._min));
        }
        initPaymentView(datum.getPaymentMode(), "", false);

        payment = datum.getPayment();
        try {
            if (payment != null) {
                if (payment.getFixed() > 0) {
                    llBaseFare.setVisibility(View.VISIBLE);
                    fixed.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getFixed())));
                } else llBaseFare.setVisibility(View.GONE);
                tax.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(payment.getTax())));
                total.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(payment.getTotal())));
                if (payment.getPayable() > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getPayable())));
                } else llPayable.setVisibility(View.GONE);

                if (payment.getWallet() > 0) {
                    llWalletDeductionContainer.setVisibility(View.VISIBLE);
                    walletDetection.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getWallet())));
                } else llWalletDeductionContainer.setVisibility(View.GONE);
                if (payment.getDiscount() > 0) {
                    llDiscountContainer.setVisibility(View.VISIBLE);
                    tvDiscount.setText(String.format("%s -%s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getDiscount())));

                } else llDiscountContainer.setVisibility(View.GONE);

                //      MIN,    HOUR,   DISTANCE,   DISTANCEMIN,    DISTANCEHOUR
                if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.min)
                        || datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.hour)) {
                    llTimeFareContainer.setVisibility(View.VISIBLE);
                    llDistanceFareContainer.setVisibility(View.GONE);
                    distanceFare.setText(R.string.time_fare);
                    if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.min)) {
                        if (payment.getMinute() > 0) {
                            llTimeFareContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getMinute())));
                        } else llTimeFareContainer.setVisibility(View.GONE);

                    } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.hour)) {
                        if (payment.getHour() > 0) {
                            llTimeFareContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getHour())));
                        } else llTimeFareContainer.setVisibility(View.GONE);
                    }
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distance)) {
                    llTimeFareContainer.setVisibility(View.GONE);
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0)
                        llDistanceFareContainer.setVisibility(View.GONE);
                    else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                getNewNumberFormat(payment.getDistance())));
                    }
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceMin)) {
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0)
                        llDistanceFareContainer.setVisibility(View.GONE);
                    else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                getNewNumberFormat(payment.getDistance())));
                    }
                    if (payment.getMinute() > 0) {
                        llTimeFareContainer.setVisibility(View.VISIBLE);
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                getNewNumberFormat(payment.getMinute())));
                    } else llTimeFareContainer.setVisibility(View.GONE);
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceHour)) {
                    if (payment.getDistance() == 0.0 || payment.getDistance() == 0) {
                        llDistanceFareContainer.setVisibility(View.GONE);
                    } else {
                        llDistanceFareContainer.setVisibility(View.VISIBLE);
                        distanceFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                getNewNumberFormat(payment.getDistance())));
                    }
                    if (payment.getHour() > 0) {
                        llTimeFareContainer.setVisibility(View.VISIBLE);
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                getNewNumberFormat(payment.getHour())));
                    } else llTimeFareContainer.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Datum datum = DATUM;
        if (datum.getPaymentMode() != null) payingMode = datum.getPaymentMode();
        llPaymentContainer.setVisibility(datum.getPaid() == 1 ? View.GONE : View.VISIBLE);

        if (datum.getPaid() == 0 && payingMode.equalsIgnoreCase("CASH")) {
            done.setVisibility(View.VISIBLE);
            payNow.setVisibility(View.GONE);
            llTipContainer.setVisibility(View.GONE);
            //  tvChange.setVisibility(View.VISIBLE);
            done.setOnClickListener(v -> Toasty.info(getContext(),
                    getString(R.string.payment_not_confirmed), Toast.LENGTH_SHORT).show());
        } else if (datum.getPaid() == 0 && payingMode.equalsIgnoreCase("CARD")) {
            done.setVisibility(View.GONE);
            payNow.setVisibility(View.VISIBLE);
            llTipContainer.setVisibility(View.VISIBLE);
            //  tvChange.setVisibility(View.GONE);
        } else if (datum.getPaid() == 0 && payingMode.equalsIgnoreCase("RAZORPAY")) {
            done.setVisibility(View.GONE);
            payNow.setVisibility(View.VISIBLE);
            llTipContainer.setVisibility(View.VISIBLE);
            //  tvChange.setVisibility(View.GONE);
        } else if (datum.getPaid() == 1 && payingMode.equalsIgnoreCase("CASH")) {
            done.setVisibility(View.VISIBLE);
            payNow.setVisibility(View.GONE);
            llTipContainer.setVisibility(View.GONE);
            //   tvChange.setVisibility(View.GONE);
        } else if (datum.getPaid() == 1 && payingMode.equalsIgnoreCase("CARD")) {
            done.setVisibility(View.VISIBLE);
            payNow.setVisibility(View.GONE);
            llTipContainer.setVisibility(View.GONE);
            //  tvChange.setVisibility(View.GONE);
        } else if (datum.getPaid() == 1 && payingMode.equalsIgnoreCase("RAZORPAY")) {
            done.setVisibility(View.VISIBLE);
            payNow.setVisibility(View.GONE);
            llTipContainer.setVisibility(View.GONE);
            //  tvChange.setVisibility(View.GONE);
        }

//        if (isCard && isCash)
//            tvChange.setVisibility(View.VISIBLE);
//        else if (isCard)
//            tvChange.setVisibility(View.VISIBLE);
//        else if (isCash)
//            tvChange.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(Object obj) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
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
    public void onError(Throwable e) {
        handleError(e);
    }

    @OnClick({R.id.payment_mode, R.id.pay_now, R.id.done, R.id.tvGiveTip, R.id.tvTipAmt, R.id.ivInvoice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_mode:
               /* if (isCard && isCash)
                    startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                else if (isCard)
                    startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);*/
                break;
            case R.id.pay_now:
                if (DATUM != null) {
                    Datum datum = DATUM;
                    switch (datum.getPaymentMode()) {
                        case Utilities.PaymentMode.card:
                            showLoading();
                            presenter.payment(datum.getId(), tips);
                            break;
                        case Utilities.PaymentMode.payPal:

                            try {
                                mBrainTreeFragment = BraintreeFragment.newInstance(activity(), BuildConfig.PAYPAL_CLIENT_TOKEN);


                                PayPalRequest request = new PayPalRequest(String.valueOf(datum.getPayment().getPayable()))
                                        .currencyCode(getKey(activity(), "currency_code"))
                                        .intent(PayPalRequest.INTENT_AUTHORIZE);
                                PayPal.requestOneTimePayment(mBrainTreeFragment, request);

                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }

                            break;
                        case Utilities.PaymentMode.cash:
                            if (isInvoiceCashToCard) {
                                showLoading();
                                presenter.payment(datum.getId(), tips);
                            }
                            break;
                        case "razorpay":
                            int   mAmount = (int) (datum.getPayment().getPayable()+tips) * 100;
                            ((MainActivity) Objects.requireNonNull(getActivity())).initPayment(mAmount);
                            break;
                    }
                }
                break;
            case R.id.done:
            case R.id.ivInvoice:
                ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING");
                break;
            case R.id.tvTipAmt:
            case R.id.tvGiveTip:
                showTipDialog(payment.getPayable());
                break;
        }
    }

    private void showTipDialog(double totalAmount) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_tip);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button percent10 = dialog.findViewById(R.id.bt10Percent);
        Button percent15 = dialog.findViewById(R.id.bt15Percent);
        Button percent20 = dialog.findViewById(R.id.bt20Percent);
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        percent10.setOnClickListener(v -> etAmount.setText(String.format("%.2f", (totalAmount * 10) / 100)));

        percent15.setOnClickListener(v -> etAmount.setText(String.format("%.2f", (totalAmount * 15) / 100)));

        percent20.setOnClickListener(v -> etAmount.setText(String.format("%.2f", (totalAmount * 20) / 100)));

        tvSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etAmount.getText()) && Double.parseDouble(etAmount.getText().toString()) > 0) {
                tvGiveTip.setVisibility(View.GONE);
                tvTipAmt.setVisibility(View.VISIBLE);
                tips = Double.parseDouble(etAmount.getText().toString());
                double payableCal = payment.getPayable() + tips;
                tvTipAmt.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(tips)));
                if (payableCal > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payableCal)));
                } else llPayable.setVisibility(View.GONE);
                dialog.dismiss();
            } else {
                tvGiveTip.setVisibility(View.VISIBLE);
                tvTipAmt.setVisibility(View.GONE);
                if (payment.getPayable() > 0) {
                    llPayable.setVisibility(View.VISIBLE);
                    payable.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getPayable())));
                    tips = 0.0;
                } else llPayable.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        HashMap<String, Object> map = new HashMap<>();

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.card)) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
                llTipContainer.setVisibility(View.VISIBLE);
                // tvChange.setVisibility(View.GONE);
                isInvoiceCashToCard = true;
            } else if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.cash)) {
                RIDE_REQUEST.put("card_id", null);
                RIDE_REQUEST.put("card_last_four", null);
                llTipContainer.setVisibility(View.GONE);
                // tvChange.setVisibility(View.VISIBLE);
                isInvoiceCashToCard = false;
            }

            // initPayment(tvPaymentMode);
            initPaymentView(data.getStringExtra("payment_mode"),
                    data.getStringExtra("card_last_four"), true);

            showLoading();

            map.put("request_id", DATUM.getId());
            map.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.card))
                map.put("card_id", data.getStringExtra("card_id"));

            presenter.updateRide(map);

        }
    }

    void initPaymentView(String payment_mode, String value, boolean payment) {

        switch (payment_mode) {
            case "CASH":
                tvPaymentMode.setText(payment_mode);
                //  tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case "CARD":
                if (payment) {
                    if (!value.equals("")) tvPaymentMode.setText(getString(R.string.card_) + value);
                } else tvPaymentMode.setText(payment_mode);
                // tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visa, 0, 0, 0);
                break;
            case "PAYPAL":
                tvPaymentMode.setText(getString(R.string.paypal));
                //  tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case "WALLET":
                tvPaymentMode.setText(getString(R.string.wallet));
                // tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            case "razorpay":
                tvPaymentMode.setText(getString(R.string.razorpay));
                // tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            default:
                break;
        }
    }
}
