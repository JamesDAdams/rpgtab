package com.rpgtab.server;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
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

    public static String getC2MEGpuName() {
        try {
            Class<?> locatorClass = Class.forName("com.ishland.c2me.opts.accel.opencl.common.enumeration.OpenCLDeviceLocator");
            Object devicesObj = locatorClass.getMethod("enumerateAll").invoke(null);
            if (devicesObj instanceof List<?> list && !list.isEmpty()) {
                Class<?> utilClass = Class.forName("com.ishland.c2me.opts.accel.opencl.common.util.CLUtil");
                java.lang.reflect.Method getStringMethod = utilClass.getMethod("getDeviceInfoStringUTF8", long.class, int.class);
                for (Object meta : list) {
                    long devicePtr = meta.getClass().getField("devicePtr").getLong(meta);
                    // 0x102B corresponds to CL_DEVICE_NAME in OpenCL
                    String name = (String) getStringMethod.invoke(null, devicePtr, 0x102B);
                    if (name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("unknown")) {
                        return name.trim();
                    }
                }
            }
        } catch (Throwable ignored) {}
        return "";
    }

    public static String getNvidiaProcfsGpuName() {
        try {
            File dir = new File("/proc/driver/nvidia/gpus");
            if (dir.exists() && dir.isDirectory()) {
                File[] subDirs = dir.listFiles();
                if (subDirs != null) {
                    for (File sub : subDirs) {
                        File info = new File(sub, "information");
                        if (info.exists() && info.isFile()) {
                            List<String> lines = Files.readAllLines(info.toPath());
                            for (String line : lines) {
                                if (line.toLowerCase().startsWith("model:")) {
                                    String model = line.substring(line.indexOf(':') + 1).trim();
                                    if (!model.isEmpty()) {
                                        return model;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        return "";
    }

    public static String getGpuName() {
        if (cachedGpuName != null && !cachedGpuName.isEmpty() && !cachedGpuName.equalsIgnoreCase("unknown")) {
            return cachedGpuName;
        }

        // 1. Try C2ME OpenCL enumeration (most accurate for Minecraft chunk gen)
        String c2meGpu = getC2MEGpuName();
        if (!c2meGpu.isEmpty()) {
            cachedGpuName = c2meGpu;
            return cachedGpuName;
        }

        // 2. Try Linux NVIDIA procfs (Pelican/Docker container environment)
        String procGpu = getNvidiaProcfsGpuName();
        if (!procGpu.isEmpty()) {
            cachedGpuName = procGpu;
            return cachedGpuName;
        }

        // 3. Try OSHI hardware graphics cards
        if (initOshi()) {
            try {
                Object hardware = oshiSystemInfo.getClass().getMethod("getHardware").invoke(oshiSystemInfo);
                List<?> gpus = (List<?>) hardware.getClass().getMethod("getGraphicsCards").invoke(hardware);
                if (gpus != null && !gpus.isEmpty()) {
                    for (Object gpuObj : gpus) {
                        String name = (String) gpuObj.getClass().getMethod("getName").invoke(gpuObj);
                        if (name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("unknown")) {
                            cachedGpuName = name.trim();
                            return cachedGpuName;
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        // Fallback: If C2ME OpenCL is loaded and GPU acceleration is on, indicate OpenCL Device
        if (isC2meOpenclLoaded()) {
            cachedGpuName = "OpenCL Acceleration GPU";
        } else {
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
