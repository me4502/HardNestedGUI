package com.me4502.hardnestgui.broker.task;

import com.me4502.hardnestgui.app.HardNestedApplication;
import com.me4502.hardnestgui.card.CardSector;
import com.me4502.hardnestgui.card.CardStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyFindTask implements Task {

    private static final Pattern KEY_PATTERN = Pattern.compile("Found key: ([a-z0-9]*)");

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
            HardNestedApplication.getInstance().addStatusMessage("Found all keys successfully!");
            return null; // Complete
        } else {
            CardSector sectorToFind = remainingSectors.get(0);
            HardNestedApplication.getInstance().addStatusMessage("Cracking sector " + sectorToFind.name() + " (This may take a while)");

            Process process = startProcess(sectorToFind, validKey.getValue(), validKey.getKey());
            String key = null;
            try {
                process.waitFor();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = KEY_PATTERN.matcher(line);
                        if (matcher.find()) {
                            key = matcher.group(1);
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new TaskException(this, "Failed to read output of process for sector " + sectorToFind.name());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (key == null) {
                throw new TaskException(this, "Failed to crack sector " + sectorToFind.name());
            }

            HardNestedApplication.getInstance().addStatusMessage("Found key for " + sectorToFind.name() + ": " + key);
            status.setSectorKey(sectorToFind, key);
        }

        return this;
    }

    private int sectorToBlock(byte sector) {
        return sector * 4;
    }

    private Process startProcess(CardSector sector, String validKey, CardSector validSector) throws TaskException {
        try {
            var builder = new ProcessBuilder(
                    "./key_crack", validKey,
                    String.valueOf(sectorToBlock(validSector.getNumber())), String.valueOf(validSector.getSide()),
                    String.valueOf(sectorToBlock(sector.getNumber())), String.valueOf(sector.getSide())
            );
            System.out.println(String.join(" ", builder.command()));
            return builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new TaskException(this, "Failed to start process for sector " + sector.name());
        }
    }
}
