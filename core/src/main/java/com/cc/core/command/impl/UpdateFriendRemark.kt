package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.wechat.invoke.UpdateFriendRemarkAction

class UpdateFriendRemark : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        return Actions.execute(UpdateFriendRemarkAction::class.java, actionId, *args)
    }

    override fun key(): String? {
        return "updateFriendRemark"
    }
}