package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.ApplicationContext
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.ImageUtil
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.MessageUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Message.AppMsgLogic
import com.cc.core.wechat.Wechat.Hook.Message.AppMsgLogicSendFunc
import com.cc.core.wechat.Wechat.Hook.NetScene.*
import com.cc.core.wechat.Wechat.WECHAT_CLASSLOADER
import com.cc.core.wechat.model.message.CardMessage
import com.cc.core.wechat.model.message.ImageMessage
import com.cc.core.wechat.model.message.TextMessage
import com.cc.core.wechat.model.message.VideoMessage
import com.cc.core.wechat.model.message.WeChatMessage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.setObjectField
import de.robv.android.xposed.XposedHelpers.newInstance
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class SendMessageAction : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No message was provided")
        }
        val gson = MessageUtils.messageDeserializeGson()

        val msg = gson.fromJson(args[0].toString(), WeChatMessage::class.java)
        if (msg is TextMessage) {
            sendTextMessage(msg)
        } else if (msg is ImageMessage) {
            sendImageMessage(msg)
        } else if (msg is VideoMessage) {
            sendVideoMessage(msg)
        } else if (msg is CardMessage) {
          sendCardMessage(msg)
        } else {
            return ActionResult.failedResult(actionId, "Not implement " + msg.javaClass.simpleName)
        }

        return ActionResult.successResult(actionId)
    }

    private fun sendTextMessage(msg: TextMessage) {
        val messageType = 1
        var flag = 0
        var messageAttributes: MutableMap<String, String>? = null
        if (msg.getAtUsers() != null && !msg.getAtUsers()!!.isEmpty()) {
            flag = 291

            messageAttributes = HashMap()
            messageAttributes["atuserlist"] = "<![CDATA[" + StrUtils.join(msg.getAtUsers()!!.asIterable(), ",") + "]]>"
        }
        val request = newInstance(findClass(Wechat.Hook.NetScene.NetSceneSendMsgClass, Wechat.WECHAT_CLASSLOADER),
                msg.getTarget(), msg.getContent(), messageType, flag, messageAttributes)
        HookUtils.enqueueNetScene(request, 0)
    }

    private fun sendImageMessage(msg: ImageMessage) {

        val data = arrayOf(3, msg.getFrom(), msg.getTarget(), msg.getImageUrl(), 1, null, 0, "", "", true, NetSceneUploadMsgImgMaskResId)
        val request = XposedHelpers.newInstance(findClass(NetSceneUploadMsgImg, WECHAT_CLASSLOADER), *data)
        HookUtils.enqueueNetScene(request, 0)
    }

    private fun sendVideoMessage(msg: VideoMessage) {

        val path = ArrayList<String?>()
        path.add(msg.getVideoUrl())
        val handler = XposedHelpers.newInstance(XposedHelpers.findClass(NetSceneUploadMsgVideo, Wechat.WECHAT_CLASSLOADER),
                ApplicationContext.forgroundActivity, path, null, msg.getTarget(), 2, null)

        callStaticMethod(XposedHelpers.findClass(UploadMsgVideoHandler, Wechat.WECHAT_CLASSLOADER), "post", handler, "ChattingUI_importMultiVideo")
    }

    private fun sendCardMessage(message:CardMessage) {
        val webpage = XposedHelpers.newInstance(
            findClass(
                "com.tencent.mm.opensdk.modelmsg.WXWebpageObject", Wechat.WECHAT_CLASSLOADER
            ), message.getUrl()
        )
        val msg = XposedHelpers.newInstance(
            findClass(
                "com.tencent.mm.opensdk.modelmsg.WXMediaMessage", Wechat.WECHAT_CLASSLOADER
            ), webpage
        )
        XposedHelpers.setObjectField(msg, "title", message.getTitle())

        message.setDescription(StrUtils.stringNotNull(message.getDescription()).toString())
        if (message.getDescription()!!.length > 1024) {
            message.setDescription(message.getDescription()!!.substring(0, 1024))
        }

        setObjectField(msg, "description", message.getDescription())

        if (!TextUtils.isEmpty(message.getThumbUrl()) && File(message.getThumbUrl()).exists()) {
            setObjectField(msg, "thumbData", ImageUtil.fileToWxThumb(message.getThumbUrl()))
        }

        callStaticMethod(
            findClass(AppMsgLogic, Wechat.WECHAT_CLASSLOADER), AppMsgLogicSendFunc, msg,
            "", "", message.getTarget(), 2, null) // 2 msg.appmsg.androidsource 没找到具体用途
    }

    override fun key(): String? {
        return "wechat:sendMessage"
    }
}