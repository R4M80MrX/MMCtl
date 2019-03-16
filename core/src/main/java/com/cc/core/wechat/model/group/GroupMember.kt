package com.cc.core.wechat.model.group

import com.google.gson.annotations.SerializedName
import java.util.*

class GroupMember {
    @SerializedName("wechatId")
    private var wechatId: String? = null
    @SerializedName("groupNickname")
    private var groupNickName: String? = null
    @SerializedName("groupDisplayName")
    private var groupDisplayName: String? = null
    @SerializedName("invitedBy")
    private var invitedBy: String? = null
    @SerializedName("sex")
    private var sex: String = "0"
    @SerializedName("avatar")
    private var avatar: String? = null

    fun setWechatId(wechatId: String?) {
        this.wechatId = wechatId
    }

    fun getWechatId(): String? {
        return wechatId
    }

    fun setGroupNickName(groupNickName: String?) {
        this.groupNickName = groupNickName
    }

    fun getGroupNickName(): String? {
        return groupNickName
    }

    fun setDisplayNickName(groupDisplayName: String?) {
        this.groupDisplayName = groupDisplayName
    }

    fun getDisplayNickName(): String? {
        return groupDisplayName
    }

    fun setInvitedBy(invitedBy: String?) {
        this.invitedBy = invitedBy
    }

    fun getInvitedBy(): String? {
        return invitedBy
    }

    fun setSex(sex: String) {
        this.sex = sex
    }

    fun getSex(): String {
        return sex
    }

    fun setAvatar(avatar: String?) {
        this.avatar = avatar
    }

    fun getAvatar(): String? {
        return avatar
    }
}