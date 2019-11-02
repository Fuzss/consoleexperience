package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.ReflectionHelper;
import com.fuzs.consoleexperience.helper.TooltipHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SelectedItemHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final TooltipHelper tooltipHelper = new TooltipHelper(this.mc);

    private List<ITextComponent> tooltipCache;
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

            if (this.highlightingItemStack.equals(itemstack)) {

                if (this.remainingHighlightTicks > 0) {

                    --this.remainingHighlightTicks;

                }

            } else {

                this.highlightingItemStack = itemstack;

                if (this.highlightingItemStack.isEmpty()) {

                    this.remainingHighlightTicks = 0;

                } else {

                    int j = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get();
                    this.remainingHighlightTicks = ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get() ? j : 40;
                    this.tooltipCache = null;

                    // used to disable vanilla held item tooltips completely without modifying the game option,
                    // as otherwise the game option might still be deactivated after the mod is removed
                    // updates highlightingItemStack in IngameGui so the vanilla gui doesn't register a change
                    ReflectionHelper.setHighlightingItemStack(this.mc.ingameGUI, this.highlightingItemStack);
                    // this is only here to fix a really weird bug where the vanilla tooltip wouldn't be deactivated
                    // once when joining a world for the first time after the game has been started
                    // so it's only required once, but now it's running every time
                    ReflectionHelper.setHighlightTicks(this.mc.ingameGUI, 0);

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.mc.playerController.isSpectatorMode()) {
            return;
        }

        boolean always = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get() == 0;

        if ((this.remainingHighlightTicks > 0 || always) && !this.highlightingItemStack.isEmpty()) {

            float scale = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.scale.get() / 6.0F;
            int posX = (int) (evt.getWindow().getScaledWidth() / (2.0F * scale));
            int posY = (int) (evt.getWindow().getScaledHeight() / scale);

            if (ConfigBuildHandler.GENERAL_CONFIG.heldItemTooltips.get()) {
                posX += ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.xOffset.get();
                posY -= ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.yOffset.get() / scale;
            } else {
                posY -= 59 / scale;
            }

            if (!this.mc.playerController.shouldDrawHUD()) {
                posY += 14;
            }

            if (ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get() && ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.tied.get()) {
                posX += ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get();
                posY -= ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get();
            }

            int alpha = always ? 255 : (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 256.0F / 10.0F);

            if (alpha > 0) {

                ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.highlightingItemStack.getItem());
                boolean blacklisted = resourcelocation != null && (ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.toString())
                        || ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));

                // update cache
                if (this.tooltipCache == null || !ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.cacheTooltip.get() || always) {
                    boolean flag = !ConfigBuildHandler.GENERAL_CONFIG.heldItemTooltips.get() || blacklisted || ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() == 1;
                    this.tooltipCache = this.tooltipHelper.createTooltip(this.highlightingItemStack, flag);
                }

                int size = this.tooltipCache.size();

                // clears the action bar so it won't overlap with the tooltip
                if (ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get() || size > 1) {
                    this.mc.player.sendStatusMessage(new StringTextComponent(""), true);
                }

                posY -= size > 1 ? (size - 1) * 10 + 2 : (size - 1) * 10;
                FontRenderer fontRenderer = this.mc.fontRenderer;
                boolean cutTop = false;
                int margin = 0;
                int border = 2;

                GlStateManager.pushMatrix();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.scalef(scale, scale, 1.0F);

                for (int i = 0; i < size; i++) {

                    ITextComponent component = this.tooltipCache.get(i);
                    int width = (int) (fontRenderer.getStringWidth(component.getString()) / 2.0F);
                    int cellHeight = i == 0 ? fontRenderer.FONT_HEIGHT + 3 : fontRenderer.FONT_HEIGHT + 1;

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

                            if (width < nextWidth) {
                                bottom -= j;
                                cutTop = false;
                            } else {
                                cutTop = true;
                            }

                        }

                        int k = (int) (alpha * this.mc.gameSettings.accessibilityTextBackgroundOpacity);
                        AbstractGui.fill(posX - width - border, posY - top, posX + width + border,
                                posY + fontRenderer.FONT_HEIGHT + bottom, k << 24);

                    }

                    GlStateManager.enableBlend();
                    fontRenderer.drawStringWithShadow(component.getFormattedText(), posX - width, posY, 16777215 + (alpha << 24));
                    GlStateManager.disableBlend();
                    posY += cellHeight;

                }

                GlStateManager.scalef(1.0F / scale, 1.0F / scale, 1.0F);
                GlStateManager.popMatrix();

            }

        }

    }

}
