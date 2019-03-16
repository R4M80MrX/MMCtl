package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.ContactUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Group.*
import com.cc.core.wechat.model.group.GroupInfo
import com.cc.core.wechat.model.group.GroupMember
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.getObjectField

class GetGroupInfoAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No group wechatIs was found!")
        }

        val groups = ArrayList<GroupInfo>()
        for (group in args) {
            val groupInfo = getGroupInfo(group as String) ?: continue
            groups.add(groupInfo)
        }
        return ActionResult.successResult(actionId, groups)
    }

    override fun key(): String? {
        return "wechat:getGroupInfo"
    }

    private fun getGroupInfo(groupWechatId: String): GroupInfo? {
        KLog.e("======>>>>> getGroupInfo:$groupWechatId" )
        val raw = XposedHelpers.callMethod(HookUtils.getGroupManager(), Wechat.Hook.Group.GetGroupInfoFunc, groupWechatId)
                ?: return null
        callMethod(raw, GroupParseChatroomDataFunc)

        val groupInfo = StrUtils.fromJson<GroupInfo>(StrUtils.toJson(raw), GroupInfo::class.java)
                ?: return null

        var members = getObjectField(raw, ChatroomMembersField) ?: return groupInfo
        members = members as Map<*, *>
        var groupMembers = ArrayList<GroupMember>()
        for (key in members.keys) {
            val groupUser = GroupMember()
            groupUser.setWechatId(key as String)

            val value = members[key]
            val groupNickname = getObjectField(value, ChatroomMemberGroupNicknameField)
            if (groupNickname != null) {
                groupUser.setGroupNickName(groupNickname as String)
            }
            val invitedBy = getObjectField(value, ChatroomMemberInviterField)
            if (invitedBy != null) {
                groupUser.setInvitedBy(invitedBy as String)
            }

            val details = ContactUtils.getContactDetails(key)
            if (details != null) {
                groupUser.setAvatar(details.avatar)
                groupUser.setDisplayNickName(if (TextUtils.isEmpty(details.remark)) {
                    details.nickname
                } else {
                    details.remark
                })
                groupUser.setSex(details.sex)
            }
            groupMembers.add(groupUser)
        }

        groupInfo.setNickName(ContactUtils.getNickNameByWechatId(groupWechatId))
        groupInfo.setMemberList(groupMembers)
        /*if (!TextUtils.isEmpty(groupInfo.getMembers())) {
            val members = ArrayList<Friend>()
            val membersWechatId = groupInfo.getMembers()!!.split(";")
            for (m in membersWechatId) {
                val f = ContactUtils.getContactByWechatId(m) ?: continue
                members.add(f)
            }
        }*/

        return groupInfo
    }
}