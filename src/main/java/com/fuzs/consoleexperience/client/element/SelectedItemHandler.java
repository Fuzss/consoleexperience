package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.config.ConfigBuildHandler;
import com.fuzs.consoleexperience.client.tooltip.TooltipHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SelectedItemHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final TooltipHelper tooltipHelper = new TooltipHelper();
    private final List<ITextComponent> tooltipCache = Lists.newArrayList();

    private ITextComponent overlayMessage;
    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack = ItemStack.EMPTY;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && itemstack.getDisplayName().equals(this.highlightingItemStack.getDisplayName())) {

                if (this.remainingHighlightTicks > 0) {

                    this.remainingHighlightTicks--;

                }

            } else {

                this.highlightingItemStack = itemstack;

                if (this.highlightingItemStack.isEmpty()) {

                    this.remainingHighlightTicks = 0;

                } else {

                    int j = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get();
                    this.remainingHighlightTicks = ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get() ? j : 40;
                    this.tooltipCache.clear();

                    // used to disable vanilla held item tooltips completely without modifying the game option,
                    // as otherwise the game option might still be deactivated after the mod is removed
                    // updates highlightingItemStack in IngameGui so the vanilla gui doesn't register a change
                    this.mc.ingameGUI.highlightingItemStack = this.highlightingItemStack;
                    // this is only here to fix a really weird bug where the vanilla tooltip wouldn't be deactivated
                    // once when joining a world for the first time after the game has been started
                    // so it's only required once, but now it's running every time
                    this.mc.ingameGUI.remainingHighlightTicks = 0;

                }

            }

        }

        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        this.catchOverlayMessage();

        if (this.mc.playerController != null && this.mc.playerController.isSpectatorMode()) {
            return;
        }

        this.renderSelectedItem(evt.getMatrixStack(), evt.getWindow().getScaledWidth(), evt.getWindow().getScaledHeight());
        this.renderRecordOverlay(evt.getMatrixStack(), evt.getWindow().getScaledWidth(), evt.getWindow().getScaledHeight(), evt.getPartialTicks());

    }

    private void renderSelectedItem(MatrixStack matrixStack, int width, int height) {

        this.mc.getProfiler().startSection("selectedItemName");

        boolean always = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get() == 0;

        if ((this.remainingHighlightTicks > 0 || always) && !this.highlightingItemStack.isEmpty()) {

            // get position
            float scale = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.scale.get() / 6.0F;
            int posX = (int) (width / (2.0F * scale));
            int posY = (int) (height / scale);
            if (ConfigBuildHandler.GENERAL_CONFIG.heldItemTooltips.get()) {
                posX += ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.xOffset.get();
                posY -= ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.yOffset.get() / scale;
            } else {
                posY -= 59 / scale;
            }
            if (this.mc.playerController != null && !this.mc.playerController.shouldDrawHUD()) {
                posY += 14;
            }
            if (ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get() && ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.tied.get()) {
                posX += ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get();
                posY -= ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get();
            }

            int alpha = always ? 255 : (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 255.0F / 10.0F);

            if (alpha > 0) {

                // check blacklist
                ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.highlightingItemStack.getItem());
                boolean blacklisted = resourcelocation != null && (ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.toString())
                        || ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));
                boolean inactive = !ConfigBuildHandler.GENERAL_CONFIG.heldItemTooltips.get() || ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() == 1;
                boolean origPosition = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.xOffset.get() == 0 && ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.yOffset.get() == 59;
                boolean message = this.overlayMessageTime > 0;
                // update cache
                if (this.tooltipCache.isEmpty() || !ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.cacheTooltip.get() || always) {
                    boolean flag = blacklisted || inactive || message && origPosition;
                    this.tooltipCache.addAll(this.tooltipHelper.createTooltip(this.highlightingItemStack, flag));
                }
                // clears the action bar so it won't overlap with the tooltip
                if (message && !origPosition) {
                    this.overlayMessageTime = 0;
                }

                int size = this.tooltipCache.size();
                posY -= size > 1 ? (size - 1) * 10 + 2 : (size - 1) * 10;
                FontRenderer fontRenderer = this.mc.fontRenderer;
                boolean cutTop = false;
                int margin = 0;
                int border = 2;

                RenderSystem.pushMatrix();
                RenderSystem.defaultBlendFunc();
                RenderSystem.scalef(scale, scale, 1.0F);

                for (int i = 0; i < size; i++) {

                    ITextComponent component = this.tooltipCache.get(i);
                    int textWidth = (int) (fontRenderer.getStringWidth(component.getString()) / 2.0F);
                    int cellHeight = i == 0 ? fontRenderer.FONT_HEIGHT + 3 : fontRenderer.FONT_HEIGHT + 1;

                    // handle accessibility background
                    if (!this.mc.gameSettings.accessibilityTextBackground) {

                        int top = border;
                        int bottom = border;
                        int j = fontRenderer.FONT_HEIGHT + border * 2 - margin;
                        if (cutTop) {
                            top -= j;
                        }
                        margin = cellHeight;
                        j = fontRenderer.FONT_HEIGHT + border * 2 - margin;

                        // prevent accessibility background from overlapping from adjacent lines
                        if (i < size - 1) {

                            int nextWidth = fontRenderer.getStringWidth(this.tooltipCache.get(i + 1).getString()) / 2;
                            if (textWidth < nextWidth) {
                                bottom -= j;
                                cutTop = false;
                            } else {
                                cutTop = true;
                            }

                        }

                        int k = (int) (alpha * this.mc.gameSettings.accessibilityTextBackgroundOpacity);
                        AbstractGui.fill(matrixStack, posX - textWidth - border, posY - top, posX + textWidth + border,
                                posY + fontRenderer.FONT_HEIGHT + bottom, k << 24);

                    }

                    RenderSystem.enableBlend();
                    AbstractGui.drawString(matrixStack, fontRenderer, component, posX - textWidth, posY, 16777215 + (alpha << 24));
                    RenderSystem.disableBlend();
                    posY += cellHeight;

                }

                RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
                RenderSystem.popMatrix();

            }

        }

        this.mc.getProfiler().endSection();

    }

    private void renderRecordOverlay(MatrixStack matrixStack, int width, int height, float partialTicks) {

        if (this.overlayMessageTime > 0) {

            this.mc.getProfiler().startSection("overlayMessage");

            float hue = (float) this.overlayMessageTime - partialTicks;
            int opacity = Math.min(255, (int) (hue * 255.0F / 20.0F));

            if (opacity > 8) {

                FontRenderer fontRenderer = this.mc.fontRenderer;
                RenderSystem.pushMatrix();

                width /= 2;
                width -= fontRenderer.func_238414_a_(this.overlayMessage) / 2;
                height -= 72;
                if (ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
                    width += ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get();
                    height -= ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get();
                }
                int k1 = 16777215;
                if (this.animateOverlayMessageColor) {
                    k1 = MathHelper.hsvToRGB(hue / 50.0F, 0.7F, 0.6F) & 16777215;
                }
                int j = opacity << 24 & -16777216;
                int background = (int) (opacity * this.mc.gameSettings.accessibilityTextBackgroundOpacity);
                if (!this.mc.gameSettings.accessibilityTextBackground) {
                    AbstractGui.fill(matrixStack, width - 2, height - 2, width + fontRenderer.func_238414_a_(this.overlayMessage) + 2,
                            height + fontRenderer.FONT_HEIGHT + 2, background << 24);
                }

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                AbstractGui.drawString(matrixStack, fontRenderer, this.overlayMessage, width, height, k1 | j);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();

            }

            this.mc.getProfiler().endSection();

        }

    }

    private void catchOverlayMessage() {

        if (this.mc.ingameGUI.overlayMessageTime > 0) {
            this.overlayMessage = this.mc.ingameGUI.overlayMessage;
            this.overlayMessageTime = this.mc.ingameGUI.overlayMessageTime;
            this.animateOverlayMessageColor = this.mc.ingameGUI.animateOverlayMessageColor;
            this.mc.ingameGUI.overlayMessageTime = 0;
            this.tooltipCache.clear();
        }

    }

}
