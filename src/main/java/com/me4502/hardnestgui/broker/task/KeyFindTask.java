package com.me4502.hardnestgui.broker.task;

import com.me4502.hardnestgui.app.HardNestedApplication;

public class KeyFindTask implements Task {

    @Override
    public String getName() {
        return "Key Finder";
    }

    @Override
    public Task run() throws TaskException {
        if (!HardNestedApplication.getInstance().getCardStatus().hasAnyKeys()) {
            throw new TaskException(this, "At least one key must be known in order to crack!");
        }

        return null;
    }
}
