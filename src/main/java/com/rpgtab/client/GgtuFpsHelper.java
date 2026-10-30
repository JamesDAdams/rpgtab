package com.rpgtab.client;

import net.minecraft.client.Minecraft;

public class GgtuFpsHelper {

    private static Boolean ggtuLoaded = null;
    private static java.lang.reflect.Method getRealFpsMethod = null;
    private static java.lang.reflect.Method getGenFpsMethod = null;

    private static void init() {
        if (ggtuLoaded != null) return;
        try {
            Class<?> statsClass = Class.forName("com.maxim.resolutionscaler.render.FrameGenStats");
            getRealFpsMethod = statsClass.getMethod("getDisplayRealFps");
            getGenFpsMethod = statsClass.getMethod("getDisplayGeneratedFps");
            ggtuLoaded = true;
        } catch (Throwable t) {
            ggtuLoaded = false;
        }
    }

    public static String getFormattedFps() {
        init();
        if (Boolean.TRUE.equals(ggtuLoaded) && getRealFpsMethod != null && getGenFpsMethod != null) {
            try {
                int realFps = (int) getRealFpsMethod.invoke(null);
                int genFps = (int) getGenFpsMethod.invoke(null);
                if (genFps > 0) {
                    int totalFps = realFps + genFps;
                    return totalFps + " FPS §7(" + realFps + " + " + genFps + " gen)";
                }
            } catch (Throwable ignored) {}
        }

        Minecraft mc = Minecraft.getInstance();
        int fps = mc.getFps();
        return fps + " FPS";
    }
}
