package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.model.sns.SnsInfo
import de.robv.android.xposed.XposedHelpers.callMethod

class UploadSnsAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No sns was inputted!")
        }
        val snsInfo = StrUtils.fromJson(args[0].toString(), SnsInfo::class.java)
        val uploadPackHelper = SnsUtils.generateSnsUploadPackHelper(snsInfo)

        return try {
            callMethod(uploadPackHelper, "commit")
            SnsUtils.uploadSns()

            ActionResult.successResult(actionId)
        } catch (thr: Throwable) {
            ActionResult.failedResult(actionId, thr.localizedMessage)
        }
    }

    override fun key(): String? {
        return "wechat:uploadSns"
    }
}