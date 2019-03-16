package com.cc.core.wechat

class WeChatMessageType {
  companion object {
    const val UNKNOWN = 0
    const val TEXT = 1 // 普通文本消息
    const val IMAGE = 3 // 图片消息
    const val VOICE = 34 // 语音消息
    const val CONTACTCARD = 42 // 名片消息
    const val VIDEO = 43 // 视频消息
    const val EMOJI = 47 // Emoji表情
    const val LOCATION = 48 // 位置消息
    const val CARD = 49 // 标准卡片消息
    const val VOIP = 50 // Voip 消息
    const val MSG_SYNC = 51 // 其他平台微信客户端的消息同步 ??
    const val GROUP_OPERATION = 10000 // 文本系统消息 = 入群、退群、踢出群、好友验证通过，建群成功，受邀入群，群主改变，改群名)
    const val CANCELABLE_GROUP_OPERATION =
      10002 // 可操作的系统消息 = 拉人入群，别人扫描自己的二维码入群)  = 在6.6.3之后存到数据库会变成 GROUP_OPERATION_663 = 570425393) )
    const val EMOTION_IMAGE = 1048625 // 推送时为CARD = 49)，大表情或输入法斗图等
    const val APP_IMAGE = 268435505 // 推送时为CARD = 49)，某些app分享图片会出现
    const val MP_ACCOUNT_SEMIXML = 285212721 // 公众号图文消息，semixml
    const val WEIXIN_NOTIFICATION = 318767153 // 推送时为CARD = 49)，微信团队安全登录提醒
    const val AA_OR_RED_PACKET = 436207665 // 推送时为CARD = 49)，红包或AA收款
    const val NEWYEAR_RED_PACKET = 469762097 // 推送时为CARD = 49)，拜年红包消息
    const val TRANSFER_MONEY = 419430449 // 推送时为CARD = 49)，转账
    const val HONEY_PAY = 536870961 // 推送时为CARD = 49)，亲属卡
    const val GROUP_OPERATION_663 = 570425393 // 可操作的系统消息
    const val MP_ACCOUNT_CARD = -1879048185 // 推送时为CARD = 49)，公众号卡片消息 = 目前仅发现微信运动有)
    const val SHARE_TEXT = 16777265 // 推送时为CARD = 49; SDK发送的文本消息
  }
}