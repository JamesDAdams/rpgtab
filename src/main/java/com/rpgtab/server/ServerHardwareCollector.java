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
    private static Boolean cachedDhLoaded = null;
    private static Boolean cachedSeedGenLoaded = null;

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

    public static boolean isDhLoaded() {
        if (cachedDhLoaded == null) {
            try {
                cachedDhLoaded = ModList.get().isLoaded("distanthorizons");
            } catch (Throwable t) {
                cachedDhLoaded = false;
            }
        }
        return cachedDhLoaded;
    }

    public static boolean isSeedGenLoaded() {
        if (cachedSeedGenLoaded == null) {
            try {
                boolean loaded = ModList.get().isLoaded("seedrenderer") || ModList.get().isLoaded("seedgen");
                if (loaded) {
                    try {
                        Class<?> compatClass = Class.forName("mobius.seedrenderer.compat.WorldgenCompat");
                        List<?> reasons = (List<?>) compatClass.getMethod("standDownReasons").invoke(null);
                        if (reasons != null && !reasons.isEmpty()) {
                            cachedSeedGenLoaded = false;
                            return false;
                        }
                    } catch (Throwable ignored) {}
                }
                cachedSeedGenLoaded = loaded;
            } catch (Throwable t) {
                cachedSeedGenLoaded = false;
            }
        }
        return cachedSeedGenLoaded;
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

        // 1. C2ME OpenCL direct device name
        String c2meGpu = getC2MEGpuName();
        if (!c2meGpu.isEmpty()) {
            cachedGpuName = c2meGpu;
            return cachedGpuName;
        }

        // 2. Linux NVIDIA procfs
        String procGpu = getNvidiaProcfsGpuName();
        if (!procGpu.isEmpty()) {
            cachedGpuName = procGpu;
            return cachedGpuName;
        }

        // 3. OSHI hardware graphics cards
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

        cachedGpuName = "";
        return cachedGpuName;
    }

    public static String getGpuStats(MinecraftServer server) {
        // 1. Try reading C2ME OpenCL context device queue & VRAM via reflection
        if (server != null && isC2meOpenclLoaded()) {
            try {
                Class<?> extClass = Class.forName("com.ishland.c2me.opts.accel.opencl.common.ducks.MinecraftServerExtension");
                if (extClass.isInstance(server)) {
                    Object clCtx = extClass.getMethod("c2me$getCLContext").invoke(server);
                    if (clCtx != null) {
                        List<?> devices = (List<?>) clCtx.getClass().getMethod("getOpenDevices").invoke(clCtx);
                        if (devices != null && !devices.isEmpty()) {
                            Object dev = devices.get(0);
                            int permits = (int) dev.getClass().getMethod("getPermits").invoke(dev);
                            Object metadata = dev.getClass().getMethod("getMetadata").invoke(dev);
                            long devPtr = metadata.getClass().getField("devicePtr").getLong(metadata);
                            Class<?> utilClass = Class.forName("com.ishland.c2me.opts.accel.opencl.common.util.CLUtil");
                            long globalMem = (long) utilClass.getMethod("getDeviceInfoLong", long.class, int.class).invoke(null, devPtr, 0x101F);
                            if (globalMem > 0) {
                                return String.format("VRAM: %.1f Go | Queue: %d", globalMem / (1024.0 * 1024.0 * 1024.0), permits);
                            } else {
                                return "Queue: " + permits;
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        // 2. Try OSHI VRAM
        if (initOshi()) {
            try {
                Object hardware = oshiSystemInfo.getClass().getMethod("getHardware").invoke(oshiSystemInfo);
                List<?> gpus = (List<?>) hardware.getClass().getMethod("getGraphicsCards").invoke(hardware);
                if (gpus != null && !gpus.isEmpty()) {
                    for (Object gpuObj : gpus) {
                        long vram = (long) gpuObj.getClass().getMethod("getVRam").invoke(gpuObj);
                        if (vram > 0) {
                            return String.format("VRAM: %.1f Go", vram / (1024.0 * 1024.0 * 1024.0));
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        return "";
    }

    public static int getContainerAllocatedCores() {
        try {
            File v2Max = new File("/sys/fs/cgroup/cpu.max");
            if (v2Max.exists()) {
                String content = Files.readString(v2Max.toPath()).trim();
                String[] parts = content.split("\\s+");
                if (parts.length >= 2 && !parts[0].equals("max")) {
                    double quota = Double.parseDouble(parts[0]);
                    double period = Double.parseDouble(parts[1]);
                    if (period > 0) {
                        return (int) Math.max(1, Math.round(quota / period));
                    }
                }
            }
        } catch (Throwable ignored) {}

        try {
            File quotaFile = new File("/sys/fs/cgroup/cpu/cpu.cfs_quota_us");
            File periodFile = new File("/sys/fs/cgroup/cpu/cpu.cfs_period_us");
            if (quotaFile.exists() && periodFile.exists()) {
                long quota = Long.parseLong(Files.readString(quotaFile.toPath()).trim());
                long period = Long.parseLong(Files.readString(periodFile.toPath()).trim());
                if (quota > 0 && period > 0) {
                    return (int) Math.max(1, Math.round((double) quota / (double) period));
                }
            }
        } catch (Throwable ignored) {}

        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    public static double getProcessCpuUsage(int cores) {
        try {
            java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                double procLoad = sunOsBean.getProcessCpuLoad();
                if (procLoad >= 0.0) {
                    return procLoad * cores * 100.0;
                }
            }
        } catch (Throwable ignored) {}

        if (initOshi() && oshiProcessor != null && prevCpuTicks != null) {
            try {
                double load = (double) oshiProcessor.getClass().getMethod("getSystemCpuLoadBetweenTicks", long[].class)
                        .invoke(oshiProcessor, (Object) prevCpuTicks);
                prevCpuTicks = (long[]) oshiProcessor.getClass().getMethod("getSystemCpuLoadTicks").invoke(oshiProcessor);
                if (load >= 0) {
                    return load * cores * 100.0;
                }
            } catch (Throwable ignored) {}
        }

        return 0.0;
    }

    public static long[] getContainerMemory() {
        try {
            File curFile = new File("/sys/fs/cgroup/memory.current");
            File maxFile = new File("/sys/fs/cgroup/memory.max");
            if (curFile.exists()) {
                long used = Long.parseLong(Files.readString(curFile.toPath()).trim());
                long max = 0;
                if (maxFile.exists()) {
                    String maxStr = Files.readString(maxFile.toPath()).trim();
                    if (!maxStr.equals("max")) {
                        max = Long.parseLong(maxStr);
                    }
                }
                if (max <= 0) {
                    max = Runtime.getRuntime().maxMemory();
                }
                return new long[]{used, max};
            }
        } catch (Throwable ignored) {}

        try {
            File curFile = new File("/sys/fs/cgroup/memory/memory.usage_in_bytes");
            File maxFile = new File("/sys/fs/cgroup/memory/memory.limit_in_bytes");
            if (curFile.exists()) {
                long used = Long.parseLong(Files.readString(curFile.toPath()).trim());
                long max = 0;
                if (maxFile.exists()) {
                    max = Long.parseLong(Files.readString(maxFile.toPath()).trim());
                    if (max > 1L << 50) {
                        max = Runtime.getRuntime().maxMemory();
                    }
                }
                if (max <= 0) {
                    max = Runtime.getRuntime().maxMemory();
                }
                return new long[]{used, max};
            }
        } catch (Throwable ignored) {}

        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();
        return new long[]{used, max};
    }

    public static void updateNetworkSpeeds() {
        long now = System.currentTimeMillis();

        try {
            File netDev = new File("/proc/net/dev");
            if (netDev.exists()) {
                List<String> lines = Files.readAllLines(netDev.toPath());
                long rxBytes = 0;
                long txBytes = 0;
                boolean foundEth = false;

                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("eth0:") || (!foundEth && trimmed.startsWith("eth"))) {
                        String[] parts = trimmed.substring(trimmed.indexOf(':') + 1).trim().split("\\s+");
                        if (parts.length >= 9) {
                            rxBytes = Long.parseLong(parts[0]);
                            txBytes = Long.parseLong(parts[8]);
                            foundEth = true;
                            break;
                        }
                    }
                }

                if (foundEth) {
                    if (prevNetTimeMs > 0 && now > prevNetTimeMs && prevRxBytes >= 0 && prevTxBytes >= 0) {
                        long durationMs = now - prevNetTimeMs;
                        long rxDiff = Math.max(0L, rxBytes - prevRxBytes);
                        long txDiff = Math.max(0L, txBytes - prevTxBytes);
                        curDlSpeed = (rxDiff * 1000L) / durationMs;
                        curUlSpeed = (txDiff * 1000L) / durationMs;
                    }
                    prevNetTimeMs = now;
                    prevRxBytes = rxBytes;
                    prevTxBytes = txBytes;
                    return;
                }
            }
        } catch (Throwable ignored) {}

        if (initOshi()) {
            try {
                Object hardware = oshiSystemInfo.getClass().getMethod("getHardware").invoke(oshiSystemInfo);
                List<?> netIFs = (List<?>) hardware.getClass().getMethod("getNetworkIFs").invoke(hardware);
                if (netIFs != null) {
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
                }
            } catch (Throwable ignored) {}
        }
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

        int cores = getContainerAllocatedCores();
        int maxCpuPercent = cores * 100;
        double cpu = getProcessCpuUsage(cores);

        long[] mem = getContainerMemory();
        long usedRam = mem[0];
        long maxRam = mem[1];

        long uptimeSec = 0;
        try {
            uptimeSec = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        } catch (Throwable ignored) {}

        updateNetworkSpeeds();

        int totalLoadedChunks = ChunkStatsCollector.getTotalLoadedChunks(server);
        int totalGeneratingChunks = ChunkStatsCollector.getTotalGeneratingChunks(server);
        int dhGeneratingChunks = ChunkStatsCollector.getDhGeneratingChunksCount();

        boolean c2me = isC2meOpenclLoaded();
        boolean dh = isDhLoaded();
        boolean seedGen = isSeedGenLoaded();
        String gpu = getGpuName();
        String gpuStats = getGpuStats(server);

        return new ServerHardwareStats(tps, mspt, cpu, maxCpuPercent, usedRam, maxRam, uptimeSec,
                curDlSpeed, curUlSpeed, totalLoadedChunks, totalGeneratingChunks, dhGeneratingChunks,
                c2me, dh, seedGen, gpu, gpuStats);
    }
}
