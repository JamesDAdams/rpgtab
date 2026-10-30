package com.rpgtab.config;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class RpgTabConfigData {

    public String title = "ADVENTURERS OF THE REALM";
    public int titleColor = 0xFFE5A93C;
    public boolean showOfflinePlayers = true;

    public boolean showAvatar = true;
    public boolean showHealth = true;
    public boolean showFood = true;
    public boolean showXp = true;
    public boolean showDeaths = true;
    public boolean showFtbQuests = true;
    public boolean showAdvancements = true;
    public boolean showPlayTime = true;
    public boolean showPing = true;

    public int healthColor = 0xFFFF7777;
    public int foodColor = 0xFFFBBF24;
    public int xpColor = 0xFF4ADE80;
    public int deathsColor = 0xFFE2E8F0;
    public int ftbColor = 0xFF38BDF8;
    public int advColor = 0xFFFACC15;
    public int playTimeColor = 0xFFA78BFA;

    public static int parseColor(String str, int defaultColor) {
        if (str == null || str.trim().isEmpty()) {
            return defaultColor;
        }
        try {
            String s = str.trim();
            if (s.startsWith("0x") || s.startsWith("0X")) {
                return (int) Long.parseLong(s.substring(2), 16);
            } else if (s.startsWith("#")) {
                long val = Long.parseLong(s.substring(1), 16);
                if (s.length() <= 7) {
                    val |= 0xFF000000L;
                }
                return (int) val;
            } else {
                return (int) Long.parseLong(s, 16);
            }
        } catch (Exception e) {
            return defaultColor;
        }
    }

    public static RpgTabConfigData fromServerConfig() {
        RpgTabConfigData data = new RpgTabConfigData();
        data.title = RpgTabServerConfig.CONFIG.title.get();
        data.titleColor = parseColor(RpgTabServerConfig.CONFIG.titleColor.get(), 0xFFE5A93C);
        data.showOfflinePlayers = RpgTabServerConfig.CONFIG.showOfflinePlayers.get();

        data.showAvatar = RpgTabServerConfig.CONFIG.showAvatar.get();
        data.showHealth = RpgTabServerConfig.CONFIG.showHealth.get();
        data.showFood = RpgTabServerConfig.CONFIG.showFood.get();
        data.showXp = RpgTabServerConfig.CONFIG.showXp.get();
        data.showDeaths = RpgTabServerConfig.CONFIG.showDeaths.get();
        data.showFtbQuests = RpgTabServerConfig.CONFIG.showFtbQuests.get();
        data.showAdvancements = RpgTabServerConfig.CONFIG.showAdvancements.get();
        data.showPlayTime = RpgTabServerConfig.CONFIG.showPlayTime.get();
        data.showPing = RpgTabServerConfig.CONFIG.showPing.get();

        data.healthColor = parseColor(RpgTabServerConfig.CONFIG.healthColor.get(), 0xFFFF7777);
        data.foodColor = parseColor(RpgTabServerConfig.CONFIG.foodColor.get(), 0xFFFBBF24);
        data.xpColor = parseColor(RpgTabServerConfig.CONFIG.xpColor.get(), 0xFF4ADE80);
        data.deathsColor = parseColor(RpgTabServerConfig.CONFIG.deathsColor.get(), 0xFFE2E8F0);
        data.ftbColor = parseColor(RpgTabServerConfig.CONFIG.ftbColor.get(), 0xFF38BDF8);
        data.advColor = parseColor(RpgTabServerConfig.CONFIG.advColor.get(), 0xFFFACC15);
        data.playTimeColor = parseColor(RpgTabServerConfig.CONFIG.playTimeColor.get(), 0xFFA78BFA);

        return data;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(title);
        buf.writeInt(titleColor);
        buf.writeBoolean(showOfflinePlayers);

        buf.writeBoolean(showAvatar);
        buf.writeBoolean(showHealth);
        buf.writeBoolean(showFood);
        buf.writeBoolean(showXp);
        buf.writeBoolean(showDeaths);
        buf.writeBoolean(showFtbQuests);
        buf.writeBoolean(showAdvancements);
        buf.writeBoolean(showPlayTime);
        buf.writeBoolean(showPing);

        buf.writeInt(healthColor);
        buf.writeInt(foodColor);
        buf.writeInt(xpColor);
        buf.writeInt(deathsColor);
        buf.writeInt(ftbColor);
        buf.writeInt(advColor);
        buf.writeInt(playTimeColor);
    }

    public static RpgTabConfigData read(RegistryFriendlyByteBuf buf) {
        RpgTabConfigData data = new RpgTabConfigData();
        data.title = buf.readUtf();
        data.titleColor = buf.readInt();
        data.showOfflinePlayers = buf.readBoolean();

        data.showAvatar = buf.readBoolean();
        data.showHealth = buf.readBoolean();
        data.showFood = buf.readBoolean();
        data.showXp = buf.readBoolean();
        data.showDeaths = buf.readBoolean();
        data.showFtbQuests = buf.readBoolean();
        data.showAdvancements = buf.readBoolean();
        data.showPlayTime = buf.readBoolean();
        data.showPing = buf.readBoolean();

        data.healthColor = buf.readInt();
        data.foodColor = buf.readInt();
        data.xpColor = buf.readInt();
        data.deathsColor = buf.readInt();
        data.ftbColor = buf.readInt();
        data.advColor = buf.readInt();
        data.playTimeColor = buf.readInt();
        return data;
    }
}
