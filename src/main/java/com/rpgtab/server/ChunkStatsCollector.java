package com.rpgtab.server;

import java.util.Collection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class ChunkStatsCollector {

    public static int getTotalLoadedChunks(MinecraftServer server) {
        if (server == null) return 0;
        int total = 0;
        try {
            for (ServerLevel level : server.getAllLevels()) {
                if (level.getChunkSource() != null) {
                    total += level.getChunkSource().getLoadedChunksCount();
                }
            }
        } catch (Throwable ignored) {}
        return total;
    }

    public static int getTotalGeneratingChunks(MinecraftServer server) {
        if (server == null) return 0;
        int total = 0;
        try {
            for (ServerLevel level : server.getAllLevels()) {
                if (level.getChunkSource() != null) {
                    total += level.getChunkSource().getPendingTasksCount();
                }
            }
        } catch (Throwable ignored) {}
        return total;
    }

    public static int getDhGeneratingChunksCount() {
        try {
            Class<?> dhQueueClass = Class.forName("com.seibel.distanthorizons.core.generation.queues.WorldGenerationQueue");
            java.lang.reflect.Field instancesField = null;
            for (java.lang.reflect.Field f : dhQueueClass.getDeclaredFields()) {
                if (Collection.class.isAssignableFrom(f.getType()) || java.util.Map.class.isAssignableFrom(f.getType())) {
                    instancesField = f;
                    break;
                }
            }
            if (instancesField != null) {
                instancesField.setAccessible(true);
                Object val = instancesField.get(null);
                if (val instanceof Collection<?> col) {
                    int total = 0;
                    for (Object q : col) {
                        int queued = (int) q.getClass().getMethod("getQueuedChunkCount").invoke(q);
                        total += queued;
                    }
                    return total;
                }
            }
        } catch (Throwable ignored) {}
        return 0;
    }
}
