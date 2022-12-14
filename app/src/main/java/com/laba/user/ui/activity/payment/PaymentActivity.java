package com.laba.user.ui.activity.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.data.network.model.Card;
import com.laba.user.ui.activity.add_card.AddCardActivity;
import com.laba.user.ui.activity.wallet.WalletActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.laba.user.ui.fragment.invoice.InvoiceFragment.isInvoiceCashToCard;

public class PaymentActivity extends BaseActivity implements PaymentIView {

    public static final int PICK_PAYMENT_METHOD = 12;
    @BindView(R.id.add_card)
    TextView addCard;
    @BindView(R.id.cash)
    TextView tvCash;
    @BindView(R.id.razorpay)
    TextView razorpay;

    @BindView(R.id.wallet)
    TextView wallet;

    @BindView(R.id.cards_rv)
    RecyclerView cardsRv;
    @BindView(R.id.llCardContainer)
    LinearLayout llCardContainer;
    @BindView(R.id.llCashContainer)
    LinearLayout llCashContainer;
    private List<Card> cardsList = new ArrayList<>();
    private PaymentPresenter<PaymentActivity> presenter = new PaymentPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.payment));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean hideCash = extras.getBoolean("hideCash", false);
            tvCash.setVisibility(hideCash ? View.GONE : View.VISIBLE);
        }

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WalletActivity.class));
                finish();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();
        new Handler().postDelayed(() -> {
            if (BaseActivity.isCard) {
                cardsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                cardsRv.setItemAnimator(new DefaultItemAnimator());
                presenter.card();
                llCardContainer.setVisibility(View.VISIBLE);
            } else {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                llCardContainer.setVisibility(View.GONE);
            }

            if (BaseActivity.isCash && !isInvoiceCashToCard)
                llCashContainer.setVisibility(View.VISIBLE);
            else llCashContainer.setVisibility(View.GONE);
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick({R.id.add_card, R.id.cash, R.id.paypal, R.id.razorpay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_card:
                startActivity(new Intent(this, AddCardActivity.class));
                break;
            case R.id.cash:
                finishResult("CASH");
                break;
            case R.id.paypal:
                finishResult("PAYPAL");
                break;
            case R.id.razorpay:
                Toast.makeText(getApplicationContext(),"Payment method not enabled",
                        Toast.LENGTH_LONG).show();
                //                finishResult("RAZORPAY");
                break;
        }
    }

    public void deleteCard(@NonNull Card card) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_sure_you_want_to_delete))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> presenter.deleteCard(card.getCardId()))
                .setNegativeButton(getString(R.string.no), null).show();
    }

    public void finishResult(String mode) {
        Intent intent = new Intent();
        RIDE_REQUEST.put("payment_mode", mode);
        intent.putExtra("payment_mode", mode);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSuccess(Object card) {
        Toast.makeText(activity(), R.string.card_deleted_successfully, Toast.LENGTH_SHORT).show();
        presenter.card();
    }

    @Override
    public void onSuccess(List<Card> cards) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        cardsList.clear();
        cardsList.addAll(cards);
        cardsRv.setAdapter(new CardAdapter(cardsList));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

        private List<Card> list;

        CardAdapter(List<Card> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Card item = list.get(position);
            holder.card.setText(getString(R.string.card_) + item.getLastFour());
            if (item.getIsDefault() == 1) holder.ivDefaultCard.setVisibility(View.VISIBLE);
            else holder.ivDefaultCard.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            private RelativeLayout itemView;
            private TextView card;
            private ImageView ivDefaultCard;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                ivDefaultCard = view.findViewById(R.id.ivDefaultCard);
                card = view.findViewById(R.id.card);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Card card = list.get(position);
                if (view.getId() == R.id.item_view) {
                    Intent intent = new Intent();
                    RIDE_REQUEST.put("payment_mode", "CARD");
                    RIDE_REQUEST.put("card_id", card.getCardId());
                    RIDE_REQUEST.put("card_last_four", card.getLastFour());
                    intent.putExtra("payment_mode", "CARD");
                    intent.putExtra("card_id", card.getCardId());
                    intent.putExtra("card_last_four", card.getLastFour());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                Card card = list.get(position);
                if (v.getId() == R.id.item_view) deleteCard(card);
                return true;
            }
        }
    }
}
