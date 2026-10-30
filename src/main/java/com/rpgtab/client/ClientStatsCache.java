package com.rpgtab.client;

import com.rpgtab.config.RpgTabConfigData;
import com.rpgtab.network.PlayerStatsEntry;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientStatsCache {
    private static final Map<UUID, PlayerStatsEntry> CACHE = new ConcurrentHashMap<>();
    private static RpgTabConfigData currentConfig = new RpgTabConfigData();
    private static com.rpgtab.server.ServerHardwareStats currentServerStats = null;

    public static void setConfig(RpgTabConfigData config) {
        if (config != null) {
            currentConfig = config;
        }
    }

    public static RpgTabConfigData getConfig() {
        return currentConfig;
    }

    public static void setServerStats(com.rpgtab.server.ServerHardwareStats stats) {
        currentServerStats = stats;
    }

    public static com.rpgtab.server.ServerHardwareStats getServerStats() {
        return currentServerStats;
    }

    public static void update(PlayerStatsEntry entry) {
        CACHE.put(entry.getUuid(), entry);
    }

    public static PlayerStatsEntry get(UUID uuid) {
        return CACHE.get(uuid);
    }

    public static Collection<PlayerStatsEntry> getAll() {
        return CACHE.values();
    }

    public static void clear() {
        CACHE.clear();
    }
}
