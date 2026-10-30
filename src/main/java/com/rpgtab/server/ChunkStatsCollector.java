package com.rpgtab.server;

import java.util.Collection;
import java.util.Set;
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
        int total = 0;
        try {
            Class<?> probeClass = Class.forName("mobius.seedrenderer.dh.QueueDepthProbe");
            java.lang.reflect.Field queuesField = probeClass.getDeclaredField("LIVE_QUEUES");
            queuesField.setAccessible(true);
            Object obj = queuesField.get(null);
            if (obj instanceof Set<?> queues) {
                for (Object q : queues) {
                    int inProg = (int) q.getClass().getMethod("getInProgressTaskCount").invoke(q);
                    int waiting = (int) q.getClass().getMethod("getWaitingTaskCount").invoke(q);
                    total += (inProg + waiting);
                }
                if (total > 0) return total;
            }
        } catch (Throwable ignored) {}

        try {
            Class<?> dhQueueClass = Class.forName("com.seibel.distanthorizons.core.generation.queues.WorldGenerationQueue");
            for (java.lang.reflect.Field f : dhQueueClass.getDeclaredFields()) {
                if (Collection.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    Object val = f.get(null);
                    if (val instanceof Collection<?> col) {
                        for (Object q : col) {
                            int inProg = (int) q.getClass().getMethod("getInProgressTaskCount").invoke(q);
                            int waiting = (int) q.getClass().getMethod("getWaitingTaskCount").invoke(q);
                            total += (inProg + waiting);
                        }
                        if (total > 0) return total;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return total;
    }

    public static int getSeedGenGeneratingChunksCount() {
        int total = 0;
        try {
            Class<?> probeClass = Class.forName("mobius.seedrenderer.dh.QueueDepthProbe");
            java.lang.reflect.Field queuesField = probeClass.getDeclaredField("LIVE_QUEUES");
            queuesField.setAccessible(true);
            Object obj = queuesField.get(null);
            if (obj instanceof Set<?> queues) {
                for (Object q : queues) {
                    if (q.getClass().getName().contains("SeedRetrievalQueue") || q.getClass().getName().contains("Seed")) {
                        int inProg = (int) q.getClass().getMethod("getInProgressTaskCount").invoke(q);
                        total += inProg;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return total;
    }
}
