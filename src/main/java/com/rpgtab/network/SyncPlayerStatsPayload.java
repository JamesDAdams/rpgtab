package com.rpgtab.network;

import com.rpgtab.config.RpgTabConfigData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncPlayerStatsPayload(RpgTabConfigData config, List<PlayerStatsEntry> entries) implements CustomPacketPayload {

    public static final Type<SyncPlayerStatsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("rpgtab", "sync_player_stats"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerStatsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.write(buf),
            SyncPlayerStatsPayload::read
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        config.write(buf);
        buf.writeVarInt(entries.size());
        for (PlayerStatsEntry entry : entries) {
            entry.write(buf);
        }
    }

    public static SyncPlayerStatsPayload read(RegistryFriendlyByteBuf buf) {
        RpgTabConfigData config = RpgTabConfigData.read(buf);
        int count = buf.readVarInt();
        List<PlayerStatsEntry> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(PlayerStatsEntry.read(buf));
        }
        return new SyncPlayerStatsPayload(config, list);
    }
}
