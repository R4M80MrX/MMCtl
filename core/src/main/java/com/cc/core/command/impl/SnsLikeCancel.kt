package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.wechat.invoke.SnsLikeCancelAction

class SnsLikeCancel : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty() || args[0] == null) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no sns id was found!")
        }

        return Actions.execute(SnsLikeCancelAction::class.java, actionId, *args)
    }

    override fun key(): String? {
        return "snsLikeCancel"
    }
}