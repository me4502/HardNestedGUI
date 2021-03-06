package com.me4502.hardnestgui.card;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores the current status of a card.
 */
public class CardStatus {

    private Map<CardSector, String> sectorKeys = new EnumMap<>(CardSector.class);

    /**
     * Gets the stored key from a sector, if available
     *
     * @param sector The sector
     * @return The key
     */
    public Optional<String> getSectorKey(CardSector sector) {
        return Optional.ofNullable(sectorKeys.get(sector));
    }

    /**
     * Sets the key of a sector
     *
     * @param sector The sector
     * @param sectorKey The key
     */
    public void setSectorKey(CardSector sector, String sectorKey) {
        if (sectorKey == null || sectorKey.trim().isBlank()) {
            sectorKeys.remove(sector);
            return;
        }
        sectorKeys.put(sector, sectorKey);
    }

    /**
     * Gets a map of the current known keys.
     *
     * @return The known keys
     */
    public Map<CardSector, String> getKnownKeys() {
        return Collections.unmodifiableMap(sectorKeys);
    }

    /**
     * Gets a list of missing keys.
     *
     * @return The missing keys.
     */
    public List<CardSector> getMissingSectors() {
        List<CardSector> cardSectors = Lists.newArrayList(CardSector.values());
        cardSectors.removeAll(sectorKeys.keySet());
        return cardSectors;
    }

    /**
     * Checks if any keys for this card are available
     *
     * @return If there are any keys
     */
    public boolean hasAnyKeys() {
        return sectorKeys.size() > 0;
    }

    /**
     * Checks if all keys for this card are available.
     *
     * @return If all keys are available
     */
    public boolean hasAllKeys() {
        return sectorKeys.size() == CardSector.values().length;
    }

    /**
     * Removes all keys from this card, used for switching cards.
     */
    public void resetKeys() {
        sectorKeys.clear();
    }
}
