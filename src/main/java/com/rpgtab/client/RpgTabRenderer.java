package com.rpgtab.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rpgtab.config.RpgTabConfigData;
import com.rpgtab.network.PlayerStatsEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RpgTabRenderer {

    private static final int HEADER_HEIGHT = 40;
    private static final int ROW_HEIGHT = 24;
    private static final int FOOTER_HEIGHT = 16;
    private static final int PADDING_X = 14;

    private static final int COL_AVATAR = 22;
    private static final int COL_NAME = 100;
    private static final int COL_DIM = 65;
    private static final int COL_HEALTH = 95;
    private static final int COL_FOOD = 55;
    private static final int COL_XP = 50;
    private static final int COL_DEATHS = 45;
    private static final int COL_QUESTS = 48;
    private static final int COL_ADV = 48;
    private static final int COL_PLAYTIME = 55;
    private static final int COL_PING = 42;

    public static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        RpgTabConfigData cfg = ClientStatsCache.getConfig();
        ClientPacketListener connection = mc.getConnection();
        Map<UUID, PlayerInfo> onlineMap = new HashMap<>();
        if (connection != null) {
            for (PlayerInfo p : connection.getListedOnlinePlayers()) {
                onlineMap.put(p.getProfile().getId(), p);
            }
        }

        // Regroupe les données des joueurs
        Collection<PlayerStatsEntry> allEntries = ClientStatsCache.getAll();
        Map<UUID, PlayerStatsEntry> entryMap = new HashMap<>();
        for (PlayerStatsEntry e : allEntries) {
            entryMap.put(e.getUuid(), e);
        }

        // Si le joueur local n'est pas encore dans le cache, on crée une entrée temporaire
        if (!entryMap.containsKey(mc.player.getUUID())) {
            entryMap.put(mc.player.getUUID(), new PlayerStatsEntry(
                    mc.player.getUUID(),
                    mc.player.getGameProfile().getName(),
                    "",
                    true,
                    mc.player.getHealth(),
                    mc.player.getMaxHealth(),
                    mc.player.getFoodData().getFoodLevel(),
                    mc.player.getFoodData().getSaturationLevel(),
                    mc.player.experienceLevel,
                    mc.player.experienceProgress,
                    0, 0, 0, 0
            ));
        }

        // Filtre les joueurs selon l'option showOfflinePlayers
        List<PlayerStatsEntry> displayList = new ArrayList<>();
        for (PlayerStatsEntry entry : entryMap.values()) {
            boolean isOnline = onlineMap.containsKey(entry.getUuid()) || entry.isOnline();
            if (isOnline || cfg.showOfflinePlayers) {
                displayList.add(entry);
            }
        }

        if (displayList.isEmpty()) {
            return;
        }

        // Tri : joueur local en premier, puis en ligne par nom, puis hors ligne
        UUID selfId = mc.player.getUUID();
        displayList.sort((a, b) -> {
            if (a.getUuid().equals(selfId)) return -1;
            if (b.getUuid().equals(selfId)) return 1;
            boolean aOnline = onlineMap.containsKey(a.getUuid()) || a.isOnline();
            boolean bOnline = onlineMap.containsKey(b.getUuid()) || b.isOnline();
            if (aOnline != bOnline) {
                return aOnline ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        // Calcul dynamique de la largeur selon les colonnes actives
        int computedWidth = COL_NAME;
        if (cfg.showAvatar) computedWidth += COL_AVATAR;
        if (cfg.showDimension) computedWidth += COL_DIM;
        if (cfg.showHealth) computedWidth += COL_HEALTH;
        if (cfg.showFood) computedWidth += COL_FOOD;
        if (cfg.showXp) computedWidth += COL_XP;
        if (cfg.showDeaths) computedWidth += COL_DEATHS;
        if (cfg.showFtbQuests) computedWidth += COL_QUESTS;
        if (cfg.showAdvancements) computedWidth += COL_ADV;
        if (cfg.showPlayTime) computedWidth += COL_PLAYTIME;
        if (cfg.showPing) computedWidth += COL_PING;
        computedWidth += (PADDING_X * 2);

        int totalWidth = computedWidth;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int maxRows = Math.min(displayList.size(), 14);
        int rowsHeight = maxRows * ROW_HEIGHT;
        com.rpgtab.server.ServerHardwareStats serverStats = ClientStatsCache.getServerStats();
        boolean hasFooterMetrics = cfg.showServerFooter && serverStats != null;
        int footerHeight = hasFooterMetrics ? (serverStats.getGpuName().isEmpty() && !serverStats.isC2meOpenclLoaded() ? 24 : 36) : FOOTER_HEIGHT;
        int totalHeight = HEADER_HEIGHT + rowsHeight + footerHeight;

        int startX = (screenWidth - totalWidth) / 2;
        int startY = 16;
        if (startY + totalHeight > screenHeight - 10) {
            startY = Math.max(5, screenHeight - totalHeight - 10);
        }

        Font font = mc.font;

        // Mise au premier plan absolu :
        // 1. Flush de tout buffer en attente
        graphics.flush();
        // 2. Push pose avec offset Z
        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, 800.0f);
        // 3. Désactivation depth test pour écraser tout le reste
        RenderSystem.disableDepthTest();

        // 1. Fond du cadre RPG
        drawRpgPanel(graphics, startX, startY, totalWidth, totalHeight);

        // 2. Titre et sous-titre
        String title = cfg.title != null && !cfg.title.isEmpty() ? cfg.title : "ADVENTURERS OF THE REALM";
        int titleW = font.width(title);
        graphics.drawString(font, title, startX + (totalWidth - titleW) / 2, startY + 6, cfg.titleColor, true);

        // Affichage des FPS client en haut a droite (compatible GGTU)
        if (cfg.showFps) {
            String fpsStr = GgtuFpsHelper.getFormattedFps();
            int fpsW = font.width(fpsStr);
            graphics.drawString(font, fpsStr, startX + totalWidth - fpsW - PADDING_X, startY + 6, 0xFF4ADE80, false);
        }

        long onlineCount = displayList.stream().filter(e -> onlineMap.containsKey(e.getUuid()) || e.isOnline()).count();
        String countStr = onlineCount + " Online" + (cfg.showOfflinePlayers && displayList.size() > onlineCount ? " (" + (displayList.size() - onlineCount) + " Offline)" : "");
        int countW = font.width(countStr);
        graphics.drawString(font, countStr, startX + (totalWidth - countW) / 2, startY + 17, 0xFFAAAAAA, false);

        // Ligne de séparation sous le header
        int lineY = startY + 27;
        graphics.fill(startX + 10, lineY, startX + totalWidth - 10, lineY + 1, 0x44FFFFFF);

        // 3. En-têtes de colonnes
        int colY = startY + 29;
        int currentX = startX + PADDING_X;
        if (cfg.showAvatar) {
            currentX += COL_AVATAR;
        }

        graphics.drawString(font, "Player", currentX + 2, colY, 0xFFC0C0C0, false);
        currentX += COL_NAME;

        if (cfg.showDimension) {
            graphics.drawString(font, "✦ Dim", currentX + 2, colY, 0xFF93C5FD, false);
            currentX += COL_DIM;
        }

        if (cfg.showHealth) {
            graphics.drawString(font, "\u2764 Health", currentX + 2, colY, cfg.healthColor, false);
            currentX += COL_HEALTH;
        }
        if (cfg.showFood) {
            graphics.drawString(font, "\u25CB Food", currentX + 2, colY, cfg.foodColor, false);
            currentX += COL_FOOD;
        }
        if (cfg.showXp) {
            graphics.drawString(font, "\u2605 XP", currentX + 2, colY, cfg.xpColor, false);
            currentX += COL_XP;
        }
        if (cfg.showDeaths) {
            graphics.drawString(font, "\u2620 Deaths", currentX + 2, colY, cfg.deathsColor, false);
            currentX += COL_DEATHS;
        }
        if (cfg.showFtbQuests) {
            graphics.drawString(font, "\u2694 FTB", currentX + 2, colY, cfg.ftbColor, false);
            currentX += COL_QUESTS;
        }
        if (cfg.showAdvancements) {
            graphics.drawString(font, "\u2606 Adv.", currentX + 2, colY, cfg.advColor, false);
            currentX += COL_ADV;
        }
        if (cfg.showPlayTime) {
            graphics.drawString(font, "\u23F1 Time", currentX + 2, colY, cfg.playTimeColor, false);
            currentX += COL_PLAYTIME;
        }
        if (cfg.showPing) {
            graphics.drawString(font, "\u25CE Ping", currentX + 2, colY, 0xFF88AAFF, false);
        }

        int colDividerY = startY + HEADER_HEIGHT - 1;
        graphics.fill(startX + 6, colDividerY, startX + totalWidth - 6, colDividerY + 1, 0x33FFFFFF);

        // 4. Lignes des joueurs
        int rowY = startY + HEADER_HEIGHT;
        for (int i = 0; i < maxRows; i++) {
            PlayerStatsEntry entry = displayList.get(i);
            PlayerInfo info = onlineMap.get(entry.getUuid());
            boolean isSelf = selfId.equals(entry.getUuid());
            renderPlayerRow(graphics, font, entry, info, cfg, startX + PADDING_X, rowY, totalWidth, isSelf, i % 2 == 0);
            rowY += ROW_HEIGHT;
        }

        if (displayList.size() > maxRows) {
            String more = "+" + (displayList.size() - maxRows) + " more adventurers...";
            graphics.drawString(font, more, startX + (totalWidth - font.width(more)) / 2, rowY + 3, 0xFF888888, false);
            rowY += 12;
        }

        // 5. Bandeau Statistiques Serveur (Footer)
        if (hasFooterMetrics) {
            renderServerFooter(graphics, font, serverStats, cfg, startX, rowY + 2, totalWidth);
        }

        graphics.flush();
        RenderSystem.enableDepthTest();
        graphics.pose().popPose();
    }

    private static void renderServerFooter(GuiGraphics graphics, Font font, com.rpgtab.server.ServerHardwareStats stats,
                                           RpgTabConfigData cfg, int startX, int footerY, int totalWidth) {
        // Ligne de séparation au dessus du footer
        graphics.fill(startX + 8, footerY, startX + totalWidth - 8, footerY + 1, 0x33FFFFFF);

        List<String> parts = new ArrayList<>();
        if (cfg.showServerTps) {
            parts.add(String.format("§e⚙ TPS: §f%.1f §7(%.1fms)", stats.getTps(), stats.getMspt()));
        }
        if (cfg.showServerCpu) {
            parts.add("§b🖥 CPU: §f" + stats.getFormattedCpu());
        }
        if (cfg.showServerRam) {
            parts.add(String.format("§a💾 RAM: §f%s/%s", com.rpgtab.server.ServerHardwareStats.formatMemory(stats.getUsedRamBytes()),
                    com.rpgtab.server.ServerHardwareStats.formatMemory(stats.getMaxRamBytes())));
        }
        if (cfg.showServerNetwork) {
            parts.add(String.format("§d🌐 Net: §f↓ %s ↑ %s",
                    com.rpgtab.server.ServerHardwareStats.formatSpeed(stats.getDownloadBytesPerSec()),
                    com.rpgtab.server.ServerHardwareStats.formatSpeed(stats.getUploadBytesPerSec())));
        }
        if (cfg.showServerChunks) {
            parts.add("§2☵ " + stats.getFormattedChunks());
        }
        if (cfg.showServerUptime) {
            parts.add("§6⏱ Up: §f" + stats.getFormattedUptime());
        }

        String line1 = String.join(" §8| ", parts);
        if (!line1.isEmpty()) {
            int line1W = font.width(line1);
            graphics.drawString(font, line1, startX + (totalWidth - line1W) / 2, footerY + 4, 0xFFE0E0E0, false);
        }

        // Ligne 2 : GPU, C2ME OpenCL, Distant Horizons & SeedGen
        StringBuilder line2Sb = new StringBuilder();
        if (cfg.showServerGpu) {
            String gpuName = stats.getGpuName();
            if (!gpuName.isEmpty() && !gpuName.equalsIgnoreCase("unknown")) {
                line2Sb.append("§3⚡ GPU: §f").append(gpuName);
                String gpuStats = stats.getGpuStats();
                if (!gpuStats.isEmpty()) {
                    line2Sb.append(" §7(").append(gpuStats).append(")");
                }
            }
            if (stats.isC2meOpenclLoaded()) {
                if (line2Sb.length() > 0) line2Sb.append(" §8| ");
                line2Sb.append("§2✔ C2ME OpenCL: §aActif");
            }
        }
        if (cfg.showServerDh && stats.isDhLoaded()) {
            if (line2Sb.length() > 0) line2Sb.append(" §8| ");
            line2Sb.append("§b🌌 DH: §aActif");
        }
        if (cfg.showServerSeedGen && stats.isSeedGenLoaded()) {
            if (line2Sb.length() > 0) line2Sb.append(" §8| ");
            line2Sb.append("§e🌱 SeedGen: §aActif");
        }

        String line2 = line2Sb.toString();
        if (!line2.isEmpty()) {
            int line2W = font.width(line2);
            graphics.drawString(font, line2, startX + (totalWidth - line2W) / 2, footerY + 16, 0xFFE0E0E0, false);
        }
    }

    private static void renderPlayerRow(GuiGraphics graphics, Font font, PlayerStatsEntry entry, PlayerInfo info,
                                        RpgTabConfigData cfg, int x, int y, int totalWidth, boolean isSelf, boolean alternate) {
        boolean isOnline = info != null || entry.isOnline();

        // Fond de la ligne
        if (isSelf) {
            graphics.fill(x - 8, y, x + totalWidth - (PADDING_X * 2) + 8, y + ROW_HEIGHT, 0x28FFD700);
            graphics.fill(x - 8, y, x - 6, y + ROW_HEIGHT, 0xFFFFD700);
        } else if (alternate) {
            graphics.fill(x - 8, y, x + totalWidth - (PADDING_X * 2) + 8, y + ROW_HEIGHT, 0x14FFFFFF);
        }

        int currX = x;
        int textY = y + 7;

        // 1. Avatar
        if (cfg.showAvatar) {
            ResourceLocation skinLoc = info != null ? info.getSkin().texture() : DefaultPlayerSkin.get(entry.getUuid()).texture();
            PlayerFaceRenderer.draw(graphics, skinLoc, currX, y + 4, 16);
            currX += COL_AVATAR;
        }

        // 2. Nom du joueur & Nom de Team FTB
        String name = entry.getName();
        if (info != null && info.getTabListDisplayName() != null) {
            name = info.getTabListDisplayName().getString();
        }
        int nameColor = isSelf ? 0xFFFFD700 : (isOnline ? 0xFFFFFFFF : 0xFF777777);
        String suffix = isOnline ? "" : " (off)";
        String trimmedName = font.plainSubstrByWidth(name + suffix, COL_NAME - 10);

        String team = entry.getTeamName();
        if (team != null && !team.trim().isEmpty()) {
            String rawTeam = team.trim();
            if (rawTeam.startsWith("[") && rawTeam.endsWith("]") && rawTeam.length() > 2) {
                rawTeam = rawTeam.substring(1, rawTeam.length() - 1).trim();
            }
            if (!rawTeam.isEmpty()) {
                graphics.drawString(font, trimmedName, currX + 2, y + 3, nameColor, false);
                String trimmedTeam = font.plainSubstrByWidth(rawTeam, COL_NAME - 10);
                graphics.drawString(font, "§o" + trimmedTeam, currX + 2, y + 13, 0xFF9E9E9E, false);
            } else {
                graphics.drawString(font, trimmedName, currX + 2, textY, nameColor, false);
            }
        } else {
            graphics.drawString(font, trimmedName, currX + 2, textY, nameColor, false);
        }
        currX += COL_NAME;

        // 3. Dimension
        if (cfg.showDimension) {
            String dimStr = entry.getFormattedDimension();
            graphics.drawString(font, dimStr, currX + 2, textY, 0xFFFFFFFF, false);
            currX += COL_DIM;
        }

        // 4. Barre de vie avec texte DANS la barre de vie
        if (cfg.showHealth) {
            float hp = isOnline ? entry.getHealth() : 0.0f;
            float maxHp = Math.max(1.0f, entry.getMaxHealth());
            int barW = 70;
            int barH = 12;
            int barX = currX + 4;
            int barY = y + 6;

            // Fond noir transparent de la barre
            graphics.fill(barX, barY, barX + barW, barY + barH, 0x88000000);

            // Remplissage dynamique
            float hpPercent = Math.min(1.0f, Math.max(0.0f, hp / maxHp));
            int filledW = (int) (barW * hpPercent);
            int hpBarColor = hpPercent > 0.5f ? 0xFF22C55E : (hpPercent > 0.25f ? 0xFFF59E0B : 0xFFEF4444);
            if (filledW > 0) {
                graphics.fill(barX, barY, barX + filledW, barY + barH, hpBarColor);
            }
            // Bordure de la jauge
            graphics.renderOutline(barX - 1, barY - 1, barW + 2, barH + 2, 0x55FFFFFF);

            // Texte centré À L'INTÉRIEUR de la barre de vie
            String hpStr = isOnline ? String.format("%.0f/%.0f", hp, maxHp) : "-";
            int textW = font.width(hpStr);
            int hpTextX = barX + (barW - textW) / 2;
            int hpTextY = barY + 2;
            graphics.drawString(font, hpStr, hpTextX, hpTextY, 0xFFFFFFFF, true);

            currX += COL_HEALTH;
        }

        // 4. Nourriture
        if (cfg.showFood) {
            String foodStr = isOnline ? entry.getFoodLevel() + "/20" : "-";
            graphics.drawString(font, foodStr, currX + 4, textY, isOnline ? cfg.foodColor : 0xFF666666, false);
            currX += COL_FOOD;
        }

        // 5. XP
        if (cfg.showXp) {
            String xpStr = isOnline ? "Lvl " + entry.getXpLevel() : "-";
            graphics.drawString(font, xpStr, currX + 4, textY, isOnline ? cfg.xpColor : 0xFF666666, false);
            currX += COL_XP;
        }

        // 6. Morts
        if (cfg.showDeaths) {
            graphics.drawString(font, String.valueOf(entry.getDeaths()), currX + 6, textY, cfg.deathsColor, false);
            currX += COL_DEATHS;
        }

        // 7. Quêtes FTB
        if (cfg.showFtbQuests) {
            String ftbStr = String.valueOf(entry.getFtbQuestsCount());
            graphics.drawString(font, ftbStr, currX + 6, textY, isOnline ? cfg.ftbColor : 0xFFAAAAAA, false);
            currX += COL_QUESTS;
        }

        // 8. Avancements
        if (cfg.showAdvancements) {
            String advStr = String.valueOf(entry.getAdvancementsCount());
            graphics.drawString(font, advStr, currX + 6, textY, isOnline ? cfg.advColor : 0xFFAAAAAA, false);
            currX += COL_ADV;
        }

        // 9. Temps de jeu
        if (cfg.showPlayTime) {
            graphics.drawString(font, entry.getFormattedPlayTime(), currX + 4, textY, cfg.playTimeColor, false);
            currX += COL_PLAYTIME;
        }

        // 10. Ping
        if (cfg.showPing) {
            if (info != null) {
                int ping = info.getLatency();
                int pingColor = ping < 80 ? 0xFF22C55E : (ping < 180 ? 0xFFF59E0B : 0xFFEF4444);
                graphics.drawString(font, ping + "ms", currX + 4, textY, pingColor, false);
            } else {
                graphics.drawString(font, "off", currX + 4, textY, 0xFF777777, false);
            }
        }
    }

    private static void drawRpgPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        // Fond sombre
        graphics.fill(x, y, x + width, y + height, 0xEE12141A);

        // Double bordure dorée
        graphics.renderOutline(x, y, width, height, 0x889A7B4F);
        graphics.renderOutline(x + 2, y + 2, width - 4, height - 4, 0x445A452E);

        // Ornements dorés aux angles
        int goldAccent = 0xFFD4AF37;
        graphics.fill(x, y, x + 4, y + 2, goldAccent);
        graphics.fill(x, y, x + 2, y + 4, goldAccent);
        graphics.fill(x + width - 4, y, x + width, y + 2, goldAccent);
        graphics.fill(x + width - 2, y, x + width, y + 4, goldAccent);
        graphics.fill(x, y + height - 2, x + 4, y + height, goldAccent);
        graphics.fill(x, y + height - 4, x + 2, y + height, goldAccent);
        graphics.fill(x + width - 4, y + height - 2, x + width, y + height, goldAccent);
        graphics.fill(x + width - 2, y + height - 4, x + width, y + height, goldAccent);
    }
}
