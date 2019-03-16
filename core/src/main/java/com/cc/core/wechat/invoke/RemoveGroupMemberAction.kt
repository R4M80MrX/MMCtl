package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.model.group.GroupInfo
import de.robv.android.xposed.XposedHelpers

class RemoveGroupMemberAction : Action {
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

        val groupWechatId = args[0] as String
        if (!checkIsGroupOwner(StrUtils.stringNotNull(groupWechatId).toString())) {
            return ActionResult.failedResult(actionId, "You is not owner of group $groupWechatId")
        }

        val chatRoomService = XposedHelpers.callStaticMethod(XposedHelpers.findClass(Wechat.Hook.Group.RoomServiceFactoryClass, Wechat.WECHAT_CLASSLOADER),
                Wechat.Hook.Group.RoomServiceFactoryGetRoomService, args[0] as String)
        val members = args[1] as List<*>

        val addMemberRequest = XposedHelpers.callMethod(chatRoomService, Wechat.Hook.Group.RoomServiceGetRequest, groupWechatId, members, 1)
        XposedHelpers.callMethod(addMemberRequest, Wechat.Hook.Group.SendAddMemberRequest)
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:removeGroupMember"
    }

    private fun checkIsGroupOwner(groupWechatId: String): Boolean {
        val raw = XposedHelpers.callMethod(HookUtils.getGroupManager(), Wechat.Hook.Group.GetGroupInfoFunc, groupWechatId)
                ?: return false
        KLog.e("))(((())))))(((")
        XposedHelpers.callMethod(raw, Wechat.Hook.Group.GroupParseChatroomDataFunc)

        KLog.e("))((((7777777))))))(((")
        val groupInfo = StrUtils.fromJson<GroupInfo>(StrUtils.toJson(raw), GroupInfo::class.java)
                ?: return false

        KLog.e("))((((7777777))))))(((${groupInfo.getRoomOwner()}")
        return groupWechatId == groupInfo.getRoomOwner()
    }
}