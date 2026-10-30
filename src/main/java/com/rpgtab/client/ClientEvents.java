package com.rpgtab.client;

import com.rpgtab.RpgTabConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = RpgTabConstants.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (VanillaGuiLayers.TAB_LIST.equals(event.getName())) {
            event.setCanceled(true);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keyPlayerList.isDown() && (!mc.isLocalServer() || mc.getConnection() != null)) {
            ResourceLocation layerName = event.getName();
            if (layerName != null) {
                String path = layerName.getPath().toLowerCase();
                String ns = layerName.getNamespace().toLowerCase();
                if (ns.contains("rpghud") || ns.contains("partyhud") || path.contains("rpghud") || path.contains("hud")) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keyPlayerList.isDown() && (!mc.isLocalServer() || mc.getConnection() != null)) {
            RpgTabRenderer.render(event.getGuiGraphics());
        }
    }
}

