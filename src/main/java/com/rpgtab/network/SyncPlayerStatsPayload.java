package com.rpgtab.network;

import com.rpgtab.RpgTabConstants;
import com.rpgtab.config.RpgTabConfigData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncPlayerStatsPayload(RpgTabConfigData config, List<PlayerStatsEntry> entries) implements CustomPacketPayload {

    public static final Type<SyncPlayerStatsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RpgTabConstants.MODID, "sync_player_stats"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerStatsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.write(buf),
            SyncPlayerStatsPayload::read
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        if (config != null) {
            config.write(buf);
        } else {
            new RpgTabConfigData().write(buf);
        }
        List<PlayerStatsEntry> safeEntries = entries != null ? entries : Collections.emptyList();
        int safeSize = Math.min(safeEntries.size(), RpgTabConstants.MAX_NETWORK_ENTRIES);
        buf.writeVarInt(safeSize);
        for (int i = 0; i < safeSize; i++) {
            safeEntries.get(i).write(buf);
        }
    }

    public static SyncPlayerStatsPayload read(RegistryFriendlyByteBuf buf) {
        RpgTabConfigData config = RpgTabConfigData.read(buf);
        int count = buf.readVarInt();
        if (count < 0 || count > RpgTabConstants.MAX_NETWORK_ENTRIES) {
            count = Math.max(0, Math.min(count, RpgTabConstants.MAX_NETWORK_ENTRIES));
        }
        List<PlayerStatsEntry> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(PlayerStatsEntry.read(buf));
        }
        return new SyncPlayerStatsPayload(config, list);
    }
}
