package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.ReflectionHelper;
import com.fuzs.consoleexperience.helper.TooltipHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
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
    private int highlightingItemSlot = 9;
    private ItemStack highlightingItemStack = ItemStack.EMPTY;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            int i = this.mc.player.inventory.currentItem;

            if (this.highlightingItemSlot == i) {

                if (this.remainingHighlightTicks > 0) {

                    --this.remainingHighlightTicks;

                }

            } else {

                this.highlightingItemStack = this.mc.player.inventory.getCurrentItem();

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

            this.highlightingItemSlot = i;

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.mc.playerController.isSpectatorMode() || this.mc.gameSettings.hideGUI) {
            return;
        }

        boolean always = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get() == 0;

        if ((this.remainingHighlightTicks > 0 || always) && !this.highlightingItemStack.isEmpty()) {

            int posX = evt.getWindow().getScaledWidth() / 2;
            int posY = evt.getWindow().getScaledHeight();

            if (ConfigBuildHandler.GENERAL_CONFIG.heldItemTooltips.get()) {
                posX += ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.xOffset.get();
                posY -= ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.yOffset.get();
            } else {
                posY -= 59;
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

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

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

                for (int i = 0; i < size; i++) {

                    ITextComponent component = this.tooltipCache.get(i);
                    int width = this.mc.fontRenderer.getStringWidth(component.getString()) / 2;
                    int accessibilityOpacity = this.mc.gameSettings.func_216839_a(0);

                    if (accessibilityOpacity != 0) {
                        AbstractGui.fill(posX - width - 2, posY - 2, posX + width + 2, posY + 7 + 2, accessibilityOpacity);
                        alpha = 255;
                    }

                    this.mc.fontRenderer.drawStringWithShadow(component.getFormattedText(), posX - width, posY, 16777215 + (alpha << 24));
                    posY += i == 0 ? 12 : 10;

                }

                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            }

        }

    }

}
