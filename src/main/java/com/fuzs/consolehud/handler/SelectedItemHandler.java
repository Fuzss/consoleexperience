package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.helper.TooltipHelper;
import com.fuzs.consolehud.helper.ReflectionHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SelectedItemHandler extends IngameGui {

    private final TooltipHelper tooltipHelper;
    private List<ITextComponent> tooltipCache = Lists.newArrayList();

    public SelectedItemHandler() {
        super(Minecraft.getInstance());
        this.tooltipHelper = new TooltipHelper(this.mc);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (itemstack.isEmpty()) {

                this.remainingHighlightTicks = 0;

            } else if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && itemstack.getDisplayName().equals(this.highlightingItemStack.getDisplayName())) {

                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }

            } else {

                this.remainingHighlightTicks = ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get();

            }

            // used to disable vanilla held item tooltips completely without modifying the game option,
            // as otherwise the game option might still be deactivated after the mod is removed
            // using -2 instead of -1 in case some lag interferes, will run twice most of the time then
            if (this.remainingHighlightTicks > ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get() - 2) {
                ReflectionHelper.setHighlightTicks(this.mc.ingameGUI, 0);
            }

            this.highlightingItemStack = itemstack;

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.mc.playerController.isSpectatorMode() || (ConfigHandler.GENERAL_CONFIG.heldItemTooltips.get() && ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() < 1)) {
            return;
        }

        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty()) {

            int posX = evt.getWindow().getScaledWidth() / 2;
            int posY = evt.getWindow().getScaledHeight();

            if (ConfigHandler.GENERAL_CONFIG.heldItemTooltips.get()) {
                posX += ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.xOffset.get();
                posY -= ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.yOffset.get();
            } else {
                posY -= 59;
            }

            if (!this.mc.playerController.shouldDrawHUD()) {
                posY += 14;
            }

            if (ConfigHandler.GENERAL_CONFIG.hoveringHotbar.get() && ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.tied.get()) {
                posX += ConfigHandler.HOVERING_HOTBAR_CONFIG.xOffset.get();
                posY -= ConfigHandler.HOVERING_HOTBAR_CONFIG.yOffset.get();
            }

            int alpha = (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 256.0F / 10.0F);

            if (alpha > 0) {

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.highlightingItemStack.getItem());
                boolean blacklisted = resourcelocation != null && (ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.toString()) || ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));

                // using -2 instead of -1 in case some lag interferes, will run twice most of the time then, but still better than 40 times
                if (!ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.cacheTooltip.get() || this.remainingHighlightTicks > ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.displayTime.get() - 2) {
                    this.tooltipCache = this.tooltipHelper.createTooltip(this.highlightingItemStack, !ConfigHandler.GENERAL_CONFIG.heldItemTooltips.get() || blacklisted || ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() == 1);
                }

                int size = this.tooltipCache.size();

                // clears the action bar so it won't overlap with the tooltip
                if (size > (ConfigHandler.GENERAL_CONFIG.hoveringHotbar.get() ? 0 : 1)) {
                    this.mc.player.sendStatusMessage(new StringTextComponent(""), true);
                }

                posY -= size > 1 ? (size - 1) * 10 + 2 : (size - 1) * 10;

                for (int i = 0; i < size; i++) {

                    String text = this.tooltipCache.get(i).getFormattedText();
                    this.getFontRenderer().drawStringWithShadow(text, (float) (posX - this.getFontRenderer().getStringWidth(text) / 2), (float) posY, 16777215 + (alpha << 24));
                    posY += i == 0 ? 12 : 10;

                }

                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            }

        }

    }

}
