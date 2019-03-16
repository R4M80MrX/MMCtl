package com.cc.core.wechat

import android.database.Cursor
import android.text.TextUtils
import com.cc.core.log.KLog
import com.cc.core.utils.FileUtil
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.Wechat.Hook.*
import com.cc.core.wechat.Wechat.Hook.Account.*
import com.cc.core.wechat.Wechat.Hook.NetScene.*
import com.cc.core.wechat.Wechat.Hook.Sqlite.*
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.*
import java.io.File
import java.util.*

class HookUtils {
    companion object {
        fun getContactManager(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetContactManagerFunc)
        }

        fun getGroupManager(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetGroupManagerFunc)
        }

        fun getMsgInfoStorage(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetMsgInfoManagerFunc)
        }

        fun getConversationStorage(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetConversationManagerFunc)
        }

        fun getNetscenQueue(): Any {
            return callStaticMethod(findClass(GetNetSceneQueueClass, Wechat.WECHAT_CLASSLOADER), GetNetSceneQueueFunc)
        }

        fun getLoginUserInfo(id: Int): Any {
            return getLoginUserInfo(id, "")
        }

        fun getLoginUserInfo(id: Int, defaultValue: Any): Any {
            val coreStorageObject = callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetConfigManagerFunc)

            return callMethod(coreStorageObject, ConfigStorageGetFunc, id, defaultValue)
        }

        fun getLoginUserWechatId(): String {
            return getLoginUserInfo(UserInfoId_WechatId) as String
        }

        fun getLoginUserRegionCode(): String {
            val countryCode = getLoginUserInfo(UserInfoId_CountryCode) as String
            val provinceCode = getLoginUserInfo(UserInfoId_ProvinceCode) as String
            val cityCode = getLoginUserInfo(UserInfoId_CityCode)

            return encodeRegionCode(countryCode, provinceCode, cityCode.toString())
        }

        private fun encodeRegionCode(countryCode: String, provinceCode: String, cityCode: String): String {
            return callStaticMethod(findClass(RegionCodeDecoderClass, Wechat.WECHAT_CLASSLOADER), Wechat.Hook.Account.RegionCodeDecoderEncodeFunc, countryCode, provinceCode, cityCode) as String
        }


        fun getDbHelper(): Any {
            val obj = callStaticMethod(findClass(KernelClass, Wechat.WECHAT_CLASSLOADER), GetDBHelerFunc)
            return getObjectField(obj, DbHelperField)
        }

        fun executeRawQuery(rawQuery: String, vararg params: String): Cursor {
            return callMethod(getDbHelper(), DBRawQueryFunc, rawQuery, params) as Cursor
        }

        fun execWritableSql(sql: String): Boolean {
            return callMethod(getDbHelper(), DBExecSqlFunc, "", sql) as Boolean
        }

        fun getSelfBigAvatarUrl(): String? {
            var avatar = ""
            try {
                executeRawQuery("SELECT value FROM userinfo2 WHERE sid = 'USERINFO_LAST_LOGIN_AVATAR_PATH_STRING'").use { cursor ->
                    if (cursor.moveToNext()) {
                        avatar = cursor.getString(0)
                        if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("user_hd")) {
                            val file = FileUtil.copyFile(avatar, FileUtil.getImageCacheDirectory().absolutePath)
                            if (file != null) {
                                return file.absolutePath
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }

            return avatar
        }

        fun enqueueNetScene(request: Any, type: Int) {
            callMethod(getNetscenQueue(), NetSceneEnqueueFunc, request, type)
        }


        fun xmlToMap(xml: String, rootNodeName: String): Map<String, String> {
            val rawMap = callStaticMethod(
                    findClass(commonWechatSdkXmlParserClass, Wechat.WECHAT_CLASSLOADER),
                    commonWechatSdkXmlParserToMapFunc,
                    xml,
                    rootNodeName
            )

            return if (rawMap == null) HashMap() else rawMap as Map<String, String>
        }

        fun deleteUpdatedAPkFile() {
            try {
                var wechatPath: File? = XposedHelpers.callStaticMethod(findClass("com.tencent.mm.compatible.util.h", Wechat.WECHAT_CLASSLOADER), "getExternalStorageDirectory"/*ehM*/) as File
                if (wechatPath == null) {
                    KLog.e("AndroidUpdateHook", "External storage directory is empty")
                    return
                }
                wechatPath = File(wechatPath, "tencent/MicroMsg")
                if (!wechatPath.isDirectory) {
                    KLog.e("AndroidUpdateHook", "path [" + wechatPath.absolutePath + "] is not a directory")
                }

                for (f in wechatPath.listFiles()!!) {
                    if (f.isFile) {
                        if (StrUtils.stringNotNull(f.name).toString().contains(".apk")) {
                            KLog.i("AndroidUpdateHook", "delete update apk file:" + f.name)
                            f.delete()
                        }
                    }
                }
            } catch (e: Throwable) {
                KLog.e("AndroidUpdateHook", "deleteUpdatedAPkFile failed " + e.localizedMessage)
            }
        }

        fun getFileMd5(filePath: String?): String? {
            val md5 = callStaticMethod(findClass(GetFileMd5Uitls, Wechat.WECHAT_CLASSLOADER), GetFileMd5Func, filePath)
                    ?: return null

            return md5 as String
        }
    }
}