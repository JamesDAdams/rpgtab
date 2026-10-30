package com.rpgtab.server;

import java.util.Collection;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ChunkStatsCollector {

    public static int getLoadedChunksCount(ServerPlayer player) {
        if (player == null) return 0;
        try {
            ChunkTrackingView view = player.getChunkTrackingView();
            if (view != null) {
                int[] count = new int[1];
                view.forEach(pos -> count[0]++);
                if (count[0] > 0) {
                    return count[0];
                }
            }
            int vd = player.requestedViewDistance();
            if (vd > 0) {
                int diameter = (vd * 2) + 1;
                return diameter * diameter;
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    public static int getGeneratingChunksCount(ServerPlayer player) {
        if (player == null) return 0;
        try {
            ServerLevel level = player.serverLevel();
            if (level != null && level.getChunkSource() != null) {
                return Math.max(0, level.getChunkSource().getPendingTasksCount());
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    public static int getDhGeneratingChunksCount(ServerPlayer player) {
        if (player == null) return 0;
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
