package com.cc.core.wechat.model.group

import com.google.gson.annotations.SerializedName

class GroupInfo {
    @SerializedName(value = "displayName", alternate = ["field_displayname"])
    private var displayName : String? = null
    @SerializedName(value = "nickName", alternate = ["field_chatroomnick"])
    private var nickName : String? = null
    @SerializedName(value = "groupWechatId", alternate = ["field_chatroomname"])
    private var groupWechatId :String? = null
    @SerializedName(value = "notice", alternate = ["field_chatroomnotice"])
    private var notice:String?= null
    @SerializedName(value = "roomOwner", alternate = ["field_roomowner"])
    private var roomOwner: String? = null
    @SerializedName(value = "memberList")
    private var memberList:List<GroupMember>?=null

    fun getDisplayName():String? {
        return displayName
    }

    fun setDisplayName(displayName:String?) {
        this.displayName = displayName
    }

    fun getGroupWechatId():String? {
        return groupWechatId
    }

    fun setGroupWechatId(groupWechatId:String?) {
        this.groupWechatId = groupWechatId
    }

    fun getNotice():String? {
        return notice
    }

    fun setNotice(notice:String?) {
        this.notice = notice
    }

    fun getRoomOwner():String? {
        return roomOwner
    }

    fun setRoomOwner(roomOwner:String?) {
        this.roomOwner = roomOwner
    }

    fun getNickName():String? {
        return nickName
    }

    fun setNickName(nickName:String?) {
        this.nickName = nickName
    }

    fun setMemberList(memberList : List<GroupMember>?) {
        this.memberList = memberList
    }

    fun getMemberList() : List<GroupMember> ? {
        return memberList
    }
}