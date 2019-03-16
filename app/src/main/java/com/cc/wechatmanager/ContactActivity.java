package com.cc.wechatmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cc.core.command.Callback;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.cc.core.wechat.model.user.Friend;
import com.cc.wechatmanager.model.ContactsResult;


public class ContactActivity extends AppCompatActivity {
    RecyclerView contactListView, messageListView;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        registerMessageBroadcast();

        messageAdapter = new MessageAdapter();
        contactListView = findViewById(R.id.contactsList);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView = findViewById(R.id.message_list);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setAdapter(messageAdapter);

        findViewById(R.id.addFriendPhoneBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.addFriendPhoneInput);
                if (TextUtils.isEmpty(et.getText())) {
                    Toast.makeText(ContactActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
                String phone = et.getText().toString();
                et = findViewById(R.id.addFriendSayHiInput);
                String sayHi = et.getText().toString();

                Messenger.Companion.sendCommand(MainActivity.genCommand("addFriend", phone, sayHi), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "addFriend Result:" + result);
                    }
                });
            }
        });
        findViewById(R.id.sendTextMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv = findViewById(R.id.sendMsgToInput);
                String to = tv.getText().toString();
                if (TextUtils.isEmpty(to)) {
                    Toast.makeText(ContactActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                    return;
                }


                tv = findViewById(R.id.sendMsgContentInput);
                String content = tv.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ContactActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                TextMessage message = new TextMessage();
                message.setCreateTime(System.currentTimeMillis() / 1000);
                message.setTarget(to);
                message.setContent(content);

                Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", message), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "sendMessage Result:" + result);
                    }
                });
            }

        });
        findViewById(R.id.sendImageMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });*/
                        EditText tv = findViewById(R.id.sendMsgToInput);
                        String to = tv.getText().toString();
                        if (TextUtils.isEmpty(to)) {
                            Toast.makeText(ContactActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        tv = findViewById(R.id.sendMsgContentInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(ContactActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ImageMessage msg = new ImageMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(to);
                        msg.setImageUrl(content);
                        msg.setFrom(Wechat.LoginWechatId);

                        Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
        findViewById(R.id.sendVideoMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });*/
                        EditText tv = findViewById(R.id.sendMsgToInput);
                        String to = tv.getText().toString();
                        if (TextUtils.isEmpty(to)) {
                            Toast.makeText(ContactActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        tv = findViewById(R.id.sendMsgContentInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(ContactActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        VideoMessage msg = new VideoMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(to);
                        msg.setVideoUrl(content);

                        Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
        getContact();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getContact();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getContact() {
        Messenger.Companion.sendCommand(MainActivity.genCommand("getContacts"), new Callback() {
            @Override
            public void onResult(String result) {

                KLog.e("---->>.", "getContacts Result:" + result);
                ContactsResult contacts = StrUtils.fromJson(result, ContactsResult.class);
                final ContactsAdapter adapter = new ContactsAdapter(true, true);
                adapter.refreshData(contacts.getData());
                adapter.setSelectFriendListener(new ContactsAdapter.OnSelectFriendListener() {
                    @Override
                    public void onSelectFriend(Friend friend, boolean isSelected) {
                        EditText tv = findViewById(R.id.sendMsgToInput);
                        tv.setText(isSelected ? friend.getWechatId() : null);
                    }
                });
                adapter.setClickFriendListener(new ContactsAdapter.OnClickFriendListener() {
                    @Override
                    public void onClickFriend(Friend friend) {
                        Intent i = new Intent(ContactActivity.this, RemarkActivity.class);
                        i.putExtra("friend", friend);
                        startActivityForResult(i, 1);
                    }
                });

                contactListView.post(new Runnable() {
                    @Override
                    public void run() {
                        contactListView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private void registerMessageBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageUtils.RECEIVE_MESSAGE_BROADCAST);

        registerReceiver(receiver, filter);
    }

    private MessageReceiver receiver = new MessageReceiver();

    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String details = intent.getStringExtra("msg");
            WeChatMessage msg = MessageUtils.Companion.messageDeserializeGson().fromJson(details, WeChatMessage.class);
            messageAdapter.addMessage(msg);
        }
    }
}
