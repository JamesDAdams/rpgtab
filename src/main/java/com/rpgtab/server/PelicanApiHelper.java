package com.rpgtab.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

public class PelicanApiHelper {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(1500))
            .build();

    private static volatile PelicanStats cachedStats = null;
    private static volatile long lastPollTime = 0;
    private static volatile long prevApiRxBytes = -1;
    private static volatile long prevApiTxBytes = -1;
    private static volatile long prevApiSampleTime = 0;
    private static volatile long computedRxSpeed = 0;
    private static volatile long computedTxSpeed = 0;
    private static volatile boolean fetching = false;

    public record PelicanStats(double cpuAbsolute, long memoryBytes, long memoryLimitBytes,
                               long swapBytes, long swapLimitBytes,
                               long networkRxBytes, long networkTxBytes,
                               long networkRxSpeed, long networkTxSpeed, boolean isRunning) {}

    public static long[] getContainerSwapStats() {
        // [swapUsedBytes, swapLimitBytes]
        try {
            File curFile = new File("/sys/fs/cgroup/memory.swap.current");
            File maxFile = new File("/sys/fs/cgroup/memory.swap.max");
            if (curFile.exists()) {
                long used = Long.parseLong(Files.readString(curFile.toPath()).trim());
                long max = 0;
                if (maxFile.exists()) {
                    String s = Files.readString(maxFile.toPath()).trim();
                    if (!s.equals("max")) {
                        max = Long.parseLong(s);
                    }
                }
                return new long[]{used, max};
            }
        } catch (Throwable ignored) {}

        try {
            File memswFile = new File("/sys/fs/cgroup/memory/memory.memsw.usage_in_bytes");
            File memFile = new File("/sys/fs/cgroup/memory/memory.usage_in_bytes");
            File memswLimitFile = new File("/sys/fs/cgroup/memory/memory.memsw.limit_in_bytes");
            File memLimitFile = new File("/sys/fs/cgroup/memory/memory.limit_in_bytes");
            if (memswFile.exists() && memFile.exists()) {
                long memsw = Long.parseLong(Files.readString(memswFile.toPath()).trim());
                long mem = Long.parseLong(Files.readString(memFile.toPath()).trim());
                long usedSwap = Math.max(0L, memsw - mem);
                long maxSwap = 0;
                if (memswLimitFile.exists() && memLimitFile.exists()) {
                    long memswLim = Long.parseLong(Files.readString(memswLimitFile.toPath()).trim());
                    long memLim = Long.parseLong(Files.readString(memLimitFile.toPath()).trim());
                    maxSwap = Math.max(0L, memswLim - memLim);
                }
                return new long[]{usedSwap, maxSwap};
            }
        } catch (Throwable ignored) {}

        return new long[]{0L, 0L};
    }

    public static PelicanStats getStats(String panelUrl, String apiKey, String serverId) {
        if (panelUrl == null || panelUrl.trim().isEmpty() || apiKey == null || apiKey.trim().isEmpty() || serverId == null || serverId.trim().isEmpty()) {
            return null;
        }

        long now = System.currentTimeMillis();
        if (now - lastPollTime > 800 && !fetching) {
            fetching = true;
            Thread.ofVirtual().start(() -> {
                try {
                    String baseUrl = panelUrl.trim();
                    if (baseUrl.endsWith("/")) {
                        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                    }
                    String targetUrl = baseUrl + "/api/client/servers/" + serverId.trim() + "/resources";

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(targetUrl))
                            .header("Authorization", "Bearer " + apiKey.trim())
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .timeout(Duration.ofMillis(1500))
                            .GET()
                            .build();

                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                        JsonObject attrs = root.getAsJsonObject("attributes");
                        JsonObject resources = attrs.getAsJsonObject("resources");

                        double cpu = resources.get("cpu_absolute").getAsDouble();
                        long mem = resources.get("memory_bytes").getAsLong();
                        long rx = resources.get("network_rx_bytes").getAsLong();
                        long tx = resources.get("network_tx_bytes").getAsLong();

                        long sampleTime = System.currentTimeMillis();
                        if (prevApiSampleTime > 0 && sampleTime > prevApiSampleTime && prevApiRxBytes >= 0 && prevApiTxBytes >= 0) {
                            long durMs = sampleTime - prevApiSampleTime;
                            long rxDelta = Math.max(0L, rx - prevApiRxBytes);
                            long txDelta = Math.max(0L, tx - prevApiTxBytes);
                            computedRxSpeed = (rxDelta * 1000L) / durMs;
                            computedTxSpeed = (txDelta * 1000L) / durMs;
                        }

                        prevApiSampleTime = sampleTime;
                        prevApiRxBytes = rx;
                        prevApiTxBytes = tx;

                        long memLimit = 0;
                        long swapLimit = 0;
                        if (attrs.has("limits") && attrs.get("limits").isJsonObject()) {
                            JsonObject limits = attrs.getAsJsonObject("limits");
                            if (limits.has("memory")) {
                                memLimit = limits.get("memory").getAsLong() * 1024L * 1024L;
                            }
                            if (limits.has("swap")) {
                                swapLimit = limits.get("swap").getAsLong() * 1024L * 1024L;
                            }
                        }

                        // Also fetch server details to get swap limit if not in resources endpoint
                        if (swapLimit <= 0) {
                            try {
                                String serverDetailUrl = baseUrl + "/api/client/servers/" + serverId.trim();
                                HttpRequest detailReq = HttpRequest.newBuilder()
                                        .uri(URI.create(serverDetailUrl))
                                        .header("Authorization", "Bearer " + apiKey.trim())
                                        .header("Accept", "application/json")
                                        .timeout(Duration.ofMillis(1200))
                                        .GET()
                                        .build();
                                HttpResponse<String> detailResp = HTTP_CLIENT.send(detailReq, HttpResponse.BodyHandlers.ofString());
                                if (detailResp.statusCode() == 200) {
                                    JsonObject dRoot = JsonParser.parseString(detailResp.body()).getAsJsonObject();
                                    JsonObject dAttrs = dRoot.getAsJsonObject("attributes");
                                    JsonObject dLimits = dAttrs.getAsJsonObject("limits");
                                    if (dLimits != null && dLimits.has("swap")) {
                                        swapLimit = dLimits.get("swap").getAsLong() * 1024L * 1024L;
                                    }
                                }
                            } catch (Throwable ignored) {}
                        }

                        long[] cgroupSwap = getContainerSwapStats();
                        long usedSwap = cgroupSwap[0];
                        if (swapLimit <= 0) {
                            swapLimit = cgroupSwap[1];
                        }

                        cachedStats = new PelicanStats(cpu, mem, memLimit, usedSwap, swapLimit, rx, tx, computedRxSpeed, computedTxSpeed, true);
                    }
                } catch (Throwable ignored) {
                } finally {
                    lastPollTime = System.currentTimeMillis();
                    fetching = false;
                }
            });
        }

        return cachedStats;
    }
}
