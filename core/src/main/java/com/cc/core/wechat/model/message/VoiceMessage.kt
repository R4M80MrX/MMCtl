package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

class VoiceMessage : BaseMessage(WeChatMessageType.VOICE) {
    @SerializedName("voiceUrl")
    private var voiceUrl: String? = null

    fun getVoiceUrl(): String? {
        return voiceUrl
    }

    fun setVoiceUrl(voiceUrl: String) {
        this.voiceUrl = voiceUrl
    }
}