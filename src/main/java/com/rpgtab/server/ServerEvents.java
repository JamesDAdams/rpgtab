package com.rpgtab.server;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "rpgtab")
public class ServerEvents {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        // Synchronise toutes les 20 ticks (1 seconde)
        if (tickCounter % 20 == 0) {
            MinecraftServer server = event.getServer();
            if (server != null) {
                ServerStatsCollector.syncAllPlayers(server);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().getServer() != null) {
            ServerStatsCollector.syncAllPlayers(event.getEntity().getServer());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().getServer() != null) {
            ServerStatsCollector.syncAllPlayers(event.getEntity().getServer());
        }
    }
}
