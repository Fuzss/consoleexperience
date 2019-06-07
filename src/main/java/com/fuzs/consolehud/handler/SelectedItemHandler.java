package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.helper.TooltipHelper;
import com.fuzs.consolehud.helper.TooltipShulkerBoxHelper;
import com.fuzs.consolehud.util.IPrivateAccessor;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

@SuppressWarnings("unused")
public class SelectedItemHandler extends GuiIngame implements IPrivateAccessor {

    private final TooltipHelper tooltipHelper = new TooltipHelper();
    private List<String> tooltipCache = Lists.newArrayList();

    public SelectedItemHandler() {
        super(Minecraft.getMinecraft());
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (itemstack.isEmpty()) {

                this.remainingHighlightTicks = 0;

            } else if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {

                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }

            } else {

                this.remainingHighlightTicks = ConfigHandler.heldItemTooltipsConfig.displayTime;

            }

            // used to disable vanilla held item tooltips completely without modifying the game option,
            // as otherwise the game option might still be deactivated after the mod is removed
            // using -2 instead of -1 in case some lag interferes, will run twice most of the time then
            if (this.remainingHighlightTicks > ConfigHandler.heldItemTooltipsConfig.displayTime - 2) {
                this.setHighlightTicks(this.mc.ingameGUI, 0);
            }

            this.highlightingItemStack = itemstack;

        }

    }

    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.mc.playerController.isSpectator() || (ConfigHandler.heldItemTooltips && ConfigHandler.heldItemTooltipsConfig.rows < 1)) {
            return;
        }

        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty()) {

            int posX = evt.getResolution().getScaledWidth() / 2;
            int posY = evt.getResolution().getScaledHeight();

            if (ConfigHandler.heldItemTooltips) {
                posX += ConfigHandler.heldItemTooltipsConfig.xOffset;
                posY -= ConfigHandler.heldItemTooltipsConfig.yOffset;
            } else {
                posY -= 59;
            }

            if (!this.mc.playerController.shouldDrawHUD()) {
                posY += 14;
            }

            if (ConfigHandler.hoveringHotbar) {
                posX += ConfigHandler.hoveringHotbarConfig.xOffset;
                posY -= ConfigHandler.hoveringHotbarConfig.yOffset;
            }

            int alpha = (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 256.0F / 10.0F);

            if (alpha > 0) {

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                ResourceLocation resource = Item.REGISTRY.getNameForObject(this.highlightingItemStack.getItem());
                List<String> blacklist = Lists.newArrayList(ConfigHandler.heldItemTooltipsConfig.blacklist);
                boolean blacklisted = resource != null && (blacklist.contains(resource.toString()) || blacklist.contains(resource.getResourceDomain()));

                // using -2 instead of -1 in case some lag interferes, will run twice most of the time then, but still better than 40 times
                if (!ConfigHandler.heldItemTooltipsConfig.cacheTooltip || this.remainingHighlightTicks > ConfigHandler.heldItemTooltipsConfig.displayTime - 2) {
                    this.tooltipCache = this.tooltipHelper.createTooltip(this.highlightingItemStack, !ConfigHandler.heldItemTooltips || blacklisted || ConfigHandler.heldItemTooltipsConfig.rows == 1);
                }

                int size = this.tooltipCache.size();

                // clears the action bar so it won't overlap with the tooltip
                if (size > (ConfigHandler.hoveringHotbar ? 0 : 1)) {
                    this.mc.player.sendStatusMessage(new TextComponentString(""), true);
                }

                posY -= size > 1 ? (size - 1) * 10 + 2 : (size - 1) * 10;

                for (int i = 0; i < size; i++) {

                    this.drawCenteredString(this.tooltipCache.get(i), (float) posX, (float) posY, alpha << 24);
                    posY += i == 0 ? 12 : 10;

                }

                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            }

        }

    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    private void drawCenteredString(String text, float x, float y, int color)
    {
        this.getFontRenderer().drawStringWithShadow(text, x - this.getFontRenderer().getStringWidth(text) / 2, y, color);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void makeTooltip(ItemTooltipEvent evt) {

        if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.sumShulkerBox && evt.getItemStack().getItem() instanceof ItemShulkerBox) {

            List<String> tooltip = evt.getToolTip();
            List<String> contents = Lists.newArrayList();

            evt.getItemStack().getItem().addInformation(evt.getItemStack(), evt.getEntityPlayer() == null ? null : evt.getEntityPlayer().world, contents, evt.getFlags());

            if (!tooltip.isEmpty() && !contents.isEmpty()) {

                int i = tooltip.indexOf(contents.get(0));

                if (i != -1 && tooltip.removeAll(contents)) {

                    List<String> list = Lists.newArrayList();
                    TooltipShulkerBoxHelper.getLootTableTooltip(list, evt.getItemStack());
                    TooltipShulkerBoxHelper.getContentsTooltip(list, evt.getItemStack(), new Style().setColor(TextFormatting.GRAY), 6);
                    tooltip.addAll(i, list);

                }

            }

        }

    }
}
