package com.rpgtab.server;

import java.lang.management.ManagementFactory;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;

public class ServerHardwareCollector {

    private static Boolean oshiAvailable = null;
    private static Object oshiSystemInfo = null;
    private static Object oshiProcessor = null;
    private static long[] prevCpuTicks = null;

    private static long prevNetTimeMs = 0;
    private static long prevRxBytes = -1;
    private static long prevTxBytes = -1;
    private static long curDlSpeed = 0;
    private static long curUlSpeed = 0;

    private static String cachedGpuName = null;
    private static Boolean cachedC2meLoaded = null;

    private static boolean initOshi() {
        if (oshiAvailable != null) {
            return oshiAvailable;
        }
        try {
            Class<?> siClass = Class.forName("oshi.SystemInfo");
            oshiSystemInfo = siClass.getDeclaredConstructor().newInstance();
            Object hardware = siClass.getMethod("getHardware").invoke(oshiSystemInfo);
            oshiProcessor = hardware.getClass().getMethod("getProcessor").invoke(hardware);
            prevCpuTicks = (long[]) oshiProcessor.getClass().getMethod("getSystemCpuLoadTicks").invoke(oshiProcessor);
            oshiAvailable = true;
        } catch (Throwable t) {
            oshiAvailable = false;
        }
        return oshiAvailable;
    }

    public static boolean isC2meOpenclLoaded() {
        if (cachedC2meLoaded == null) {
            try {
                cachedC2meLoaded = ModList.get().isLoaded("c2me_opts_accel_opencl")
                        || ModList.get().isLoaded("c2me-opts-accel-opencl")
                        || ModList.get().isLoaded("c2me");
            } catch (Throwable t) {
                cachedC2meLoaded = false;
            }
        }
        return cachedC2meLoaded;
    }

    public static String getGpuName() {
        if (cachedGpuName != null) {
            return cachedGpuName;
        }
        if (!initOshi()) {
            cachedGpuName = "";
            return cachedGpuName;
        }
        try {
            Object hardware = oshiSystemInfo.getClass().getMethod("getHardware").invoke(oshiSystemInfo);
            List<?> gpus = (List<?>) hardware.getClass().getMethod("getGraphicsCards").invoke(hardware);
            if (gpus != null && !gpus.isEmpty()) {
                Object primaryGpu = gpus.get(0);
                String name = (String) primaryGpu.getClass().getMethod("getName").invoke(primaryGpu);
                cachedGpuName = name != null ? name.trim() : "";
            } else {
                cachedGpuName = "";
            }
        } catch (Throwable t) {
            cachedGpuName = "";
        }
        return cachedGpuName;
    }

    public static double getCpuUsage() {
        if (initOshi() && oshiProcessor != null && prevCpuTicks != null) {
            try {
                double load = (double) oshiProcessor.getClass().getMethod("getSystemCpuLoadBetweenTicks", long[].class)
                        .invoke(oshiProcessor, (Object) prevCpuTicks);
                prevCpuTicks = (long[]) oshiProcessor.getClass().getMethod("getSystemCpuLoadTicks").invoke(oshiProcessor);
                if (load >= 0) {
                    return Math.min(100.0, Math.max(0.0, load * 100.0));
                }
            } catch (Throwable ignored) {}
        }

        try {
            java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                double load = sunOsBean.getCpuLoad();
                if (load >= 0.0) {
                    return load * 100.0;
                }
            }
        } catch (Throwable ignored) {}

        return 0.0;
    }

    public static void updateNetworkSpeeds() {
        if (!initOshi()) {
            return;
        }
        try {
            long now = System.currentTimeMillis();
            Object hardware = oshiSystemInfo.getClass().getMethod("getHardware").invoke(oshiSystemInfo);
            List<?> netIFs = (List<?>) hardware.getClass().getMethod("getNetworkIFs").invoke(hardware);
            if (netIFs == null) return;

            long totalRx = 0;
            long totalTx = 0;
            for (Object net : netIFs) {
                net.getClass().getMethod("updateAttributes").invoke(net);
                long rx = (long) net.getClass().getMethod("getBytesRecv").invoke(net);
                long tx = (long) net.getClass().getMethod("getBytesSent").invoke(net);
                totalRx += rx;
                totalTx += tx;
            }

            if (prevNetTimeMs > 0 && now > prevNetTimeMs && prevRxBytes >= 0 && prevTxBytes >= 0) {
                long durationMs = now - prevNetTimeMs;
                long rxDiff = Math.max(0L, totalRx - prevRxBytes);
                long txDiff = Math.max(0L, totalTx - prevTxBytes);
                curDlSpeed = (rxDiff * 1000L) / durationMs;
                curUlSpeed = (txDiff * 1000L) / durationMs;
            }

            prevNetTimeMs = now;
            prevRxBytes = totalRx;
            prevTxBytes = totalTx;
        } catch (Throwable ignored) {}
    }

    public static ServerHardwareStats collect(MinecraftServer server) {
        float mspt = 0.0f;
        float tps = 20.0f;
        try {
            long avgNanos = server.getAverageTickTimeNanos();
            mspt = avgNanos / 1_000_000.0f;
            if (mspt > 0.0f) {
                tps = Math.min(20.0f, 1000.0f / mspt);
            }
        } catch (Throwable ignored) {}

        double cpu = getCpuUsage();

        Runtime rt = Runtime.getRuntime();
        long totalRam = rt.totalMemory();
        long freeRam = rt.freeMemory();
        long usedRam = totalRam - freeRam;
        long maxRam = rt.maxMemory();

        long uptimeSec = 0;
        try {
            uptimeSec = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        } catch (Throwable ignored) {}

        updateNetworkSpeeds();

        boolean c2me = isC2meOpenclLoaded();
        String gpu = getGpuName();

        return new ServerHardwareStats(tps, mspt, cpu, usedRam, maxRam, uptimeSec, curDlSpeed, curUlSpeed, c2me, gpu);
    }
}
