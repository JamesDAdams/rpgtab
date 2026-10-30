package com.rpgtab.server;

import java.io.Serializable;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ServerHardwareStats implements Serializable {

    private final float tps;
    private final float mspt;
    private final double cpuUsage; // percentage 0 - 100
    private final long usedRamBytes;
    private final long maxRamBytes;
    private final long uptimeSeconds;
    private final long downloadBytesPerSec;
    private final long uploadBytesPerSec;
    private final boolean c2meOpenclLoaded;
    private final String gpuName;

    public ServerHardwareStats(float tps, float mspt, double cpuUsage, long usedRamBytes, long maxRamBytes,
                               long uptimeSeconds, long downloadBytesPerSec, long uploadBytesPerSec,
                               boolean c2meOpenclLoaded, String gpuName) {
        this.tps = tps;
        this.mspt = mspt;
        this.cpuUsage = cpuUsage;
        this.usedRamBytes = usedRamBytes;
        this.maxRamBytes = maxRamBytes;
        this.uptimeSeconds = uptimeSeconds;
        this.downloadBytesPerSec = downloadBytesPerSec;
        this.uploadBytesPerSec = uploadBytesPerSec;
        this.c2meOpenclLoaded = c2meOpenclLoaded;
        this.gpuName = gpuName != null ? gpuName : "";
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

    public boolean isC2meOpenclLoaded() {
        return c2meOpenclLoaded;
    }

    public String getGpuName() {
        return gpuName;
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
        buf.writeLong(usedRamBytes);
        buf.writeLong(maxRamBytes);
        buf.writeLong(uptimeSeconds);
        buf.writeLong(downloadBytesPerSec);
        buf.writeLong(uploadBytesPerSec);
        buf.writeBoolean(c2meOpenclLoaded);
        buf.writeUtf(gpuName);
    }

    public static ServerHardwareStats read(RegistryFriendlyByteBuf buf) {
        float tps = buf.readFloat();
        float mspt = buf.readFloat();
        double cpu = buf.readDouble();
        long usedRam = buf.readLong();
        long maxRam = buf.readLong();
        long uptime = buf.readLong();
        long dl = buf.readLong();
        long ul = buf.readLong();
        boolean c2me = buf.readBoolean();
        String gpu = buf.readUtf();
        return new ServerHardwareStats(tps, mspt, cpu, usedRam, maxRam, uptime, dl, ul, c2me, gpu);
    }
}
