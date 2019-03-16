package com.cc.core;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.SharedPreferences;

import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;
import com.cc.core.utils.NTTimeUtils;
import com.cc.core.wechat.Wechat;


public class ApplicationContext {
    public static String PACKAGE_NAME = "com.cc.wechatmanager";
    private static Application application;
    public static Activity forgroundActivity;

    public static void setup(Application application) {
        init(application);
        //enableAccessibility();]

        // clear cache
        WorkerHandler.postOnWorkThread(new Runnable() {
            @Override public void run() {
                clearCache();
            }
        });
    }

    public static boolean init(Application application) {
        KLog.e(">>>> Application init:" + (application == null ? "null" : application.getClass().getName()));
        ApplicationContext.application = application;
        WorkerHandler.getInstance().init();
        KLog.enableLog2Console(KLog.POLICY_MASK);
        if (application != null) {
            PACKAGE_NAME = application.getPackageName();
        }

        return Wechat.initEnvironment(Wechat.WECHAT_PACKAGE_NAME);
    }

    public static Application application() {
        if (application == null) {
            application = AndroidAppHelper.currentApplication();
        }

        return application;
    }

    private static void clearCache() {
        if (application() == null) return;
        SharedPreferences pre = application().getSharedPreferences("xposed_config", 0);
        long lastClearDate = pre.getLong("last_clear_date", System.currentTimeMillis());
        if (System.currentTimeMillis() - lastClearDate > NTTimeUtils.MILLIS_IN_DAY * 7) {
            FileUtil.clearCache();
            pre.edit().putLong("last_clear_date", System.currentTimeMillis()).apply();
        }
    }
}
