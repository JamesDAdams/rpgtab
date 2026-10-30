package com.rpgtab.config;

import com.rpgtab.RpgTabConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class RpgTabConfigData {

    public String title = RpgTabConstants.DEFAULT_TITLE;
    public int titleColor = RpgTabConstants.DEFAULT_TITLE_COLOR;
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

    // Métriques serveur (Footer)
    public boolean showServerFooter = true;
    public boolean showServerTps = true;
    public boolean showServerCpu = true;
    public boolean showServerRam = true;
    public boolean showServerUptime = true;
    public boolean showServerNetwork = true;
    public boolean showServerGpu = true;

    public int healthColor = RpgTabConstants.DEFAULT_HEALTH_COLOR;
    public int foodColor = RpgTabConstants.DEFAULT_FOOD_COLOR;
    public int xpColor = RpgTabConstants.DEFAULT_XP_COLOR;
    public int deathsColor = RpgTabConstants.DEFAULT_DEATHS_COLOR;
    public int ftbColor = RpgTabConstants.DEFAULT_FTB_COLOR;
    public int advColor = RpgTabConstants.DEFAULT_ADV_COLOR;
    public int playTimeColor = RpgTabConstants.DEFAULT_PLAYTIME_COLOR;

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
        data.titleColor = parseColor(RpgTabServerConfig.CONFIG.titleColor.get(), RpgTabConstants.DEFAULT_TITLE_COLOR);
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

        data.showServerFooter = RpgTabServerConfig.CONFIG.showServerFooter.get();
        data.showServerTps = RpgTabServerConfig.CONFIG.showServerTps.get();
        data.showServerCpu = RpgTabServerConfig.CONFIG.showServerCpu.get();
        data.showServerRam = RpgTabServerConfig.CONFIG.showServerRam.get();
        data.showServerUptime = RpgTabServerConfig.CONFIG.showServerUptime.get();
        data.showServerNetwork = RpgTabServerConfig.CONFIG.showServerNetwork.get();
        data.showServerGpu = RpgTabServerConfig.CONFIG.showServerGpu.get();

        data.healthColor = parseColor(RpgTabServerConfig.CONFIG.healthColor.get(), RpgTabConstants.DEFAULT_HEALTH_COLOR);
        data.foodColor = parseColor(RpgTabServerConfig.CONFIG.foodColor.get(), RpgTabConstants.DEFAULT_FOOD_COLOR);
        data.xpColor = parseColor(RpgTabServerConfig.CONFIG.xpColor.get(), RpgTabConstants.DEFAULT_XP_COLOR);
        data.deathsColor = parseColor(RpgTabServerConfig.CONFIG.deathsColor.get(), RpgTabConstants.DEFAULT_DEATHS_COLOR);
        data.ftbColor = parseColor(RpgTabServerConfig.CONFIG.ftbColor.get(), RpgTabConstants.DEFAULT_FTB_COLOR);
        data.advColor = parseColor(RpgTabServerConfig.CONFIG.advColor.get(), RpgTabConstants.DEFAULT_ADV_COLOR);
        data.playTimeColor = parseColor(RpgTabServerConfig.CONFIG.playTimeColor.get(), RpgTabConstants.DEFAULT_PLAYTIME_COLOR);

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

        buf.writeBoolean(showServerFooter);
        buf.writeBoolean(showServerTps);
        buf.writeBoolean(showServerCpu);
        buf.writeBoolean(showServerRam);
        buf.writeBoolean(showServerUptime);
        buf.writeBoolean(showServerNetwork);
        buf.writeBoolean(showServerGpu);

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

        data.showServerFooter = buf.readBoolean();
        data.showServerTps = buf.readBoolean();
        data.showServerCpu = buf.readBoolean();
        data.showServerRam = buf.readBoolean();
        data.showServerUptime = buf.readBoolean();
        data.showServerNetwork = buf.readBoolean();
        data.showServerGpu = buf.readBoolean();

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
