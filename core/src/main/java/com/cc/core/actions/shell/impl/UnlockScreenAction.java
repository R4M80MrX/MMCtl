package com.cc.core.actions.shell.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.utils.DeviceUtils;

public class UnlockScreenAction implements Action {
    @Override
    public ActionResult execute(String actionId, Object... args) {
        DeviceUtils.unlockScreen();
        return ActionResult.Companion.successResult(actionId);
    }

    @Override
    public String key() {
        return "UnlockScreen";
    }
}
