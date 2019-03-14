package com.cc.wechatmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.cc.core.wechat.model.sns.SnsInfo;
import com.cc.wechatmanager.model.CommandResult;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.LoginUserResult;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GroupActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.sns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SnsActivity.class);
                startActivity(i);
            }
        });
        Messenger.Companion.sendCommand(genCommand("initDelayHooks"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {

            }
        });
        getWechatInfo();
    }

    private void getWechatInfo() {
        Messenger.Companion.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
            @Override
            public void onResult(final String result) {

                KLog.e("---->>.", "getLoginUserInfo Result:" + result);
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        LoginUserResult result1 = StrUtils.fromJson(result, LoginUserResult.class);
                        if (result1.isSuccess()) {
                            View v = findViewById(R.id.wechatInfo);
                            TextView tv = v.findViewById(R.id.name);
                            tv.setText(result1.getData().getNickname());
                            tv = v.findViewById(R.id.wechatId);
                            tv.setText(result1.getData().getWechatId());
                            ImageView iv = v.findViewById(R.id.avatar);
                            Glide.with(MainActivity.this).load(new File(result1.getData().getAvatar())).into(iv);
                        }
                    }
                });


            }
        });
    }

    public static Command genCommand(String key, Object... data) {
        Command c = new Command();
        c.setKey(key);
        c.setId(UUID.randomUUID().toString());
        List<Object> args = new ArrayList<>(Arrays.asList(data));
        c.setArgs(args);
        return c;
    }
}
