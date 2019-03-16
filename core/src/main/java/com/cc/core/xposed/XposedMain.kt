package com.cc.core.xposed

import com.cc.core.log.KLog
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.AvoidUpdateHooks
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedMain : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        KLog.e("XposedMain", "Xposed handle load package:" + lpparam!!.packageName + "    AppInfo:" + lpparam.appInfo + "   processName:" + lpparam.processName)
        if (lpparam.packageName == Wechat.WECHAT_PACKAGE_NAME) {
            XposedBridge.log("XposedMain" + ">>process: " + lpparam.processName)
            if (lpparam.processName == Wechat.WECHAT_PACKAGE_NAME) {
                Wechat.start(lpparam)
            } else if (lpparam.packageName.equals("com.tencent.mm:sandbox")) {
                XposedBridge.log("XposedMain" + ">>process: " + lpparam.processName)
                Wechat.lookup(AvoidUpdateHooks::class.java).hook(lpparam.classLoader)
            }

        }
    }
}