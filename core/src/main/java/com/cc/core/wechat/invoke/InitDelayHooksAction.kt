package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.tool.CdnLogicHooks
import com.cc.core.wechat.hook.tool.XLogHooks

class InitDelayHooksAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        Wechat.LoginWechatId = HookUtils.getLoginUserWechatId()

        var hooks = Wechat.lookup(CdnLogicHooks::class.java)
        if (hooks == null) {
            hooks = CdnLogicHooks()
            hooks.hook(Wechat.WECHAT_CLASSLOADER)
            Wechat.addHook(hooks)
        }

        hooks = Wechat.lookup(XLogHooks::class.java)
        if (hooks == null) {
            hooks = XLogHooks()
            hooks.hook(Wechat.WECHAT_CLASSLOADER)
            Wechat.addHook(hooks)
        }

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:initDelayHooks"
    }
}