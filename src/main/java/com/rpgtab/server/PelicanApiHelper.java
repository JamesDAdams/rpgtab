package com.rpgtab.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PelicanApiHelper {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(1200))
            .build();

    private static volatile PelicanStats cachedStats = null;
    private static volatile long lastFetchTime = 0;
    private static volatile boolean fetching = false;

    public record PelicanStats(double cpuAbsolute, long memoryBytes, long memoryLimitBytes,
                               long networkRxBytes, long networkTxBytes,
                               long networkRxSpeed, long networkTxSpeed, boolean isRunning) {}

    public static PelicanStats getStats(String panelUrl, String apiKey, String serverId) {
        if (panelUrl == null || panelUrl.trim().isEmpty() || apiKey == null || apiKey.trim().isEmpty() || serverId == null || serverId.trim().isEmpty()) {
            return null;
        }

        long now = System.currentTimeMillis();
        // Rafraîchir toutes les 1000 ms (1 seconde) en arrière-plan sans bloquer
        if (now - lastFetchTime > 1000 && !fetching) {
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
                            .timeout(Duration.ofMillis(1200))
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

                        long rxSpeed = 0;
                        long txSpeed = 0;
                        if (cachedStats != null && lastFetchTime > 0) {
                            long dur = Math.max(1, now - lastFetchTime);
                            rxSpeed = Math.max(0, (rx - cachedStats.networkRxBytes()) * 1000L / dur);
                            txSpeed = Math.max(0, (tx - cachedStats.networkTxBytes()) * 1000L / dur);
                        }

                        long memLimit = 0;
                        if (attrs.has("limits") && attrs.get("limits").isJsonObject()) {
                            JsonObject limits = attrs.getAsJsonObject("limits");
                            if (limits.has("memory")) {
                                memLimit = limits.get("memory").getAsLong() * 1024L * 1024L;
                            }
                        }

                        cachedStats = new PelicanStats(cpu, mem, memLimit, rx, tx, rxSpeed, txSpeed, true);
                    }
                } catch (Throwable ignored) {
                } finally {
                    lastFetchTime = System.currentTimeMillis();
                    fetching = false;
                }
            });
        }

        return cachedStats;
    }
}
