package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.invoke.UploadSnsAction
import com.cc.core.wechat.model.sns.SnsInfo
import java.util.ArrayList
import android.media.MediaPlayer
import android.text.TextUtils
import com.cc.core.log.KLog
import com.cc.core.utils.FileUtil
import com.cc.core.utils.ImageUtil
import java.io.File
import java.lang.Exception


class UploadSns : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No sns was inputted!")
        }
        var sns : SnsInfo = if (args[0] is SnsInfo) {
            args[0] as SnsInfo
        } else {
            StrUtils.fromJson(args[0].toString(), SnsInfo::class.java)
        }

        if (sns.getSnsType() != SnsInfo.TEXT_TYPE) {
            downloadMedia(sns)
            ImageUtil.fileToWxThumb(sns.getMedias()!![0])
            if (sns.getSnsType() == SnsInfo.VIDEO_TYPE) {
                if (sns.getMedias() == null || sns.getMedias()!!.isEmpty() || TextUtils.isEmpty(sns.getMedias()!![0])) {
                    return ActionResult.failedResult(actionId, "no video was found!")
                } else if (!verifyVideoDuration(sns.getMedias()!![0])) {
                    return ActionResult.failedResult(actionId, "The duration of sns video must less than 10 minutes.")
                }
            }
        }
        return Actions.execute(UploadSnsAction::class.java, actionId, StrUtils.toJson(sns))
    }

    private fun downloadMedia(sns:SnsInfo) {
        if (sns.getMedias() == null) {
            return
        }


        try {
            var localPath = ArrayList<String>()
            for (url in sns.getMedias()!!) {
                if (checkIsLocalFilePath(url)) {
                    localPath.add(url)
                    continue
                }
                val path = Utils.downloadFile(url, sns.getSnsType() == SnsInfo.VIDEO_TYPE)
                localPath.add(path)
            }
            sns.setMedias(localPath)
        } catch (e : Exception) {
            KLog.e("failed to download media")
        }

    }

    private fun checkIsLocalFilePath(url : String) : Boolean {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false
        }

        return File(url).exists()
    }

    private fun verifyVideoDuration(videoPath : String?) : Boolean {
        return try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(videoPath)
            mediaPlayer.prepare()
            mediaPlayer.duration <= 10000
        } catch (e : Exception) {
            KLog.e("failed to verify video duration, video path:$videoPath", e)
            false
        }
    }

    override fun key(): String? {
        return "uploadSns"
    }
}