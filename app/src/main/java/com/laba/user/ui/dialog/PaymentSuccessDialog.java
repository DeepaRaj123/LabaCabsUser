package com.laba.user.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.laba.user.R;

public class PaymentSuccessDialog extends Dialog {

    public PaymentSuccessDialog(@NonNull Context context) {
        super(context);

        setCancelable(false);
        setContentView(R.layout.dialog_payment_success);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
