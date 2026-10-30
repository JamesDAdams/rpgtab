package com.rpgtab.test;

import com.rpgtab.client.ClientStatsCache;
import com.rpgtab.config.RpgTabConfigData;
import com.rpgtab.network.PlayerStatsEntry;
import java.util.UUID;

public class RpgTabUnitTests {

    public static void main(String[] args) {
        testClientStatsCache();
        testPlayerStatsEntryAccessors();
        testConfigDataParsing();
        System.out.println("ALL RPG TAB TESTS PASSED!");
    }

    private static void testClientStatsCache() {
        ClientStatsCache.clear();
        UUID id = UUID.randomUUID();
        PlayerStatsEntry entry = new PlayerStatsEntry(id, "TestHero", true, 24.0f, 24.0f, 19, 5.0f, 42, 0.75f, 3, 15, 27, 72000L);
        ClientStatsCache.update(entry);

        PlayerStatsEntry cached = ClientStatsCache.get(id);
        if (cached == null) {
            throw new AssertionError("Cache should not return null for inserted uuid");
        }
        if (cached.getHealth() != 24.0f || cached.getMaxHealth() != 24.0f) {
            throw new AssertionError("Unexpected health: " + cached.getHealth());
        }
        if (!"TestHero".equals(cached.getName())) {
            throw new AssertionError("Unexpected name: " + cached.getName());
        }
        if (!"1h".equals(cached.getFormattedPlayTime())) {
            throw new AssertionError("Unexpected play time: " + cached.getFormattedPlayTime());
        }
        if (cached.getXpLevel() != 42) {
            throw new AssertionError("Unexpected XP level: " + cached.getXpLevel());
        }
        if (cached.getDeaths() != 3) {
            throw new AssertionError("Unexpected deaths: " + cached.getDeaths());
        }
        if (cached.getFtbQuestsCount() != 27) {
            throw new AssertionError("Unexpected FTB quests count: " + cached.getFtbQuestsCount());
        }
        if (cached.getAdvancementsCount() != 15) {
            throw new AssertionError("Unexpected advancements count: " + cached.getAdvancementsCount());
        }
        System.out.println("[PASS] ClientStatsCache tests");
    }

    private static void testPlayerStatsEntryAccessors() {
        UUID id = UUID.nameUUIDFromBytes("Player1".getBytes());
        PlayerStatsEntry entry = new PlayerStatsEntry(id, "Player1", false, 20.0f, 20.0f, 20, 5.0f, 100, 0.0f, 0, 50, 100, 150000L);
        if (!entry.getUuid().equals(id)) {
            throw new AssertionError("UUID mismatch");
        }
        if (entry.isOnline()) {
            throw new AssertionError("Expected offline");
        }
        if (entry.getFoodLevel() != 20 || entry.getSaturation() != 5.0f) {
            throw new AssertionError("Food stats mismatch");
        }
        if (!"2h 5m".equals(entry.getFormattedPlayTime())) {
            throw new AssertionError("Playtime formatting mismatch: " + entry.getFormattedPlayTime());
        }
        System.out.println("[PASS] PlayerStatsEntry accessor tests");
    }

    private static void testConfigDataParsing() {
        int color1 = RpgTabConfigData.parseColor("0xFFE5A93C", 0);
        if (color1 != 0xFFE5A93C) {
            throw new AssertionError("Failed to parse 0x hex: " + Integer.toHexString(color1));
        }

        int color2 = RpgTabConfigData.parseColor("#E5A93C", 0);
        if (color2 != 0xFFE5A93C) {
            throw new AssertionError("Failed to parse # RGB hex: " + Integer.toHexString(color2));
        }

        RpgTabConfigData data = new RpgTabConfigData();
        if (!data.showOfflinePlayers || !data.showPlayTime || !data.showHealth) {
            throw new AssertionError("Default config values mismatch");
        }
        System.out.println("[PASS] RpgTabConfigData parsing tests");
    }
}
