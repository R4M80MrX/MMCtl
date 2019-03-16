package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult

import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.RemoteRespHooks
import de.robv.android.xposed.XposedHelpers
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class CreateGroupAction : Action {
    private val lock = ArrayBlockingQueue<String>(1)

    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val members = ArrayList<Any?>()
        members.addAll(args.asList())

        var request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.Hook.Group.CreateGroupRequest, Wechat.WECHAT_CLASSLOADER), "", members)

        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(119) { response->
            try {
                KLog.e("+++>>>>>>", StrUtils.toJson(response))
                val resp = XposedHelpers.getObjectField(response, Wechat.Hook.NetScene.NetSceneCmdResponseBodyKey)
                var groupWechatId = XposedHelpers.getObjectField(resp, Wechat.Hook.Group.CreateGroupWechatIdField)

                groupWechatId = XposedHelpers.getObjectField(groupWechatId, Wechat.Hook.NetScene.NetSceneResponseStringBooleanValueKey) as String

                lock.offer(groupWechatId)
            } catch (e: Throwable) {
                e.printStackTrace()
                lock.offer("""${e.javaClass.name}:${e.message}""")
            }
        }

        val groupWecharId = lock.poll(30, TimeUnit.SECONDS)
        return if (TextUtils.isEmpty(groupWecharId)) {
            ActionResult.failedResult(actionId, "")
        } else if (!StrUtils.isGroupWechatId(groupWecharId)) {
            ActionResult.failedResult(actionId, groupWecharId)
        } else {
            ActionResult.successResult(actionId, groupWecharId)
        }
    }

    override fun key(): String? {
        return "wechat:createGroup"
    }
}