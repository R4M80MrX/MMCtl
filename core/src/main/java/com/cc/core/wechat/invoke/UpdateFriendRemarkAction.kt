package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.ContactUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Account.ContactStorageLogic
import com.cc.core.wechat.Wechat.Hook.Account.ContactStorageLogicUpdateRemark
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass

class UpdateFriendRemarkAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.size < 2) {
            return ActionResult.failedResult(actionId, "Illegal argument error, the first must be friend wechatId " +
                    "the second must be the new remark")
        }

        val wechatId = args[0] as String
        val remark = args[1] as String

        val contactInfo = ContactUtils.getWechatContactByWechatId(wechatId) ?: return ActionResult.failedResult(actionId, "Can not find friend $wechatId")
        callStaticMethod(findClass(ContactStorageLogic, Wechat.WECHAT_CLASSLOADER), ContactStorageLogicUpdateRemark, contactInfo, remark)

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:updateFriendRemark"
    }
}