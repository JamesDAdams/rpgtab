package com.rpgtab.server;

import java.io.Serializable;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ServerHardwareStats implements Serializable {

    private final float tps;
    private final float mspt;
    private final double cpuUsage;
    private final int maxCpuPercent;
    private final long usedRamBytes;
    private final long maxRamBytes;
    private final long uptimeSeconds;
    private final long downloadBytesPerSec;
    private final long uploadBytesPerSec;
    private final int totalLoadedChunks;
    private final int totalGeneratingChunks;
    private final int dhGeneratingChunks;
    private final boolean c2meOpenclLoaded;
    private final boolean dhLoaded;
    private final boolean seedGenLoaded;
    private final String gpuName;
    private final String gpuStats;

    public ServerHardwareStats(float tps, float mspt, double cpuUsage, int maxCpuPercent,
                               long usedRamBytes, long maxRamBytes, long uptimeSeconds,
                               long downloadBytesPerSec, long uploadBytesPerSec,
                               int totalLoadedChunks, int totalGeneratingChunks, int dhGeneratingChunks,
                               boolean c2meOpenclLoaded, boolean dhLoaded, boolean seedGenLoaded,
                               String gpuName, String gpuStats) {
        this.tps = tps;
        this.mspt = mspt;
        this.cpuUsage = cpuUsage;
        this.maxCpuPercent = maxCpuPercent > 0 ? maxCpuPercent : 100;
        this.usedRamBytes = usedRamBytes;
        this.maxRamBytes = maxRamBytes;
        this.uptimeSeconds = uptimeSeconds;
        this.downloadBytesPerSec = downloadBytesPerSec;
        this.uploadBytesPerSec = uploadBytesPerSec;
        this.totalLoadedChunks = Math.max(0, totalLoadedChunks);
        this.totalGeneratingChunks = Math.max(0, totalGeneratingChunks);
        this.dhGeneratingChunks = Math.max(0, dhGeneratingChunks);
        this.c2meOpenclLoaded = c2meOpenclLoaded;
        this.dhLoaded = dhLoaded;
        this.seedGenLoaded = seedGenLoaded;
        this.gpuName = gpuName != null ? gpuName : "";
        this.gpuStats = gpuStats != null ? gpuStats : "";
    }

    public float getTps() {
        return tps;
    }

    public float getMspt() {
        return mspt;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public int getMaxCpuPercent() {
        return maxCpuPercent;
    }

    public long getUsedRamBytes() {
        return usedRamBytes;
    }

    public long getMaxRamBytes() {
        return maxRamBytes;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public long getDownloadBytesPerSec() {
        return downloadBytesPerSec;
    }

    public long getUploadBytesPerSec() {
        return uploadBytesPerSec;
    }

    public int getTotalLoadedChunks() {
        return totalLoadedChunks;
    }

    public int getTotalGeneratingChunks() {
        return totalGeneratingChunks;
    }

    public int getDhGeneratingChunks() {
        return dhGeneratingChunks;
    }

    public boolean isC2meOpenclLoaded() {
        return c2meOpenclLoaded;
    }

    public boolean isDhLoaded() {
        return dhLoaded;
    }

    public boolean isSeedGenLoaded() {
        return seedGenLoaded;
    }

    public String getGpuName() {
        return gpuName;
    }

    public String getGpuStats() {
        return gpuStats;
    }

    public String getFormattedCpu() {
        return String.format("%.0f%% / %d%%", cpuUsage, maxCpuPercent);
    }

    public String getFormattedChunks() {
        if (dhGeneratingChunks > 0) {
            return totalLoadedChunks + " loaded (Gen: " + totalGeneratingChunks + " + DH:" + dhGeneratingChunks + ")";
        } else if (totalGeneratingChunks > 0) {
            return totalLoadedChunks + " loaded (Gen: " + totalGeneratingChunks + ")";
        } else {
            return totalLoadedChunks + " loaded";
        }
    }

    public String getFormattedUptime() {
        long days = uptimeSeconds / 86400L;
        long rem = uptimeSeconds % 86400L;
        long hours = rem / 3600L;
        long minutes = (rem % 3600L) / 60L;

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    public static String formatSpeed(long bytesPerSec) {
        if (bytesPerSec >= 1024L * 1024L * 1024L) {
            return String.format("%.1f Go/s", bytesPerSec / (1024.0 * 1024.0 * 1024.0));
        } else if (bytesPerSec >= 1024L * 1024L) {
            return String.format("%.1f Mo/s", bytesPerSec / (1024.0 * 1024.0));
        } else if (bytesPerSec >= 1024L) {
            return String.format("%.0f Ko/s", bytesPerSec / 1024.0);
        } else {
            return bytesPerSec + " o/s";
        }
    }

    public static String formatMemory(long bytes) {
        double gib = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.1f Go", gib);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeFloat(tps);
        buf.writeFloat(mspt);
        buf.writeDouble(cpuUsage);
        buf.writeVarInt(maxCpuPercent);
        buf.writeLong(usedRamBytes);
        buf.writeLong(maxRamBytes);
        buf.writeLong(uptimeSeconds);
        buf.writeLong(downloadBytesPerSec);
        buf.writeLong(uploadBytesPerSec);
        buf.writeVarInt(totalLoadedChunks);
        buf.writeVarInt(totalGeneratingChunks);
        buf.writeVarInt(dhGeneratingChunks);
        buf.writeBoolean(c2meOpenclLoaded);
        buf.writeBoolean(dhLoaded);
        buf.writeBoolean(seedGenLoaded);
        buf.writeUtf(gpuName);
        buf.writeUtf(gpuStats);
    }

    public static ServerHardwareStats read(RegistryFriendlyByteBuf buf) {
        float tps = buf.readFloat();
        float mspt = buf.readFloat();
        double cpu = buf.readDouble();
        int maxCpu = buf.readVarInt();
        long usedRam = buf.readLong();
        long maxRam = buf.readLong();
        long uptime = buf.readLong();
        long dl = buf.readLong();
        long ul = buf.readLong();
        int loadedChunks = buf.readVarInt();
        int genChunks = buf.readVarInt();
        int dhGenChunks = buf.readVarInt();
        boolean c2me = buf.readBoolean();
        boolean dh = buf.readBoolean();
        boolean seedGen = buf.readBoolean();
        String gpu = buf.readUtf();
        String stats = buf.readUtf();
        return new ServerHardwareStats(tps, mspt, cpu, maxCpu, usedRam, maxRam, uptime, dl, ul,
                loadedChunks, genChunks, dhGenChunks, c2me, dh, seedGen, gpu, stats);
    }
}
