package com.laba.user.chat;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.data.network.APIClient;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ChatActivity extends BaseActivity {

    private ChatMessageAdapter mAdapter;

    public static String chatPath = null;
    @BindView(R.id.chat_lv)
    ListView chatLv;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.send)
    ImageView send;
    @BindView(R.id.chat_controls_layout)
    LinearLayout chatControlsLayout;

    private DatabaseReference myRef;
    public static String sender = "user";
    private CompositeDisposable mCompositeDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mCompositeDisposable = new CompositeDisposable();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            chatPath = extras.getString("request_id", null);
            initChatView(chatPath);
        }
    }

    private void initChatView(String chatPath) {
        if (chatPath == null) return;

        message.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String myText = message.getText().toString().trim();
                if (myText.length() > 0) sendMessage(myText);
                handled = true;
            }
            return handled;
        });

        mAdapter = new ChatMessageAdapter(activity(), new ArrayList<>());
        chatLv.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(chatPath)/*.child("chat")*/;
//        Toast.makeText(getApplicationContext(),"Reference Path "+myRef.toString(),Toast.LENGTH_LONG).show();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                assert chat != null;
                if (chat.getSender() != null && chat.getRead() != null)
                    if (!chat.getSender().equals(sender) && chat.getRead() == 0) {
                        chat.setRead(1);
                        dataSnapshot.getRef().setValue(chat);
                    }
                mAdapter.add(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @OnClick(R.id.send)
    public void onViewClicked() {
        String myText = message.getText().toString();
        if (myText.length() > 0) sendMessage(myText);
    }

    private void sendMessage(String messageStr) {
        Log.e("checkingtext",messageStr);
        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setTimestamp(new Date().getTime());
        chat.setType("text");
        chat.setText(messageStr);
        chat.setRead(0);

        if (DATUM != null && DATUM.getProviderId() != null)
            chat.setDriverId(DATUM.getProviderId());

        if (DATUM != null && DATUM.getUserId() != null){
            chat.setUserId(DATUM.getUserId());
        }

        myRef.push().setValue(chat);
        message.setText("");

        if (DATUM != null && DATUM.getProviderId() != null){
            mCompositeDisposable.add(APIClient
                    .getAPIClient()
                    .postChatItem("user", DATUM.getProviderId().toString(), messageStr)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {}, Throwable::printStackTrace));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
