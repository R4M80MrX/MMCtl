package com.cc.core.wechat.hook;

import android.util.Log;
import android.util.SparseArray;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.Hook.NetScene.NetSceneRemoteRespClass;

public class RemoteRespHooks extends BaseXposedHook {
    private static SparseArray<OnResponseListener> responseListeners = new SparseArray<>();

    @Override
    public void hook(ClassLoader classLoader) {
        if ("6.7.2".equals(Wechat.WechatVersion)) {
            hookMethod(
                    NetSceneRemoteRespClass,
                    classLoader,
                    "a",
                    int.class,
                    byte[].class,
                    byte[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            KLog.e("bufToResp: " + "{\"response\": " + StrUtils.toJson(param.thisObject) + ", \"stackTrace\": \"" + Log.getStackTraceString(new Exception()) + "\"}");
                            try {
                                int type = XposedHelpers.getIntField(param.thisObject, "type");
                                Object response = XposedHelpers.getObjectField(param.thisObject, Wechat.Hook.NetScene.NetSceneResponseBodyKey);

                                OnResponseListener l = responseListeners.get(type);
                                if (l != null) {
                                    responseListeners.remove(type);
                                    l.onResponse(response);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
        } else if ("7.0.3".equals(Wechat.WechatVersion)) {
            hookMethod(
                    NetSceneRemoteRespClass,
                    classLoader,
                    "a",
                    int.class,
                    byte[].class,
                    byte[].class,
                    long.class,
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            KLog.e("bufToResp: " + "{\"response\": " + StrUtils.toJson(param.thisObject) + ", \"stackTrace\": \"" + Log.getStackTraceString(new Exception()) + "\"}");
                            try {
                                int type = XposedHelpers.getIntField(param.thisObject, "type");
                                Object response = XposedHelpers.getObjectField(param.thisObject, Wechat.Hook.NetScene.NetSceneResponseBodyKey);

                                OnResponseListener l = responseListeners.get(type);
                                if (l != null) {
                                    responseListeners.remove(type);
                                    l.onResponse(response);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
        }
    }

    public static void registerOnResponseListener(int type, OnResponseListener listener) {
        responseListeners.put(type, listener);
    }

    public interface OnResponseListener {
        void onResponse(Object response);
    }
}
