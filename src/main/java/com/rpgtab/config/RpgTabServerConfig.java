package com.rpgtab.config;

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
                .define("title", "ADVENTURERS OF THE REALM");
        titleColor = builder.comment("Couleur du titre en hex (ex: 0xFFE5A93C)")
                .define("titleColor", "0xFFE5A93C");
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
        builder.pop();

        builder.push("colors");
        healthColor = builder.comment("Couleur texte PV (hex)").define("healthColor", "0xFFFF7777");
        foodColor = builder.comment("Couleur texte Faim (hex)").define("foodColor", "0xFFFBBF24");
        xpColor = builder.comment("Couleur texte XP (hex)").define("xpColor", "0xFF4ADE80");
        deathsColor = builder.comment("Couleur texte Morts (hex)").define("deathsColor", "0xFFE2E8F0");
        ftbColor = builder.comment("Couleur texte Quetes FTB (hex)").define("ftbColor", "0xFF38BDF8");
        advColor = builder.comment("Couleur texte Progres (hex)").define("advColor", "0xFFFACC15");
        playTimeColor = builder.comment("Couleur texte Temps de jeu (hex)").define("playTimeColor", "0xFFA78BFA");
        builder.pop();
    }
}
