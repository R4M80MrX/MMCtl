package com.cc.wechatmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cc.core.command.Callback;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.model.user.Friend;
import com.cc.wechatmanager.model.CommandResult;

public class RemarkActivity extends AppCompatActivity {
    private Friend friend;
    private EditText remarkEV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        friend = (Friend) getIntent().getSerializableExtra("friend");
        remarkEV = findViewById(R.id.remark);
        remarkEV.setHint(TextUtils.isEmpty(friend.getRemark()) ? friend.getNickname() : friend.getRemark());
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(remarkEV.getText())) {
                    finish();
                } else {
                    remarkFriend(remarkEV.getText().toString());
                }
            }
        });
    }

    private void remarkFriend(final String remark) {
        Messenger.Companion.sendCommand(MainActivity.genCommand("updateFriendRemark", friend.getWechatId(), remark), new Callback() {
            @Override
            public void onResult(String result) {
                CommandResult res = StrUtils.fromJson(result, CommandResult.class);
                if (res.isSuccess()) {
                    Intent data = new Intent();
                    data.putExtra("remarkResult", remark);
                    setResult(RESULT_OK, data);
                    Utils.showToast("备注成功");

                } else {

                    Utils.showToast("备注失败");
                }
                finish();
                KLog.e("---->>.", "sendMessage Result:" + result);
            }
        });

    }
}
