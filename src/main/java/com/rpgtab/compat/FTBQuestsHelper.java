package com.rpgtab.compat;

import net.minecraft.server.level.ServerPlayer;

public class FTBQuestsHelper {
    private static Boolean isFTBLoaded = null;

    private static boolean checkLoaded() {
        if (isFTBLoaded == null) {
            try {
                Class.forName("dev.ftb.mods.ftbquests.quest.ServerQuestFile");
                isFTBLoaded = true;
            } catch (Throwable t) {
                isFTBLoaded = false;
            }
        }
        return isFTBLoaded;
    }

    public static int getCompletedQuestsCount(ServerPlayer player) {
        if (!checkLoaded()) {
            return 0;
        }
        try {
            return FTBQuestsReflection.count(player);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static int getCompletedQuestsCount(java.util.UUID uuid) {
        if (!checkLoaded()) {
            return 0;
        }
        try {
            return FTBQuestsReflection.countByUuid(uuid);
        } catch (Throwable t) {
            return 0;
        }
    }

    private static class FTBQuestsReflection {
        public static int count(ServerPlayer player) {
            dev.ftb.mods.ftbquests.quest.ServerQuestFile file = dev.ftb.mods.ftbquests.quest.ServerQuestFile.INSTANCE;
            if (file == null) {
                return 0;
            }
            java.util.Optional<dev.ftb.mods.ftbquests.quest.TeamData> opt = file.getTeamData(player);
            if (opt.isEmpty()) {
                return 0;
            }
            return countCompletedInTeamData(file, opt.get());
        }

        public static int countByUuid(java.util.UUID uuid) {
            dev.ftb.mods.ftbquests.quest.ServerQuestFile file = dev.ftb.mods.ftbquests.quest.ServerQuestFile.INSTANCE;
            if (file == null) {
                return 0;
            }

            dev.ftb.mods.ftbquests.quest.TeamData teamData = file.getNullableTeamData(uuid);
            if (teamData == null) {
                try {
                    if (dev.ftb.mods.ftbteams.api.FTBTeamsAPI.api().isManagerLoaded()) {
                        java.util.Optional<dev.ftb.mods.ftbteams.api.Team> teamOpt = dev.ftb.mods.ftbteams.api.FTBTeamsAPI.api().getManager().getTeamForPlayerID(uuid);
                        if (teamOpt.isPresent()) {
                            teamData = file.getNullableTeamData(teamOpt.get().getId());
                        }
                    }
                } catch (Throwable ignored) {}
            }

            if (teamData == null) {
                return 0;
            }
            return countCompletedInTeamData(file, teamData);
        }

        private static int countCompletedInTeamData(dev.ftb.mods.ftbquests.quest.ServerQuestFile file, dev.ftb.mods.ftbquests.quest.TeamData teamData) {
            int[] counter = new int[1];
            file.forAllQuests(q -> {
                if (teamData.isCompleted(q)) {
                    counter[0]++;
                }
            });
            return counter[0];
        }
    }
}
