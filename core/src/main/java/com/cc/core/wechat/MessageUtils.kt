package com.cc.core.wechat

import android.content.Intent
import android.text.TextUtils
import com.cc.core.ApplicationContext
import com.cc.core.log.KLog
import com.cc.core.utils.FileUtil
import com.cc.core.utils.MD5
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.Wechat.Hook.Message.MessageVoiceLogicClass
import com.cc.core.wechat.Wechat.Hook.Message.MessageVoiceLogicGetVoiceFullPathFunc
import com.cc.core.wechat.Wechat.Hook.NetScene.ModelCdnUtil
import com.cc.core.wechat.Wechat.Hook.NetScene.ModelCdnUtilGetFileKeyFunc
import com.cc.core.wechat.hook.tool.CdnLogicHooks
import com.cc.core.wechat.model.message.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MessageUtils {
    companion object {
        const val RECEIVE_MESSAGE_BROADCAST = "com.cc.core.wechat.Receive_Message_Broadcast"

        private const val GetMsgSvrIdSql = "select msgId from message where msgSvrId = ?"

        private const val GetVoiceMsgImagePathSql = "select imgPath from message where msgSvrId = ?"

        private val lock = ArrayBlockingQueue<String>(1)
        private val executorService = Executors.newFixedThreadPool(5)

        fun receiveNewMessage(messageInfo: Object) {
            executorService.submit { processNewMessage(messageInfo) }
        }

        private fun processNewMessage(messageInfo: Object) {
            KLog.e("message ===>>>", StrUtils.toJson(messageInfo))
            val msgType = XposedHelpers.getIntField(messageInfo,
                    Wechat.Hook.Message.MessageTypeFieldId)

            var obj: Any? = XposedHelpers.getObjectField(messageInfo,
                    Wechat.Hook.Message.MessageToFieldId)
            val to = obj?.toString() ?: ""

            val from: String
            var content: String
            obj = XposedHelpers.getObjectField(messageInfo,
                    Wechat.Hook.Message.MessageContentFieldId)
            if (StrUtils.isGroupWechatId(to)) {
                val info = obj?.toString()?.split(":".toRegex(), 2)?.toTypedArray()
                        ?: arrayOf("", "")
                from = info[0]
                content = if (info.size > 1) info[1] else ""
                if (content[0] == '\n') {
                    content = content.substring(1)
                }
            } else {
                content = obj!!.toString()
                from = to
            }

            KLog.e("message content ===>>>", content)
            val dateTime = XposedHelpers.getLongField(messageInfo,
                    Wechat.Hook.Message.MessageDatetimeFieldId)

            obj = XposedHelpers.getObjectField(messageInfo,
                    Wechat.Hook.Message.MessageServIdFieldId)
            val msgservId = obj?.toString() ?: ""

            val msg: WeChatMessage
            when (msgType) {
                WeChatMessageType.IMAGE, WeChatMessageType.EMOJI -> {
                    msg = ImageMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setType(msgType)
                    if (WeChatMessageType.EMOJI == msgType) {
                        msg.setImageUrl(MessageUtils.getEmojiImageUrl(content)!!)
                    } else {
                        msg.setImageUrl(
                                MessageUtils.downloadImage(msg, content))
                    }
                }
                WeChatMessageType.VIDEO -> {
                    msg = VideoMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setVideoUrl(MessageUtils.downloadVideo(msg, content))
                }
                WeChatMessageType.TEXT -> {
                    msg = TextMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setContent(content)
                }
                WeChatMessageType.CARD -> {
                    msg = CardMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    MessageUtils.processCardMessage(msg, content)
                }
                WeChatMessageType.VOICE -> {
                    msg = VoiceMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    MessageUtils.processVoiceMessage(msg)
                }
                WeChatMessageType.VOIP -> {
                    /*msg = VoipMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    MessageUtils.processVoipMessage(msg, content)*/
                    return
                }
                else -> {
                    msg = UnsupportMessage()
                    msg.setMessageDetails(content)
                    msg.setContent("[不支持的消息格式]")
                    msg.setType(msgType)
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                }
            }
            KLog.e("message", StrUtils.toJson(msg))
            MessageUtils.notifyMessageReceived(msg)
        }

        fun messageDeserializeGson(): Gson {
            return GsonBuilder().registerTypeAdapter(WeChatMessage::class.java, MessageTypeAdapter())
                    .create()
        }

        private fun downloadImage(message: ImageMessage, details: String): String {
            val map = HookUtils.xmlToMap(details, "msg")

            val savePath = File(
                    FileUtil.getImageCacheDirectory(),
                    MD5.getMD5(details + System.currentTimeMillis())!! + ".jpg"
            ).absolutePath
            val msgId = getMessageIdByServId(message.getMsgServId())
            val fileKey = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
                    ModelCdnUtilGetFileKeyFunc,
                    "downimg", message.getCreateTime(), message.getTarget(), msgId.toString() + ""
            )
            val request = XposedHelpers.newInstance(
                    XposedHelpers.findClass(
                            "com.tencent.mars.cdn.CdnLogic\$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER
                    )
            )
            XposedHelpers.setObjectField(request, "fileKey", fileKey)
            XposedHelpers.setObjectField(request, "savePath", savePath)
            XposedHelpers.setObjectField(request, "fileid", map[".msg.img.\$cdnmidimgurl"])
            XposedHelpers.setObjectField(request, "aeskey", map[".msg.img.\$aeskey"])
            XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map[".msg.img.\$length"]))
            XposedHelpers.setIntField(request, "fileType", 2)
            XposedHelpers.setIntField(request, "transforTimeoutSeconds", 600)
            XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0)
            XposedHelpers.setBooleanField(request, "isAutoStart", true)
            XposedHelpers.setIntField(
                    request, "chatType", if (StrUtils.isGroupWechatId(message.getTarget())) 1 else 0
            )
            XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER),
                    "startC2CDownload", request
            )
            waitForDownloadFinish(fileKey.toString())

            // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);

            return savePath
        }

        private fun downloadVideo(message: VideoMessage, details: String): String {
            val map = HookUtils.xmlToMap(details, "msg")

            val fileName = System.currentTimeMillis().toString() + ""
            val savePath = File(FileUtil.getVideoCacheDirectory(), "$fileName.mp4").absolutePath
            val fileKey = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
                    ModelCdnUtilGetFileKeyFunc,
                    "downvideo", message.getCreateTime(), message.getTarget(), fileName
            )
            val request = XposedHelpers.newInstance(
                    XposedHelpers.findClass(
                            "com.tencent.mars.cdn.CdnLogic\$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER
                    )
            )

            XposedHelpers.setObjectField(request, "fileKey", fileKey)
            XposedHelpers.setObjectField(request, "savePath", savePath)
            XposedHelpers.setObjectField(request, "fileid", map[".msg.videomsg.\$cdnvideourl"])
            XposedHelpers.setObjectField(request, "aeskey", map[".msg.videomsg.\$aeskey"])
            XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map[".msg.videomsg.\$length"]))
            XposedHelpers.setIntField(request, "fileType", 4)
            XposedHelpers.setIntField(request, "transforTimeoutSeconds", 0)
            XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0)
            XposedHelpers.setBooleanField(request, "isAutoStart", true)
            XposedHelpers.setIntField(
                    request, "chatType", if (StrUtils.isGroupWechatId(message.getTarget())) 1 else 0
            )
            XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER),
                    "startVideoStreamingDownload", request, 0
            )
            waitForDownloadFinish(fileKey.toString())
            return savePath
        }

        private fun getMessageIdByServId(servId: String?): Long {
            val cursor = HookUtils.executeRawQuery(GetMsgSvrIdSql, servId!!)
            var msgId: Long = -1
            if (cursor.moveToFirst()) {
                msgId = cursor.getLong(cursor.getColumnIndex("msgId"))
            }
            cursor.close()
            return msgId
        }

        private fun getVoiceMessageImagePathByServId(servId: String?): String? {
            var retryTimes = 0
            while (retryTimes < 60) {
                ++retryTimes
                val cursor = HookUtils.executeRawQuery(GetVoiceMsgImagePathSql, servId!!)
                var imgPath: String? = null
                if (cursor.moveToFirst()) {
                    imgPath = cursor.getString(cursor.getColumnIndex("imgPath"))
                }
                cursor.close()
                if (!TextUtils.isEmpty(imgPath)) {
                    return imgPath
                } else {
                    Utils.sleep(1000)
                }
            }
            return null
        }

        /**
         * <msg>
         * <appmsg appid="" sdkver="0">
         * <title>蔚来ES8，百公里烧35~40升柴油的电动车</title>
         * <des>尽管我早就知道蔚来要搞幺蛾子，但没想到这么邪乎。</des>
         * <action></action>
         * <type>5</type>
         * <showtype>0</showtype>
         * <soundtype>0</soundtype>
         * <mediatagname></mediatagname>
         * <messageext></messageext>
         * <messageaction></messageaction>
         * <content></content>
         * <contentattr>0</contentattr>
         * <url>http://mp.weixin.qq.com/s?__biz=MzIxMTQ3NTUxOQ==&amp;mid=2247483835&amp;idx=1&amp;sn=9d31784a2743af7c0870e0ff643defbe&amp;chksm=97558ceba02205fdf1f7e4220982acf82def12b02933c93827c01013124c3dce4df7a4ddbf56&amp;mpshare=1&amp;scene=1&amp;srcid=0220YC1yZfdnp61e3prdokdm#rd</url>
         * <lowurl></lowurl>
         * <dataurl></dataurl>
         * <lowdataurl></lowdataurl>
         * <appattach>
         * <totallen>0</totallen>
         * <attachid></attachid>
         * <emoticonmd5></emoticonmd5>
         * <fileext></fileext>
         * <cdnthumburl>3055020100044e304c020100020421e3c3ff02033d11fe020435f516d202045c6e046e0427777875706c6f61645f3634313335373339354063686174726f6f6d315f313535303731333936350204010400030201000400</cdnthumburl>
         * <cdnthumbmd5>01c02f8a365230b687b433101e70cd55</cdnthumbmd5>
         * <cdnthumblength>7089</cdnthumblength>
         * <cdnthumbwidth>160</cdnthumbwidth>
         * <cdnthumbheight>160</cdnthumbheight>
         * <cdnthumbaeskey>c6b589dfeba455fd715750e2eaec26f8</cdnthumbaeskey>
         * <aeskey>c6b589dfeba455fd715750e2eaec26f8</aeskey>
         * <encryver>0</encryver>
         * <filekey>9640220477@chatroom5_1550728021</filekey>
        </appattach> *
         * <extinfo></extinfo>
         * <sourceusername>gh_353c2ff556ef</sourceusername>
         * <sourcedisplayname></sourcedisplayname>
         * <thumburl>https://mmbiz.qlogo.cn/mmbiz_jpg/l5N3ll27MAoppibBWFuqt0F0aV8luRias3PediappO5q8KLyTQnEyAaqiaxH1IqQQYUENWMmwn2g18XnFc6ZmTN0Pw/300?wx_fmt=jpeg&amp;wxfrom=1</thumburl>
         * <md5></md5>
         * <statextstr></statextstr>
        </appmsg> *
         * <fromusername>xnhjcc</fromusername>
         * <scene>0</scene>
         * <appinfo>
         * <version>1</version>
         * <appname></appname>
        </appinfo> *
         * <commenturl></commenturl>
        </msg> *
         * @param message
         * @param details
         */
        private fun processCardMessage(message: CardMessage?, details: String) {
            if (message == null) {
                return
            }

            val map = HookUtils.xmlToMap(details, "msg")
            if (map.isEmpty()) {
                return
            }

            message.setDescription(map[".msg.appmsg.des"])
            message.setTitle(map[".msg.appmsg.title"])
            message.setThumbUrl(map[".msg.appmsg.thumburl"])
            message.setUrl(map[".msg.appmsg.url"])
            message.setCardType(map[".msg.appmsg.type"])

            if ("8".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：斗图表情]")
            } else if ("6".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：文件传输]")
            } else if ("33".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：小程序分享]")
            } else if ("2001".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：红包/收款]")
            } else if ("2000".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：转账]")
            }
        }

        private fun getEmojiImageUrl(details: String): String? {

            val map = HookUtils.xmlToMap(details, "msg")
            return map[".msg.emoji.\$cdnurl"]
        }

        fun notifyMessageReceived(msg: WeChatMessage) {
            val i = Intent(RECEIVE_MESSAGE_BROADCAST)
            i.putExtra("msg", StrUtils.toJson(msg))
            ApplicationContext.application().sendBroadcast(i)
        }

        private fun waitForDownloadFinish(fileKey: String?) {
            CdnLogicHooks.registerDownloadListeners(fileKey) { fileKey -> lock.put(fileKey) }
            lock.poll(30, TimeUnit.SECONDS)
        }

        fun processOperationMessage(messageDetails: Any) {
            if (avoidMessageRevoke(messageDetails)) {
                return
            }

        }

        private fun processVoiceMessage(message: VoiceMessage?) {
            if (message == null) {
                return
            }

            val imagePath = getVoiceMessageImagePathByServId(message.getMsgServId())
            if (TextUtils.isEmpty(imagePath)) {
                return
            }
            val fullPath = getVoiceFullPath(imagePath)


            val path = FileUtil.copyFile(fullPath, FileUtil.getVoiceCacheDirectory().absolutePath)
            if (path != null) {
                message.setVoiceUrl(path.absolutePath)
                return
            }
        }

        /*private fun processVoipMessage(message: VoipMessage?, content : String) {
            if (message == null) {
                return
            }

            if (TextUtils.isEmpty(content)) {
                return
            }

            val map = HookUtils.xmlToMap(content, "voipinvitemsg")
            if (map.isEmpty()) {
                return
            }

            val type = map[".voipinvitemsg.status"]
            message.setVoipType(Integer.valueOf(type))
        }*/

        private fun getVoiceFullPath(imgPath: String?): String? {
            val path = callStaticMethod(findClass(MessageVoiceLogicClass, Wechat.WECHAT_CLASSLOADER),
                    MessageVoiceLogicGetVoiceFullPathFunc, imgPath, false)
            return if (path == null) {
                ""
            } else {
                path as String
            }
        }

        /**
         * <sysmsg type="sysmsgtemplate">
        <sysmsgtemplate>
        <content_template type="tmpl_type_profile">
        <plain><![CDATA[]]></plain>
        <template><![CDATA["$username$"试图撤回一条消息“$remark$”]]></template>
        <link_list>
        <link name="username" type="link_profile">
        <memberlist>
        <member>
        <username><![CDATA[wxid_w4y2nc022m0g22]]></username>
        <nickname><![CDATA[wxfake]]></nickname>
        </member>
        </memberlist>
        </link>
        <link name="remark" type="link_plain">
        <plain><![CDATA[被我阻止了，哈哈]]></plain>
        </link>
        </link_list>
        </content_template>
        </sysmsgtemplate>
        </sysmsg>
         */

        fun avoidMessageRevoke(messageDetails: Any): Boolean {

            val contentObj = XposedHelpers.getObjectField(messageDetails,
                    Wechat.Hook.Message.MessageContentFieldId)
            val msgContent = XposedHelpers.getObjectField(contentObj, Wechat.Hook.NetScene.NetSceneResponseStringBooleanValueKey)
            val nickName = isRevokeMessage(msgContent as String)
            if (TextUtils.isEmpty(nickName)) {
                return false
            }

            var content = """
                $nickName 试图撤回一条消息, 被我阻止了，哈哈!!
            """.trimIndent()
            XposedHelpers.setIntField(messageDetails, Wechat.Hook.Message.MessageTypeFieldId, WeChatMessageType.GROUP_OPERATION)
            XposedHelpers.setObjectField(contentObj, Wechat.Hook.NetScene.NetSceneResponseStringBooleanValueKey, content)
            return true
        }

        /**
         * <sysmsg type="revokemsg">
        <revokemsg>
        <session>
        11249221992@chatroom
        </session>
        <msgid>
        1060587575
        </msgid>
        <newmsgid>
        2783623623504511865
        </newmsgid>
        <replacemsg>
        <![CDATA["wxfake" 撤回了一条消息]]>
        </replacemsg>
        </revokemsg>
        </sysmsg>
         */
        fun isRevokeMessage(content: String): String? {
            if (TextUtils.isEmpty(content)) {
                return null
            }
            val map = HookUtils.xmlToMap(content, "revokemsg")
            if (map.isEmpty()) {
                return null
            }
            val content = map[".revokemsg.replacemsg"] ?: return null
            return content.split("\"".toRegex())[1]
        }
    }
}