package com.rpgtab.config;

import com.rpgtab.RpgTabConstants;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class RpgTabServerConfig {

    public static final RpgTabServerConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        Pair<RpgTabServerConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(RpgTabServerConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<String> title;
    public final ModConfigSpec.ConfigValue<String> titleColor;
    public final ModConfigSpec.BooleanValue showOfflinePlayers;

    // Colonnes
    public final ModConfigSpec.BooleanValue showAvatar;
    public final ModConfigSpec.BooleanValue showHealth;
    public final ModConfigSpec.BooleanValue showFood;
    public final ModConfigSpec.BooleanValue showXp;
    public final ModConfigSpec.BooleanValue showDeaths;
    public final ModConfigSpec.BooleanValue showFtbQuests;
    public final ModConfigSpec.BooleanValue showAdvancements;
    public final ModConfigSpec.BooleanValue showPlayTime;
    public final ModConfigSpec.BooleanValue showPing;
    public final ModConfigSpec.BooleanValue showFps;
    public final ModConfigSpec.BooleanValue showDimension;

    // Métriques serveur (Footer)
    public final ModConfigSpec.BooleanValue showServerFooter;
    public final ModConfigSpec.BooleanValue showServerCpu;
    public final ModConfigSpec.BooleanValue showServerRam;
    public final ModConfigSpec.BooleanValue showServerTps;
    public final ModConfigSpec.BooleanValue showServerUptime;
    public final ModConfigSpec.BooleanValue showServerNetwork;
    public final ModConfigSpec.BooleanValue showServerChunks;
    public final ModConfigSpec.BooleanValue showServerGpu;
    public final ModConfigSpec.BooleanValue showServerDh;
    public final ModConfigSpec.BooleanValue showServerSeedGen;

    // Pelican Panel API Integration
    public final ModConfigSpec.ConfigValue<String> pelicanUrl;
    public final ModConfigSpec.ConfigValue<String> pelicanApiKey;
    public final ModConfigSpec.ConfigValue<String> pelicanServerId;

    // Couleurs configurables (Format hex ARGB ou RGB, ex: "#FFE5A93C" ou "0xFFE5A93C")
    public final ModConfigSpec.ConfigValue<String> healthColor;
    public final ModConfigSpec.ConfigValue<String> foodColor;
    public final ModConfigSpec.ConfigValue<String> xpColor;
    public final ModConfigSpec.ConfigValue<String> deathsColor;
    public final ModConfigSpec.ConfigValue<String> ftbColor;
    public final ModConfigSpec.ConfigValue<String> advColor;
    public final ModConfigSpec.ConfigValue<String> playTimeColor;

    public RpgTabServerConfig(ModConfigSpec.Builder builder) {
        builder.push("general");
        title = builder.comment("Title displayed above the RPG player list")
                .define("title", RpgTabConstants.DEFAULT_TITLE);
        titleColor = builder.comment("Title color in hex format (e.g., 0xFFE5A93C)")
                .define("titleColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_TITLE_COLOR).toUpperCase());
        showOfflinePlayers = builder.comment("Display offline players who have previously played on this server")
                .define("showOfflinePlayers", true);
        builder.pop();

        builder.push("columns");
        showAvatar = builder.comment("Display player skin face/avatar").define("showAvatar", true);
        showHealth = builder.comment("Display health bar and numerical HP (e.g. 24/24)").define("showHealth", true);
        showFood = builder.comment("Display hunger points").define("showFood", true);
        showXp = builder.comment("Display player experience level").define("showXp", true);
        showDeaths = builder.comment("Display player deaths count").define("showDeaths", true);
        showFtbQuests = builder.comment("Display completed FTB Quests count").define("showFtbQuests", true);
        showAdvancements = builder.comment("Display completed vanilla advancements count").define("showAdvancements", true);
        showPlayTime = builder.comment("Display total player server playtime").define("showPlayTime", true);
        showPing = builder.comment("Display network latency / ping").define("showPing", true);
        showFps = builder.comment("Display client FPS in the top right corner of the TAB menu (GGTU compatible)").define("showFps", true);
        showDimension = builder.comment("Display current dimension of the player (Overworld, Nether, End, etc.)").define("showDimension", true);
        builder.pop();

        builder.push("server_metrics");
        showServerFooter = builder.comment("Enable the server hardware & performance footer bar at the bottom of the TAB overlay").define("showServerFooter", true);
        showServerTps = builder.comment("Display server TPS and MSPT").define("showServerTps", true);
        showServerCpu = builder.comment("Display server CPU usage percentage").define("showServerCpu", true);
        showServerRam = builder.comment("Display server RAM usage and maximum allocation").define("showServerRam", true);
        showServerUptime = builder.comment("Display server uptime since last reboot").define("showServerUptime", true);
        showServerNetwork = builder.comment("Display real-time server network throughput (DL and UL)").define("showServerNetwork", true);
        showServerChunks = builder.comment("Display loaded and generating world chunks count").define("showServerChunks", true);
        showServerGpu = builder.comment("Display server GPU and C2ME OpenCL acceleration status").define("showServerGpu", true);
        showServerDh = builder.comment("Display server Distant Horizons status").define("showServerDh", true);
        showServerSeedGen = builder.comment("Display server SeedGen status").define("showServerSeedGen", true);
        builder.pop();

        builder.push("pelican_api");
        pelicanUrl = builder.comment("Base URL of your Pelican Panel (e.g. https://panel.example.com)").define("pelicanUrl", "");
        pelicanApiKey = builder.comment(
                "Pelican Client API Key (Account Settings -> API Credentials / Tokens).\n" +
                "Required permission: 'server.resources' (Read resources / Read server metrics / WebSocket connection)."
        ).define("pelicanApiKey", "");
        pelicanServerId = builder.comment("Short server identifier on Pelican (e.g. 39de0ded)").define("pelicanServerId", "");
        builder.pop();

        builder.push("colors");
        healthColor = builder.comment("Health text color in hex").define("healthColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_HEALTH_COLOR).toUpperCase());
        foodColor = builder.comment("Food text color in hex").define("foodColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_FOOD_COLOR).toUpperCase());
        xpColor = builder.comment("XP text color in hex").define("xpColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_XP_COLOR).toUpperCase());
        deathsColor = builder.comment("Deaths text color in hex").define("deathsColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_DEATHS_COLOR).toUpperCase());
        ftbColor = builder.comment("FTB quests text color in hex").define("ftbColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_FTB_COLOR).toUpperCase());
        advColor = builder.comment("Advancements text color in hex").define("advColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_ADV_COLOR).toUpperCase());
        playTimeColor = builder.comment("Playtime text color in hex").define("playTimeColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_PLAYTIME_COLOR).toUpperCase());
        builder.pop();
    }
}
