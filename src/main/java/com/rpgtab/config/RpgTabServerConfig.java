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
        title = builder.comment("Titre affiche au dessus du tableau RPG")
                .define("title", RpgTabConstants.DEFAULT_TITLE);
        titleColor = builder.comment("Couleur du titre en hex (ex: 0xFFE5A93C)")
                .define("titleColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_TITLE_COLOR).toUpperCase());
        showOfflinePlayers = builder.comment("Afficher aussi les joueurs hors ligne ayant deja joue sur le serveur")
                .define("showOfflinePlayers", true);
        builder.pop();

        builder.push("columns");
        showAvatar = builder.comment("Afficher l'avatar/tete du joueur").define("showAvatar", true);
        showHealth = builder.comment("Afficher la barre de vie et les PV (ex: 24/24)").define("showHealth", true);
        showFood = builder.comment("Afficher la faim").define("showFood", true);
        showXp = builder.comment("Afficher le niveau d'experience").define("showXp", true);
        showDeaths = builder.comment("Afficher le nombre de morts").define("showDeaths", true);
        showFtbQuests = builder.comment("Afficher les quetes FTB Quests terminees").define("showFtbQuests", true);
        showAdvancements = builder.comment("Afficher les progres/advancements termines").define("showAdvancements", true);
        showPlayTime = builder.comment("Afficher le temps de jeu sur le serveur").define("showPlayTime", true);
        showPing = builder.comment("Afficher le ping / latence").define("showPing", true);
        showFps = builder.comment("Afficher les FPS du client en haut a droite du menu TAB (compatible GGTU)").define("showFps", true);
        showDimension = builder.comment("Afficher la dimension actuelle du joueur (Overworld, Nether, End, etc.)").define("showDimension", true);
        builder.pop();

        builder.push("server_metrics");
        showServerFooter = builder.comment("Activer le bandeau de statistiques du serveur en bas du TAB").define("showServerFooter", true);
        showServerTps = builder.comment("Afficher les TPS et MSPT du serveur").define("showServerTps", true);
        showServerCpu = builder.comment("Afficher le pourcentage CPU du serveur").define("showServerCpu", true);
        showServerRam = builder.comment("Afficher l'utilisation de la RAM du serveur").define("showServerRam", true);
        showServerUptime = builder.comment("Afficher le temps de fonctionnement (uptime) du serveur depuis le reboot").define("showServerUptime", true);
        showServerNetwork = builder.comment("Afficher le debit reseau du serveur (DL et UL)").define("showServerNetwork", true);
        showServerChunks = builder.comment("Afficher le nombre de chunks charges et en generation sur le serveur").define("showServerChunks", true);
        showServerGpu = builder.comment("Afficher le GPU du serveur et l'etat d'acceleration C2ME OpenCL").define("showServerGpu", true);
        showServerDh = builder.comment("Afficher le statut Distant Horizons cote serveur").define("showServerDh", true);
        showServerSeedGen = builder.comment("Afficher le statut SeedGen cote serveur").define("showServerSeedGen", true);
        builder.pop();

        builder.push("pelican_api");
        pelicanUrl = builder.comment("URL du panel Pelican (ex: https://panel.example.com)").define("pelicanUrl", "");
        pelicanApiKey = builder.comment("Cle API Client Pelican (ex: ptlc_... ou pelican_...)").define("pelicanApiKey", "");
        pelicanServerId = builder.comment("Identifiant court du serveur Pelican (ex: 39de0ded)").define("pelicanServerId", "");
        builder.pop();

        builder.push("colors");
        healthColor = builder.comment("Couleur texte PV (hex)").define("healthColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_HEALTH_COLOR).toUpperCase());
        foodColor = builder.comment("Couleur texte Faim (hex)").define("foodColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_FOOD_COLOR).toUpperCase());
        xpColor = builder.comment("Couleur texte XP (hex)").define("xpColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_XP_COLOR).toUpperCase());
        deathsColor = builder.comment("Couleur texte Morts (hex)").define("deathsColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_DEATHS_COLOR).toUpperCase());
        ftbColor = builder.comment("Couleur texte Quetes FTB (hex)").define("ftbColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_FTB_COLOR).toUpperCase());
        advColor = builder.comment("Couleur texte Progres (hex)").define("advColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_ADV_COLOR).toUpperCase());
        playTimeColor = builder.comment("Couleur texte Temps de jeu (hex)").define("playTimeColor", "0x" + Integer.toHexString(RpgTabConstants.DEFAULT_PLAYTIME_COLOR).toUpperCase());
        builder.pop();
    }
}
