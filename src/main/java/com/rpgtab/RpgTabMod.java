package com.rpgtab;

import com.rpgtab.client.ClientStatsCache;
import com.rpgtab.config.RpgTabServerConfig;
import com.rpgtab.network.PlayerStatsEntry;
import com.rpgtab.network.SyncPlayerStatsPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(RpgTabConstants.MODID)
public class RpgTabMod {

    public static final String MODID = RpgTabConstants.MODID;

    public RpgTabMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, RpgTabServerConfig.SPEC, "rpgtab-server.toml");
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RpgTabConstants.PROTOCOL_VERSION).optional();
        registrar.playToClient(
                SyncPlayerStatsPayload.TYPE,
                SyncPlayerStatsPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ClientStatsCache.setConfig(payload.config());
                    for (PlayerStatsEntry entry : payload.entries()) {
                        ClientStatsCache.update(entry);
                    }
                })
        );
    }
}

