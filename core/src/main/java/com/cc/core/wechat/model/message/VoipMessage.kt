package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

class VoipMessage : BaseMessage(WeChatMessageType.VOIP) {

    companion object {
        val VoiceType : Int = 1
        val VideoType : Int = 2
    }
    // 1 - Video, 2 - Voice
    @SerializedName("voipType")
    private var voipType = VoiceType

    fun setVoipType(voipType : Int) {
        this.voipType = voipType
    }

    fun getVoipType() : Int {
        return voipType
    }
}