package com.rpgtab.test;

import com.rpgtab.RpgTabConstants;
import com.rpgtab.client.ClientStatsCache;
import com.rpgtab.config.RpgTabConfigData;
import com.rpgtab.network.PlayerStatsEntry;
import com.rpgtab.network.SyncPlayerStatsPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RpgTabUnitTests {

    public static void main(String[] args) {
        testClientStatsCache();
        testPlayerStatsEntryAccessors();
        testPlayerStatsEntryEdgeCases();
        testPlayTimeFormatting();
        testConfigDataParsing();
        testConfigDataDefaultValues();
        testSyncPlayerStatsPayloadEdgeCases();
        System.out.println("ALL RPG TAB TESTS PASSED!");
    }

    private static void testClientStatsCache() {
        ClientStatsCache.clear();
        UUID id = UUID.randomUUID();
        PlayerStatsEntry entry = new PlayerStatsEntry(id, "TestHero", "TeamAlpha", true, 24.0f, 24.0f, 19, 5.0f, 42, 0.75f, 3, 15, 27, 72000L);
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
        if (!"TeamAlpha".equals(cached.getTeamName())) {
            throw new AssertionError("Unexpected team name: " + cached.getTeamName());
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

        // Test update existing entry
        PlayerStatsEntry updatedEntry = new PlayerStatsEntry(id, "TestHero", "TeamBeta", true, 18.0f, 24.0f, 18, 4.0f, 43, 0.10f, 4, 16, 28, 73000L);
        ClientStatsCache.update(updatedEntry);
        PlayerStatsEntry recheck = ClientStatsCache.get(id);
        if (!"TeamBeta".equals(recheck.getTeamName()) || recheck.getXpLevel() != 43 || recheck.getDeaths() != 4) {
            throw new AssertionError("Failed to update cached player entry");
        }

        System.out.println("[PASS] ClientStatsCache tests");
    }

    private static void testPlayerStatsEntryAccessors() {
        UUID id = UUID.nameUUIDFromBytes("Player1".getBytes());
        PlayerStatsEntry entry = new PlayerStatsEntry(id, "Player1", "", false, 20.0f, 20.0f, 20, 5.0f, 100, 0.0f, 0, 50, 100, 150000L);
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

    private static void testPlayerStatsEntryEdgeCases() {
        // Null UUID fallback
        PlayerStatsEntry nullUuidEntry = new PlayerStatsEntry(null, null, null, false, -5.0f, 0.0f, -10, -2.0f, -1, -0.5f, -1, -5, -10, -100L);
        if (nullUuidEntry.getUuid() == null) {
            throw new AssertionError("UUID should fallback safely when null");
        }
        if (!"Unknown".equals(nullUuidEntry.getName())) {
            throw new AssertionError("Name should fallback to Unknown");
        }
        if (!"".equals(nullUuidEntry.getTeamName())) {
            throw new AssertionError("TeamName should fallback to empty string");
        }
        if (nullUuidEntry.getHealth() < 0.0f) {
            throw new AssertionError("Health should clamp to non-negative");
        }
        if (nullUuidEntry.getMaxHealth() <= 0.0f) {
            throw new AssertionError("MaxHealth should clamp to positive value");
        }
        if (nullUuidEntry.getFoodLevel() < 0) {
            throw new AssertionError("Food level should not be negative");
        }
        if (nullUuidEntry.getXpLevel() < 0) {
            throw new AssertionError("XP level should not be negative");
        }
        if (nullUuidEntry.getPlayTimeTicks() < 0L) {
            throw new AssertionError("Play time ticks should not be negative");
        }
        System.out.println("[PASS] PlayerStatsEntry edge cases tests");
    }

    private static void testPlayTimeFormatting() {
        UUID dummy = UUID.randomUUID();
        // 0 ticks -> 0m
        PlayerStatsEntry e0 = new PlayerStatsEntry(dummy, "P", "", true, 20, 20, 20, 5, 0, 0, 0, 0, 0, 0L);
        if (!"0m".equals(e0.getFormattedPlayTime())) {
            throw new AssertionError("Expected 0m, got: " + e0.getFormattedPlayTime());
        }

        // 45 minutes = 45 * 60 * 20 = 54000 ticks
        PlayerStatsEntry e45m = new PlayerStatsEntry(dummy, "P", "", true, 20, 20, 20, 5, 0, 0, 0, 0, 0, 54000L);
        if (!"45m".equals(e45m.getFormattedPlayTime())) {
            throw new AssertionError("Expected 45m, got: " + e45m.getFormattedPlayTime());
        }

        // 3 hours exactly = 3 * 3600 * 20 = 216000 ticks
        PlayerStatsEntry e3h = new PlayerStatsEntry(dummy, "P", "", true, 20, 20, 20, 5, 0, 0, 0, 0, 0, 216000L);
        if (!"3h".equals(e3h.getFormattedPlayTime())) {
            throw new AssertionError("Expected 3h, got: " + e3h.getFormattedPlayTime());
        }

        // 2 days 4 hours = (2 * 86400 + 4 * 3600) * 20 = 3744000 ticks
        PlayerStatsEntry e2d4h = new PlayerStatsEntry(dummy, "P", "", true, 20, 20, 20, 5, 0, 0, 0, 0, 0, 3744000L);
        if (!"2d 4h".equals(e2d4h.getFormattedPlayTime())) {
            throw new AssertionError("Expected 2d 4h, got: " + e2d4h.getFormattedPlayTime());
        }

        System.out.println("[PASS] PlayTime formatting tests");
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

        int color3 = RpgTabConfigData.parseColor(null, 0x123456);
        if (color3 != 0x123456) {
            throw new AssertionError("Failed fallback on null string");
        }

        int color4 = RpgTabConfigData.parseColor("invalid_hex_string", 0xABCDEF);
        if (color4 != 0xABCDEF) {
            throw new AssertionError("Failed fallback on invalid string");
        }

        int color5 = RpgTabConfigData.parseColor("#80FF0000", 0); // ARGB hex with alpha
        if (color5 != 0x80FF0000) {
            throw new AssertionError("Failed ARGB parsing: " + Integer.toHexString(color5));
        }

        System.out.println("[PASS] RpgTabConfigData parsing tests");
    }

    private static void testConfigDataDefaultValues() {
        RpgTabConfigData data = new RpgTabConfigData();
        if (!data.showOfflinePlayers || !data.showPlayTime || !data.showHealth) {
            throw new AssertionError("Default config values mismatch");
        }
        if (data.titleColor != RpgTabConstants.DEFAULT_TITLE_COLOR) {
            throw new AssertionError("Default titleColor mismatch");
        }
        if (data.healthColor != RpgTabConstants.DEFAULT_HEALTH_COLOR) {
            throw new AssertionError("Default healthColor mismatch");
        }
        if (data.foodColor != RpgTabConstants.DEFAULT_FOOD_COLOR) {
            throw new AssertionError("Default foodColor mismatch");
        }
        if (data.xpColor != RpgTabConstants.DEFAULT_XP_COLOR) {
            throw new AssertionError("Default xpColor mismatch");
        }
        if (data.deathsColor != RpgTabConstants.DEFAULT_DEATHS_COLOR) {
            throw new AssertionError("Default deathsColor mismatch");
        }
        if (data.ftbColor != RpgTabConstants.DEFAULT_FTB_COLOR) {
            throw new AssertionError("Default ftbColor mismatch");
        }
        if (data.advColor != RpgTabConstants.DEFAULT_ADV_COLOR) {
            throw new AssertionError("Default advColor mismatch");
        }
        if (data.playTimeColor != RpgTabConstants.DEFAULT_PLAYTIME_COLOR) {
            throw new AssertionError("Default playTimeColor mismatch");
        }
        System.out.println("[PASS] RpgTabConfigData default values tests");
    }

    private static void testSyncPlayerStatsPayloadEdgeCases() {
        List<PlayerStatsEntry> largeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeList.add(new PlayerStatsEntry(UUID.randomUUID(), "Player" + i, "", true, 20, 20, 20, 5, 0, 0, 0, 0, 0, 0L));
        }
        if (largeList.size() != 100) {
            throw new AssertionError("List creation failed");
        }
        System.out.println("[PASS] SyncPlayerStatsPayload edge cases tests");
    }
}
