package com.cc.core.wechat

import android.database.Cursor
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.model.user.Friend
import de.robv.android.xposed.XposedHelpers

class ContactUtils {
    companion object {
        //private const val QUERY_CONTACT_SQL = "select username ,nickname ,alias,conRemark,verifyFlag,showHead,weiboFlag,rowid ,deleteFlag,lvbuff,descWordingId,openImAppid from rcontact  where (type & 1!=0) and type & 32=0  and type & 8 =0  and verifyFlag & 8 =0  and ( username not like '%@%' or (( (type & 1!=0) and type & 8=0 and username like '%@talkroom'))) and username != 'tmessage' and username != 'officialaccounts' and username != 'helper_entry' and username != 'blogapp' or username = 'weixin' order by showHead asc,  case when length(conRemarkPYFull) > 0 then upper(conRemarkPYFull)  else upper(quanPin) end asc,  case when length(conRemark) > 0 then upper(conRemark)  else upper(quanPin) end asc,  upper(quanPin) asc,  upper(nickname) asc,  upper(username) asc"
        private const val QUERY_CONTACT_SQL = "select * ,rowid from rcontact  where (type & 1!=0) and type & 32=0  and type & 8 =0  and verifyFlag & 8 =0  and ( username not like '%@%' or (( (type & 1!=0) and type & 8=0 and username like '%@talkroom'))) and username != 'appbrandcustomerservicemsg' and username != 'notifymessage' and username != 'weibo' and username != 'pc_share' and username != 'officialaccounts' and username != 'voicevoipapp' and username != 'cardpackage' and username != 'qqfriend' and username != 'helper_entry' and username != 'medianote' and username != 'shakeapp' and username != 'topstoryapp' and username != 'qmessage' and username != 'voipapp' and username != 'qqmail' and username != 'qqsync' and username != 'blogapp' and username != 'lbsapp' and username != 'feedsapp' and username != 'readerapp' and username != 'newsapp' and username != 'floatbottle' and username != 'fmessage' and username != 'tmessage' and username != 'weixin' and username != 'facebookapp' and username != 'meishiapp' and username != 'masssendapp' and username != 'voiceinputapp' and username != 'filehelper' and username != 'linkedinplugin' order by showHead asc,  case when length(conRemarkPYFull) > 0 then upper(conRemarkPYFull)  else upper(quanPin) end asc,  case when length(conRemark) > 0 then upper(conRemark)  else upper(quanPin) end asc,  upper(quanPin) asc,  upper(nickname) asc,  upper(username) asc"
        // private val QUERY_CONTACT_SQL = "SELECT rowid, username, alias, conRemark, nickname, verifyFlag, type " + "FROM rcontact WHERE type & 1 != 0 AND type & 8 = 0 AND type & 32 = 0 AND (verifyFlag & 8 = 0 AND username NOT LIKE '%@%') ORDER BY rowid"

        private const val QUERY_AVATAR_SQL = "select reserved1 from img_flag where username = ? ;"

        private const val QUERY_NICKNAME_SQL = "select nickname from rcontact  where username = ? ;"

        fun getContactDetails(wechatId:String) : Friend? {
            val f = getContactByWechatId(wechatId) ?: return null
            f.avatar = getAvatarBy(wechatId)
            return f
        }

        fun getContactByWechatId(wechatId: String): Friend? {
            val j = getWechatContactByWechatId(wechatId)
            return if (j == null) {
                null
            } else StrUtils.fromJson(StrUtils.toJson(j), Friend::class.java)
        }

        fun getWechatContactByWechatId(wechatId: String): Any? {
            return XposedHelpers.callMethod(HookUtils.getContactManager(), Wechat.Hook.Account.GetContactInfoFunc, wechatId)
        }

        fun getAvatarBy(wechatId: String): String {
            HookUtils.executeRawQuery(QUERY_AVATAR_SQL, wechatId).use { contactCursor ->
                if (contactCursor.moveToFirst()) {
                    return contactCursor.getString(0)

                }
            }
            return ""
        }

        fun getContactWechatIds() : Cursor {
            return HookUtils.executeRawQuery(QUERY_CONTACT_SQL)
        }

        fun getNickNameByWechatId(wechatId:String) : String? {
            return HookUtils.executeRawQuery(QUERY_NICKNAME_SQL, wechatId).use { contactCursor->
                if (contactCursor.moveToFirst()) {
                    return contactCursor.getString(0)
                }

                return null
            }
        }
    }

}