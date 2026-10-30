package com.rpgtab.network;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PlayerStatsEntry {
    private final UUID uuid;
    private final String name;
    private final String teamName;
    private final boolean online;
    private final float health;
    private final float maxHealth;
    private final int foodLevel;
    private final float saturation;
    private final int xpLevel;
    private final float xpProgress;
    private final int deaths;
    private final int advancementsCount;
    private final int ftbQuestsCount;
    private final long playTimeTicks;

    public PlayerStatsEntry(UUID uuid, String name, String teamName, boolean online, float health, float maxHealth,
                            int foodLevel, float saturation, int xpLevel, float xpProgress,
                            int deaths, int advancementsCount, int ftbQuestsCount, long playTimeTicks) {
        this.uuid = uuid != null ? uuid : new UUID(0L, 0L);
        this.name = name != null && !name.trim().isEmpty() ? name : "Unknown";
        this.teamName = teamName != null ? teamName : "";
        this.online = online;
        this.health = Math.max(0.0f, health);
        this.maxHealth = Math.max(1.0f, maxHealth);
        this.foodLevel = Math.max(0, foodLevel);
        this.saturation = Math.max(0.0f, saturation);
        this.xpLevel = Math.max(0, xpLevel);
        this.xpProgress = Math.max(0.0f, Math.min(1.0f, xpProgress));
        this.deaths = Math.max(0, deaths);
        this.advancementsCount = Math.max(0, advancementsCount);
        this.ftbQuestsCount = Math.max(0, ftbQuestsCount);
        this.playTimeTicks = Math.max(0L, playTimeTicks);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean isOnline() {
        return online;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public int getXpLevel() {
        return xpLevel;
    }

    public float getXpProgress() {
        return xpProgress;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAdvancementsCount() {
        return advancementsCount;
    }

    public int getFtbQuestsCount() {
        return ftbQuestsCount;
    }

    public long getPlayTimeTicks() {
        return playTimeTicks;
    }

    public String getFormattedPlayTime() {
        long totalSeconds = Math.max(0L, playTimeTicks) / 20L;
        long days = totalSeconds / 86400L;
        long remainingAfterDays = totalSeconds % 86400L;
        long hours = remainingAfterDays / 3600L;
        long minutes = (remainingAfterDays % 3600L) / 60L;

        if (days > 0) {
            return days + "d" + (hours > 0 ? " " + hours + "h" : "");
        } else if (hours > 0) {
            return hours + "h" + (minutes > 0 ? " " + minutes + "m" : "");
        } else {
            return minutes + "m";
        }
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeUtf(name);
        buf.writeUtf(teamName);
        buf.writeBoolean(online);
        buf.writeFloat(health);
        buf.writeFloat(maxHealth);
        buf.writeVarInt(foodLevel);
        buf.writeFloat(saturation);
        buf.writeVarInt(xpLevel);
        buf.writeFloat(xpProgress);
        buf.writeVarInt(deaths);
        buf.writeVarInt(advancementsCount);
        buf.writeVarInt(ftbQuestsCount);
        buf.writeVarLong(playTimeTicks);
    }

    public static PlayerStatsEntry read(RegistryFriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        String name = buf.readUtf();
        String teamName = buf.readUtf();
        boolean online = buf.readBoolean();
        float health = buf.readFloat();
        float maxHealth = buf.readFloat();
        int foodLevel = buf.readVarInt();
        float saturation = buf.readFloat();
        int xpLevel = buf.readVarInt();
        float xpProgress = buf.readFloat();
        int deaths = buf.readVarInt();
        int advancementsCount = buf.readVarInt();
        int ftbQuestsCount = buf.readVarInt();
        long playTimeTicks = buf.readVarLong();
        return new PlayerStatsEntry(uuid, name, teamName, online, health, maxHealth, foodLevel, saturation,
                xpLevel, xpProgress, deaths, advancementsCount, ftbQuestsCount, playTimeTicks);
    }
}
