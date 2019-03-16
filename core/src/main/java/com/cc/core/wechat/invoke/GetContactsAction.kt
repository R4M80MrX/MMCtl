package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.model.user.Friend
import com.cc.core.log.KLog
import com.cc.core.wechat.ContactUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import java.util.ArrayList


class GetContactsAction : Action {

    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val wechatIds = ArrayList<String>()
        val friends = ArrayList<com.cc.core.wechat.model.user.Friend>()

        ContactUtils.getContactWechatIds().use { contactCursor ->
            while (contactCursor.moveToNext()) {
                val wechatId = contactCursor.getString(0)

                // Robot self should not be considered as a friend
                if (Wechat.LoginWechatId == wechatId) {
                    continue
                }

                wechatIds.add(wechatId)
            }

            for (w in wechatIds) {
                val f = ContactUtils.getContactDetails(w)

                if (f != null) {
                    friends.add(f)
                }
            }
        }

        KLog.e("----->>>>> ${wechatIds}")
        return ActionResult.successResult(actionId, friends)
    }

    override fun key(): String? {
        return "wechat:getContacts"
    }
}