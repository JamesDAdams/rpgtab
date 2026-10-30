package com.rpgtab.server;

import com.mojang.authlib.GameProfile;
import com.rpgtab.compat.FTBQuestsHelper;
import com.rpgtab.config.RpgTabConfigData;
import com.rpgtab.network.PlayerStatsEntry;
import com.rpgtab.network.SyncPlayerStatsPayload;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerStatsCollector {

    public static PlayerStatsEntry collectOnline(ServerPlayer player) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int foodLevel = player.getFoodData().getFoodLevel();
        float saturation = player.getFoodData().getSaturationLevel();
        int xpLevel = player.experienceLevel;
        float xpProgress = player.experienceProgress;

        int deaths = 0;
        long playTime = 0;
        try {
            deaths = player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS));
            playTime = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        } catch (Throwable ignored) {}

        int advancementsCount = 0;
        try {
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (AdvancementHolder holder : server.getAdvancements().getAllAdvancements()) {
                    if (holder.value().display().isPresent()) {
                        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
                        if (progress.isDone()) {
                            advancementsCount++;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}

        int ftbQuests = FTBQuestsHelper.getCompletedQuestsCount(player);
        String teamName = FTBQuestsHelper.getTeamName(player.getUUID());

        return new PlayerStatsEntry(
                player.getUUID(),
                player.getGameProfile().getName(),
                teamName,
                true,
                health,
                maxHealth,
                foodLevel,
                saturation,
                xpLevel,
                xpProgress,
                deaths,
                advancementsCount,
                ftbQuests,
                playTime
        );
    }

    public static PlayerStatsEntry collectOffline(MinecraftServer server, UUID uuid, String name) {
        int deaths = 0;
        long playTime = 0;
        try {
            File statsDir = server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
            File statFile = new File(statsDir, uuid.toString() + ".json");
            if (statFile.exists()) {
                ServerStatsCounter counter = new ServerStatsCounter(server, statFile);
                deaths = counter.getValue(Stats.CUSTOM.get(Stats.DEATHS));
                playTime = counter.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            }
        } catch (Throwable ignored) {}

        int advancementsCount = 0;
        try {
            File advDir = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile();
            File advFile = new File(advDir, uuid.toString() + ".json");
            if (advFile.exists() && advFile.length() > 0) {
                String json = java.nio.file.Files.readString(advFile.toPath());
                com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : root.entrySet()) {
                    String key = entry.getKey();
                    if (!key.startsWith("minecraft:recipes/") && entry.getValue().isJsonObject()) {
                        com.google.gson.JsonObject obj = entry.getValue().getAsJsonObject();
                        if (obj.has("done") && obj.get("done").getAsBoolean()) {
                            advancementsCount++;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}

        int ftbQuests = FTBQuestsHelper.getCompletedQuestsCount(uuid);
        String teamName = FTBQuestsHelper.getTeamName(uuid);

        return new PlayerStatsEntry(
                uuid,
                name,
                teamName,
                false,
                20.0f,
                20.0f,
                20,
                5.0f,
                0,
                0.0f,
                deaths,
                advancementsCount,
                ftbQuests,
                playTime
        );
    }

    public static void syncAllPlayers(MinecraftServer server) {
        RpgTabConfigData config = RpgTabConfigData.fromServerConfig();
        List<ServerPlayer> onlinePlayers = server.getPlayerList().getPlayers();
        List<PlayerStatsEntry> entries = new ArrayList<>();
        Set<UUID> onlineUuids = new HashSet<>();

        for (ServerPlayer player : onlinePlayers) {
            entries.add(collectOnline(player));
            onlineUuids.add(player.getUUID());
        }

        if (config.showOfflinePlayers) {
            GameProfileCache profileCache = server.getProfileCache();
            if (profileCache != null) {
                try {
                    File statsDir = server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
                    if (statsDir.exists() && statsDir.isDirectory()) {
                        File[] files = statsDir.listFiles((dir, fname) -> fname.endsWith(".json"));
                        if (files != null) {
                            for (File f : files) {
                                String name = f.getName().replace(".json", "");
                                try {
                                    UUID uuid = UUID.fromString(name);
                                    if (!onlineUuids.contains(uuid)) {
                                        Optional<GameProfile> profileOpt = profileCache.get(uuid);
                                        String playerName = profileOpt.map(GameProfile::getName).orElse(name.substring(0, Math.min(8, name.length())));
                                        entries.add(collectOffline(server, uuid, playerName));
                                    }
                                } catch (IllegalArgumentException ignored) {}
                            }
                        }
                    }
                } catch (Throwable ignored) {}
            }
        }

        SyncPlayerStatsPayload payload = new SyncPlayerStatsPayload(config, entries);
        PacketDistributor.sendToAllPlayers(payload);
    }
}
