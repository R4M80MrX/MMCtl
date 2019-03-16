package com.cc.core.wechat.hook;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.cc.core.log.KLog;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.model.message.VoipMessage;
import com.cc.core.xposed.BaseXposedHook;

import org.jetbrains.annotations.NotNull;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Start Activity:com.tencent.mm.plugin.voip.ui.VideoActivity  Intent:
 *      Voip_LastPage_Hash:1551623181175
 *     Voip_Outcall:true
 *     Voip_VideoCall:false
 *     Voip_User:xnhjcc
 */
public class VoipCallHooks extends BaseXposedHook {
    @Override
    public void hook(@NotNull ClassLoader classLoader) {
        hookMethod("com.tencent.mm.plugin.voip.ui.VideoActivity", classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity voipActivity = (Activity) param.thisObject;
                Intent i = voipActivity.getIntent();
                boolean isOutCall = i.getBooleanExtra("Voip_Outcall", false);

                boolean isVideo = i.getBooleanExtra("Voip_VideoCall", false);

                String talker = i.getStringExtra("Voip_User");
                if (TextUtils.isEmpty(talker)) {
                    return;
                }

                VoipMessage msg = new VoipMessage();
                msg.setVoipType(isVideo ? VoipMessage.Companion.getVideoType() :  VoipMessage.Companion.getVoiceType());
                msg.setCreateTime(System.currentTimeMillis() / 1000);
                msg.setFrom(isOutCall ? Wechat.LoginWechatId : talker);
                msg.setTarget(isOutCall ? talker : Wechat.LoginWechatId);
                MessageUtils.Companion.notifyMessageReceived(msg);
            }
        });
    }
}
