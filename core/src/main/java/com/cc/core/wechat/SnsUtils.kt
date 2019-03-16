package com.cc.core.wechat

import com.cc.core.utils.ImageUtil
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.Wechat.Hook.Sns.SnsGetSessionIdFun
import com.cc.core.wechat.Wechat.Hook.Sns.SnsGetSessionIdUtil
import com.cc.core.wechat.model.sns.SnsInfo
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.*
import java.util.*

class SnsUtils {
    companion object {

        fun generateSnsUploadPackHelper(snsInfo: SnsInfo): Any? {
            var sns: Any? = when (snsInfo.getSnsType()) {
                SnsInfo.TEXT_TYPE -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), 2)
                }
                SnsInfo.VIDEO_TYPE-> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), 15)
                }
                SnsInfo.CARD_TYPE-> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), 3)
                }
                else -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), 1)
                }
            }

            setTextSns(sns, snsInfo)

            when (snsInfo.getSnsType()) {
                SnsInfo.IMAGE_TYPE -> {
                    setImageSns(sns, snsInfo)
                }
                SnsInfo.VIDEO_TYPE -> {
                    setVideoSns(sns, snsInfo)
                }
                SnsInfo.CARD_TYPE -> {
                    setCardSns(sns, snsInfo)
                }
                else -> {
                    return sns
                }
            }

            return sns
        }

        private fun setTextSns(sns: Any?, snsInfo: SnsInfo) {
            if (sns == null) {
                return
            }
            callMethod(sns, Wechat.Hook.Sns.SnsSetDescriptionFun, snsInfo.getDescription())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTypeFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetWatcherTypeFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetSyncQQZoneFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetIsPrivateFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetWatchersFun, ArrayList<String>())
            callMethod(sns, Wechat.Hook.Sns.SnsSetAtUsersFun, LinkedList<Any>())
            callMethod(sns, Wechat.Hook.Sns.SnsSetSessionIdFun, "")
            callMethod(sns, Wechat.Hook.Sns.SnsSetLocationFun, newInstance(findClass(Wechat.Hook.Sns.LocationClass, Wechat.WECHAT_CLASSLOADER)))
        }

        private fun setImageSns(sns: Any?, snsInfo: SnsInfo) {
            if (sns == null) {
                return
            }

            callMethod(sns, Wechat.Hook.Sns.SnsSetUrlFun, "", null, null, 0, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetMediaInfoFun, genMediaList(snsInfo.getMedias(), snsInfo.getDescription()))
        }

        private fun setVideoSns(sns: Any?, snsInfo: SnsInfo) : Boolean {
            if (snsInfo.getMedias() == null || snsInfo.getMedias()!!.isEmpty()) {
                return false
            }
            val videoPath = snsInfo.getMedias()!![0]
            val caverPath = ImageUtil.getVideoPathCover(videoPath) ?: return false

            val md5 = HookUtils.getFileMd5(videoPath) ?: return false

            callMethod(sns, Wechat.Hook.Sns.SnsSetVideoInfoFun, videoPath, caverPath, snsInfo.getDescription(), md5)

            /*callMethod(sns, Wechat.Hook.Sns.SnsSetVideoInfoFun, "/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/video/videoCompressTmp/video_send_preprocess_tmp_1551376714007.mp4",
                    "/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/video/videoCompressTmpThumb/video_send_preprocess_thumb_1551376762312.jpg",
                    snsInfo.getDescription(), "4f4de48085c93716a8b8d6742f3812da")*/
            return true
        }

        private fun setCardSns(sns: Any?, snsInfo: SnsInfo) {
            // SessionId@msg_-1049477600#66650946219
            callMethod(sns, Wechat.Hook.Sns.SnsSetSessionIdFun, callStaticMethod(findClass(SnsGetSessionIdUtil, Wechat.WECHAT_CLASSLOADER), SnsGetSessionIdFun,"sns_" + Math.abs(StrUtils.stringNotNull(snsInfo.getUrl()).hashCode())))

            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTypeFun, 1)
            callMethod(sns, Wechat.Hook.Sns.SnsSetUrlFun, snsInfo.getUrl(), snsInfo.getUrl(), "", 1, 0)
            if (!(snsInfo.getMedias() == null || snsInfo.getMedias()!!.isEmpty())) {
                val thumb = ImageUtil.fileToWxThumb(snsInfo.getMedias()!![0])
                if (thumb != null) {
                    callMethod(sns, Wechat.Hook.Sns.SnsSetShareThumbFun, thumb, "", "")
                }
            }
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareUrlFun, snsInfo.getUrl())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareUrl2Fun, snsInfo.getUrl())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTitleFun, snsInfo.getShareTitle())
        }

        fun uploadSns() {
            val aw = newInstance(XposedHelpers.findClass(Wechat.Hook.Sns.UploadManager, Wechat.WECHAT_CLASSLOADER))
            callMethod(aw, Wechat.Hook.Sns.UploadFun)
        }

        private fun genMediaList(medias: ArrayList<String>?, desc: String?): Any {
            val mediasStorage = LinkedList<Any>()
            if (medias == null) return mediasStorage
            for (m in medias) {
                val data = newInstance(findClass("com.tencent.mm.plugin.sns.data.h", Wechat.WECHAT_CLASSLOADER), m, 2)
                setObjectField(data, "desc", desc)
                mediasStorage.add(data)
            }
            return mediasStorage
            /*val picWidget = newInstance(findClass("", Wechat.WECHAT_CLASSLOADER), null)
            val bundle = Bundle()
            bundle.putStringArrayList("sns_kemdia_path_list", medias)
            callMethod(picWidget, "F", bundle)
            return picWidget*/
        }

        //region share
        // UploadPackHelper#WZ <<<<<: [ (SnsUploadPackHelperHooks.java:239)#beforeHookedMethod ] 胖子的尴尬！胖老鼠卡下水道洞口 惊动消防队来救援(视频)
        //02-28 17:49:03.193 7490-7490/? E/>>>> UploadPackHelper#WX <<<<<: [ (SnsUploadPackHelperHooks.java:227)#beforeHookedMethod ] view.inews.qq.com/w/WXN20190228007200020?refer=nwx&bat_id=1114029339&cur_pos=0&grp_type=region&rg=4&newsGid=20190228_r9501831&grp_index=2&grp_id=1314029345&rate=2&pushid=2019022802&bkt=0&openid=o04IBADwyLkEIMxeKWKVU1bdaHj4&tbkt=C0&ft=0&groupid=1551346179&msgid=0&isappinstalled=0&key=0e43a58611e7182a6eeb15e373a72592151e0be9047dd3b8680e06a7bbe212f20a8375b1fd4da03fa27c53f55bf003b786610f2ecf4953e1fce3b3fe619c6ecb05275d18043fa3f0cbe6e20accdb4612&version=27000334&devicetype=android-21&wuid=oDdoCt_EBUvS7z2-XG8PebsIA4Kg&cv=0x27000334&dt=2&lang=zh_CN&pass_ticket=Dx21GhuU%2BOlSSJVXo8dfVI0HRxBAiGSgcwfA2iaBCeP1G5mMo5jVREXC0joXuKzS
        //02-28 17:49:03.203 7490-7490/? E/>>>> UploadPackHelper#WY <<<<<: [ (SnsUploadPackHelperHooks.java:233)#beforeHookedMethod ] view.inews.qq.com/w/WXN20190228007200020?refer=nwx&bat_id=1114029339&cur_pos=0&grp_type=region&rg=4&newsGid=20190228_r9501831&grp_index=2&grp_id=1314029345&rate=2&pushid=2019022802&bkt=0&openid=o04IBADwyLkEIMxeKWKVU1bdaHj4&tbkt=C0&ft=0&groupid=1551346179&msgid=0&isappinstalled=0&key=0e43a58611e7182a6eeb15e373a72592151e0be9047dd3b8680e06a7bbe212f20a8375b1fd4da03fa27c53f55bf003b786610f2ecf4953e1fce3b3fe619c6ecb05275d18043fa3f0cbe6e20accdb4612&version=27000334&devicetype=android-21&wuid=oDdoCt_EBUvS7z2-XG8PebsIA4Kg&cv=0x27000334&dt=2&lang=zh_CN&pass_ticket=Dx21GhuU%2BOlSSJVXo8dfVI0HRxBAiGSgcwfA2iaBCeP1G5mMo5jVREXC0joXuKzS
        //02-28 17:49:03.203 7490-7490/? E/>>>> UploadPackHelper#WU <<<<<: [ (SnsUploadPackHelperHooks.java:209)#beforeHookedMethod ] 默默
        //02-28 17:49:03.213 7490-7490/? E/====++MicroMsg.snsMediaStorage++==>>>>>>: [ (TestHooks.java:133)#beforeHookedMethod ] {"desc":"","path":"/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/sns/temp/b997fbe5601fdfd78aab6d9899fb3383","qbf":"","qbg":"","qbh":"","thumbPath":"","cCt":-1,"fileSize":0,"filterId":0,"height":-1,"qbd":0,"qbe":0,"qbi":false,"type":2,"width":-1}
        //    java.lang.Exception
        //        at com.cc.core.wechat.hook.TestHooks$12.beforeHookedMethod(TestHooks.java:133)
        //        at com.tencent.mm.plugin.sns.storage.s.a(<Xposed>)
        //        at com.tencent.mm.plugin.sns.storage.s.dl(SourceFile:299)
        //        at com.tencent.mm.plugin.sns.model.ax.fU(SourceFile:460)
        //        at com.tencent.mm.plugin.sns.model.ax.A(SourceFile:414)
        //        at com.tencent.mm.plugin.sns.model.ax.a(SourceFile:689)
        //        at com.tencent.mm.plugin.sns.model.ax.b(SourceFile:685)
        //        at com.tencent.mm.plugin.sns.ui.z.a(SourceFile:362)
        //        at com.tencent.mm.plugin.sns.ui.SnsUploadUI$2$1.jK(SourceFile:564)
        //        at com.tencent.mm.ui.tools.b.c.dfA(SourceFile:160)
        //        at com.tencent.mm.ui.tools.b.c.a(SourceFile:112)
        //        at com.tencent.mm.plugin.sns.ui.SnsUploadUI$2.onMenuItemClick(SourceFile:535)
        //        at com.tencent.mm.ui.q.a(SourceFile:1231)
        //        at com.tencent.mm.ui.q.a(SourceFile:89)
        //        at com.tencent.mm.ui.q$13.onClick(SourceFile:1048)
        //        at android.view.View.performClick(View.java:4760)
        //        at android.view.View$PerformClick.run(View.java:19762)
        //        at android.os.Handler.handleCallback(Handler.java:739)
        //        at android.os.Handler.dispatchMessage(Handler.java:95)
        //        at android.os.Looper.loop(Looper.java:135)
        //        at android.app.ActivityThread.main(ActivityThread.java:5336)
        //        at java.lang.reflect.Method.invoke(Native Method)
        //        at java.lang.reflect.Method.invoke(Method.java:372)
        //        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:904)
        //        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:699)
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Cq <<<<<: [ (SnsUploadPackHelperHooks.java:374)#beforeHookedMethod ] 3
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Xc <<<<<: [ (SnsUploadPackHelperHooks.java:257)#beforeHookedMethod ]
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Xd <<<<<: [ (SnsUploadPackHelperHooks.java:263)#beforeHookedMethod ]
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Co <<<<<: [ (SnsUploadPackHelperHooks.java:362)#beforeHookedMethod ] 0
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Cr <<<<<: [ (SnsUploadPackHelperHooks.java:380)#beforeHookedMethod ] 0
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#de <<<<<: [ (SnsUploadPackHelperHooks.java:293)#beforeHookedMethod ] null
        //02-28 17:49:03.283 7490-7490/? E/>>>> UploadPackHelper#ar <<<<<: [ (SnsUploadPackHelperHooks.java:287)#beforeHookedMethod ] []
        //02-28 17:49:03.283 7490-7490/? E/>>>> UploadPackHelper#a <<<<<: [ (SnsUploadPackHelperHooks.java:281)#beforeHookedMethod ] {"bsZ":0.0,"qFe":0,"qFg":0,"score":0,"vQW":0,"vQX":0,"vgc":-1000.0,"vgd":-1000.0}
        //endregion share
    }
}