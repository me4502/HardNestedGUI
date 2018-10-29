package com.me4502.hardnestgui.broker.task;

import com.me4502.hardnestgui.app.HardNestedApplication;
import com.me4502.hardnestgui.card.CardSector;
import com.me4502.hardnestgui.card.CardStatus;

import java.util.List;
import java.util.Map;

public class KeyFindTask implements Task {

    @Override
    public String getName() {
        return "Key Finder";
    }

    @Override
    public Task run() throws TaskException {
        CardStatus status = HardNestedApplication.getInstance().getCardStatus();
        if (!status.hasAnyKeys()) {
            throw new TaskException(this, "At least one key must be known in order to crack!");
        }

        Map.Entry<CardSector, String> validKey = status.getKnownKeys().entrySet()
                .stream().findAny().orElseThrow(() -> new TaskException(this, "At least one key must be known in order to crack!"));

        List<CardSector> remainingSectors = status.getMissingSectors();

        if (remainingSectors.isEmpty()) {
            return null; // Complete
        } else {
            CardSector sectorToFind = remainingSectors.get(0);
            // TODO Find the key.
            String key = "oof";
            HardNestedApplication.getInstance().addStatusMessage("Found key for " + sectorToFind.name() + ": " + key);
            status.setSectorKey(sectorToFind, key);
        }

        return this;
    }
}
