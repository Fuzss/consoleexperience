package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.util.ItemPlace;
import com.fuzs.consoleexperience.util.PositionPreset;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateDisplayHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private boolean enabled = true;
    private int updateTicks;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.requiresItem.get() == ItemPlace.NONE || this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            this.updateTicks--;

            if (this.updateTicks < 0) {

                this.updateTicks = 40;

                List<ResourceLocation> allowedKeys = ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.items.get().stream()
                        .map(it -> it.split(":")).filter(it -> it.length > 1).map(it -> new ResourceLocation(it[0], it[1])).collect(Collectors.toList());

                if (allowedKeys.isEmpty()) {
                    return;
                }

                List<ItemStack> inventoryStacks = Lists.newArrayList(this.mc.player.getHeldItemOffhand());
                ItemPlace place = ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.requiresItem.get();

                if (place == ItemPlace.HELD) {

                    inventoryStacks.add(this.mc.player.getHeldItemMainhand());

                } else if (place == ItemPlace.HOTBAR) {

                    inventoryStacks.addAll(this.mc.player.inventory.mainInventory.subList(0, PlayerInventory.getHotbarSize()));

                } else {

                    inventoryStacks.addAll(this.mc.player.inventory.mainInventory);

                }

                if (inventoryStacks.isEmpty()) {
                    return;
                }

                List<ResourceLocation> inventoryKeys = inventoryStacks.stream().map(it -> ForgeRegistries.ITEMS.getKey(it.getItem())).collect(Collectors.toList());
                this.enabled = !Collections.disjoint(allowedKeys, inventoryKeys);

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Text evt) {

        boolean flag = ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.requiresItem.get() != ItemPlace.NONE && !this.enabled;
        if (!ConfigBuildHandler.GENERAL_CONFIG.coordinateDisplay.get() || this.mc.gameSettings.showDebugInfo || flag) {
            return;
        }

        ITextComponent component;
        double d = Math.pow(10, ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get());
        double posX = Math.round(this.mc.player.posX * d) / d;
        double posY = Math.round(this.mc.player.getBoundingBox().minY * d) / d;
        double posZ = Math.round(this.mc.player.posZ * d) / d;

        if (ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get() == 0) {
            // no empty decimal place added like this
            component = new TranslationTextComponent("screen.coordinates", (int) posX, (int) posY, (int) posZ);
        } else {
            component = new TranslationTextComponent("screen.coordinates", posX, posY, posZ);
        }

        MainWindow window = evt.getWindow();
        int f = (int) ((this.mc.gameSettings.chatOpacity * 0.9f + 0.1f) * 255.0f);
        int k = this.mc.fontRenderer.getStringWidth(component.getString()) + 3;
        int l = 7 + 4;

        PositionPreset position = ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.position.get();
        float scale = ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.scale.get() / 6.0F;
        int x = (int) (position.getX(k, window.getScaledWidth(), ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.xOffset.get()) / scale);
        int y = (int) (position.getY(l, window.getScaledHeight(), ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.yOffset.get()) / scale);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.scalef(scale, scale, 1.0F);

        if (ConfigBuildHandler.COORDINATE_DISPLAY_CONFIG.background.get()) {
            AbstractGui.fill(x, y, x + k, y + l, f / 2 << 24);
        }

        this.mc.fontRenderer.drawStringWithShadow(component.getFormattedText(), x + 2, y + 2, 16777215 + (f << 24));

        GlStateManager.scalef(1.0F / scale, 1.0F / scale, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

    }

}
