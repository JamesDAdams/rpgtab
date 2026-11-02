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
        // 1. Check Distant Horizons ThreadPoolUtil world gen / builder executor
        try {
            Class<?> threadPoolClass = Class.forName("com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil");
            Object genExec = threadPoolClass.getMethod("getWorldGenExecutor").invoke(null);
            if (genExec != null) {
                int running = (int) genExec.getClass().getMethod("getRunningTaskCount").invoke(genExec);
                int queue = (int) genExec.getClass().getMethod("getQueueSize").invoke(genExec);
                total += (running + queue);
            }
            Object lodExec = threadPoolClass.getMethod("getChunkToLodBuilderExecutor").invoke(null);
            if (lodExec != null) {
                int running = (int) lodExec.getClass().getMethod("getRunningTaskCount").invoke(lodExec);
                int queue = (int) lodExec.getClass().getMethod("getQueueSize").invoke(lodExec);
                total += (running + queue);
            }
            if (total > 0) return total;
        } catch (Throwable ignored) {}

        // 2. Check QueueDepthProbe LIVE_QUEUES
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

    public static int getViewDistance(MinecraftServer server) {
        if (server == null) return 10;
        try {
            Class<?> settingClass = Class.forName("me.wesley1808.servercore.common.dynamic.DynamicSetting");
            Object viewSetting = settingClass.getField("VIEW_DISTANCE").get(null);
            if (viewSetting != null) {
                int val = (int) settingClass.getMethod("get").invoke(viewSetting);
                if (val > 0) return val;
            }
        } catch (Throwable ignored) {}

        try {
            if (server.getPlayerList() != null) {
                return server.getPlayerList().getViewDistance();
            }
        } catch (Throwable ignored) {}
        return 10;
    }

    public static int getSimulationDistance(MinecraftServer server) {
        if (server == null) return 10;
        try {
            Class<?> settingClass = Class.forName("me.wesley1808.servercore.common.dynamic.DynamicSetting");
            Object simSetting = settingClass.getField("SIMULATION_DISTANCE").get(null);
            if (simSetting != null) {
                int val = (int) settingClass.getMethod("get").invoke(simSetting);
                if (val > 0) return val;
            }
        } catch (Throwable ignored) {}

        try {
            if (server.getPlayerList() != null) {
                return server.getPlayerList().getSimulationDistance();
            }
        } catch (Throwable ignored) {}
        return 10;
    }
}
