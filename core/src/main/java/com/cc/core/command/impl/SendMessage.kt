package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.MessageUtils
import com.cc.core.wechat.invoke.SendMessageAction
import com.cc.core.wechat.model.message.CardMessage
import com.cc.core.wechat.model.message.ImageMessage
import com.cc.core.wechat.model.message.VideoMessage
import com.cc.core.wechat.model.message.WeChatMessage

class SendMessage : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No message was provided")
        }
        val gson = MessageUtils.messageDeserializeGson()

        val msg : WeChatMessage
        msg = if (args[0] is WeChatMessage) {
            args[0] as WeChatMessage
        } else {
            gson.fromJson(args[0].toString(), WeChatMessage::class.java)
        }

        when (msg) {
            is ImageMessage -> {
                val path = Utils.downloadFile(msg.getImageUrl(), false)
                msg.setImageUrl(path)
            }
            is VideoMessage -> {
                val path = Utils.downloadFile(msg.getVideoUrl(), true)
                msg.setVideoUrl(path)
            }
            is CardMessage -> {
                val path = Utils.downloadFile(msg.getThumbUrl(), false)
                msg.setThumbUrl(path)
            }
        }
        return Actions.execute(SendMessageAction::class.java, actionId, StrUtils.toJson(msg))
    }

    override fun key(): String? {
        return "sendMessage"
    }
 }