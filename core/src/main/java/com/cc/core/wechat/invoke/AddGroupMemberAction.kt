package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Group.*
import de.robv.android.xposed.XposedHelpers.*

class AddGroupMemberAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty() || args.size < 2) {
            return ActionResult.failedResult(actionId, "AddGroupMemberAction arguments error," +
                    " first is chatRoom WechatId, the second is members wechat id list.")
        }

        if (args[0] == null) {
            return ActionResult.failedResult(actionId, "AddGroupMemberAction arguments error," +
                    "no group wechatId was found")
        }
        if (args[1] == null) {
            return ActionResult.failedResult(actionId, "AddGroupMemberAction arguments error," +
                    "no group members was found")
        }

        val chatRoomService = callStaticMethod(findClass(RoomServiceFactoryClass, Wechat.WECHAT_CLASSLOADER),
                RoomServiceFactoryGetRoomService, args[0] as String)
        val members = args[1] as List<*>

        val addMemberRequest = callMethod(chatRoomService, RoomServiceGetRequest, args[0] as String, members, null)
        callMethod(addMemberRequest, SendAddMemberRequest)
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:addGroupMember"
    }
}