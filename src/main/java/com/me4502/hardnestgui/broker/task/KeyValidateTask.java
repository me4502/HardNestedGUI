package com.me4502.hardnestgui.broker.task;

import com.me4502.hardnestgui.app.HardNestedApplication;
import com.me4502.hardnestgui.card.CardSector;
import com.me4502.hardnestgui.card.CardStatus;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fills in any default keys, and validates custom entered keys.
 */
public class KeyValidateTask implements Task {

    // Source: http://0x9000.blogspot.com/2010/12/mifare-classic-default-keys.html
    private static final List<String> DEFAULT_KEYS = List.of(
            "ffffffffffff",
            "a0b0c0d0e0f0",
            "a1b1c1d1e1f1",
            "a0a1a2a3a4a5",
            "b0b1b2b3b4b5",
            "4d3a99c351dd",
            "1a982c7e459a",
            "000000000000",
            "d3f7d3f7d3f7",
            "aabbccddeeff"
    );

    private int currentSector = 0;
    private int retries = 0;

    @Override
    public String getName() {
        return "Key Validator";
    }

    @Override
    public Task run() throws TaskException {
        if (currentSector >= CardSector.values().length) {
            return null; // We're done here.
        }

        if (retries < 5) {
            CardSector sector = CardSector.values()[currentSector];
            CardStatus status = HardNestedApplication.getInstance().getCardStatus();
            List<String> testKeys = status.getSectorKey(sector).map(List::of).orElse(DEFAULT_KEYS);
            boolean userEntered = testKeys != DEFAULT_KEYS;

            HardNestedApplication.getInstance().addStatusMessage("Checking key for sector " + sector.name());

            String foundKey = null;

            for (String key : testKeys) {
                if (key.trim().isEmpty()) {
                    continue;
                }
                Process process = startProcess(sector, key);
                int retCode = -1;
                try {
                    retCode = process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (retCode > 1) {
                    retries++;
                    if (retries < 5) {
                        HardNestedApplication.getInstance().addStatusMessage("An error occurred in sector " + sector.name() + ". Retrying.");
                    }
                    return this;
                }
                if (userEntered) {
                    foundKey = retCode == 0 ? key : null;
                    if (retCode == 0) {
                        break;
                    }
                } else if (retCode == 0) {
                    foundKey = key;
                    break;
                }
            }

            if (userEntered && foundKey == null) {
                throw new TaskException(this, "Invalid key for sector " + sector.name());
            } else if (foundKey != null) {
                if (!userEntered) {
                    HardNestedApplication.getInstance().addStatusMessage("Sector " + sector.name() + " is using default key of: " + foundKey);
                }
                status.setSectorKey(sector, foundKey);
            }
        }

        currentSector ++;
        retries = 0;
        return this;
    }

    private int sectorToBlock(byte sector) {
        return sector * 4;
    }

    private Process startProcess(CardSector sector, String key) throws TaskException {
        try {
            var builder = new ProcessBuilder("./key_tester", key,
                    String.valueOf(sectorToBlock(sector.getNumber())), String.valueOf(sector.getSide()));
            System.out.println(String.join(" ", builder.command()));
            return builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new TaskException(this, "Failed to start process for sector " + sector.name());
        }
    }
}
