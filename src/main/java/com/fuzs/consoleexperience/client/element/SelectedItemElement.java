package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.client.tooltip.TooltipBuilder;
import com.fuzs.consoleexperience.config.StringListBuilder;
import com.fuzs.consoleexperience.mixin.IngameGuiAccessorMixin;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SelectedItemElement extends GameplayElement implements IHasDisplayTime {

    private static final StringListBuilder<Item> ITEM_PARSER = new StringListBuilder<>(ForgeRegistries.ITEMS, ConsoleExperience.LOGGER);
    private IngameGuiAccessorMixin ingameGUI;
    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();
    private final int defaultScale = 6;
    private final int defaultXOffset = 0;
    private final int defaultYOffset = 59;
    private final int defaultDisplayTime = 40;

    private int scale;
    private int xOffset;
    private int yOffset;
    private int displayTime;
    private int maximumRows;
    private Set<Item> blacklist;
    private int updateInterval;

    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack = ItemStack.EMPTY;
    private int overlayMessageTime;

    @Override
    public void setup() {

        this.addListener(this::onClientTick);
        this.addListener(this::onRenderGameOverlayText);
    }

    @Override
    public void init() {

        this.ingameGUI = (IngameGuiAccessorMixin) this.mc.ingameGUI;
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Held Item Tooltips";
    }

    @Override
    public String getDescription() {

        return "Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents and much more.";
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Scale of held item tooltips. Works together with \"GUI Scale\" option in \"Video Settings\". A smaller scale might make room for setting more rows.").defineInRange("Scale", this.defaultScale, 1, 24), v -> this.scale = v);
        registerClientEntry(builder.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", this.defaultXOffset, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from screen bottom.").defineInRange("Y-Offset", this.defaultYOffset, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Amount of ticks the held item tooltip will be displayed for. Set to 0 to always display the tooltip as long as an item is being held.").defineInRange("Display Time", this.defaultDisplayTime, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
        registerClientEntry(builder.comment("Maximum amount of rows to be displayed for held item tooltips.").defineInRange("Maximum Rows", 4, 1, 9), v -> this.maximumRows = v);
        registerClientEntry(builder.comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping. Enter as either \"modid:item\" or \"modid\" respectively.").define("Blacklist", new ArrayList<String>()), v -> this.blacklist = ITEM_PARSER.buildEntrySet(v));
        registerClientEntry(builder.comment("Interval in ticks after which the tooltip will be remade. Some stats such as durability aren't affected.").defineInRange("Update Interval", 20, 1, Integer.MAX_VALUE), v -> this.updateInterval = v);

        this.tooltipBuilder.setupConfig(builder);
    }

    @Override
    public boolean isVisible() {

        return this.remainingHighlightTicks > 0 || this.displayTime == 0;
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END || this.mc.player == null || this.mc.isGamePaused()) {

            return;
        }

        ItemStack itemstack = this.mc.player.inventory.getCurrentItem();
        if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && itemstack.getDisplayName().equals(this.highlightingItemStack.getDisplayName())) {

            this.highlightingItemStack = itemstack;
            if (this.remainingHighlightTicks > 0) {

                this.remainingHighlightTicks--;
            }
        } else {

            this.highlightingItemStack = itemstack;
            if (this.highlightingItemStack.isEmpty()) {

                this.remainingHighlightTicks = 0;
            } else {

                // get default vanilla value if not enabled
                this.remainingHighlightTicks = super.isEnabled() ? this.displayTime : this.defaultDisplayTime;
                this.tooltipBuilder.reset();
                // used to disable vanilla held item tooltips completely without modifying the game option,
                // as otherwise the game option might still be deactivated after the mod is removed
                // updates highlightingItemStack in IngameGui so the vanilla gui doesn't register a change
                this.ingameGUI.setHighlightingItemStack(this.highlightingItemStack);
                // this is only here to fix a really weird bug where the vanilla tooltip wouldn't be deactivated
                // once when joining a world for the first time after the game has been started
                // so it's only required once, but now it's running every time
                this.ingameGUI.setRemainingHighlightTicks(0);
            }
        }

        if (this.overlayMessageTime > 0) {

            this.overlayMessageTime--;
        }
    }

    private void onRenderGameOverlayText(final RenderGameOverlayEvent.Text evt) {

        if (this.ingameGUI.getOverlayMessageTime() > 0) {

            this.overlayMessageTime = this.ingameGUI.getOverlayMessageTime();
            this.ingameGUI.setOverlayMessageTime(0);
            this.tooltipBuilder.reset();
        }

        if (this.mc.playerController != null && this.mc.playerController.isSpectatorMode()) {

            return;
        }

        MatrixStack matrixStack = evt.getMatrixStack();
        int width = evt.getWindow().getScaledWidth();
        int height = evt.getWindow().getScaledHeight();
        int hotbarOffsetX = 0, hotbarOffsetY = 0;
        if (GameplayElements.HOVERING_HOTBAR.isEnabled()) {

            hotbarOffsetX = ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getXOffset();
            hotbarOffsetY = ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getYOffset();
        }

        this.renderSelectedItem(matrixStack, width, height, hotbarOffsetX, hotbarOffsetY);
        this.renderRecordOverlay(matrixStack, width, height, hotbarOffsetX, hotbarOffsetY, evt.getPartialTicks());
    }

    private void renderSelectedItem(MatrixStack matrixStack, int width, int height, int hotbarOffsetX, int hotbarOffsetY) {

        this.mc.getProfiler().startSection("selectedItemName");
        if (this.isVisible() && !this.highlightingItemStack.isEmpty()) {

            int alpha = this.displayTime == 0 ? 255 : (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 255.0F / 10.0F);
            if (alpha <= 0) {
                
                return;
            }

            List<ITextComponent> tooltip = this.getTooltip();

            // get position
            final int tooltipSize = tooltip.size();
            final float scale = super.isEnabled() ? this.scale / 6.0F : this.defaultScale;
            final int posX = this.getPosX(scale, width, hotbarOffsetX);
            int posY = this.getPosY(scale, height, hotbarOffsetY, tooltipSize);

            FontRenderer fontRenderer = this.mc.fontRenderer;
            boolean cutTop = false;

            RenderSystem.pushMatrix();
            RenderSystem.defaultBlendFunc();
            RenderSystem.scalef(scale, scale, 1.0F);
            for (int i = 0; i < tooltipSize; i++) {

                ITextComponent component = tooltip.get(i);
                int textWidth = (int) (fontRenderer.func_238414_a_(component) / 2.0F);
                int cellHeight = i == 0 ? fontRenderer.FONT_HEIGHT + 3 : fontRenderer.FONT_HEIGHT + 1;
                cutTop = this.isCutTop(matrixStack, tooltip, posX, posY, alpha, tooltipSize, cutTop, i, textWidth, cellHeight);

                RenderSystem.enableBlend();
                AbstractGui.drawString(matrixStack, fontRenderer, component, posX - textWidth, posY, 16777215 + (alpha << 24));
                RenderSystem.disableBlend();
                posY += cellHeight;
            }

            RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
            RenderSystem.popMatrix();
        }

        this.mc.getProfiler().endSection();
    }

    private boolean isCutTop(MatrixStack matrixStack, List<ITextComponent> tooltip, int posX, int posY, int alpha, int tooltipSize, boolean cutTop, int i, int textWidth, int cellHeight) {

        // handle accessibility background
        if (!this.mc.gameSettings.accessibilityTextBackground) {

            FontRenderer fontRenderer = this.mc.fontRenderer;
            final int border = 2;
            int top = border;
            int bottom = border;
            int j = fontRenderer.FONT_HEIGHT + border * 2 - (i != 0 ? cellHeight : 0);
            if (cutTop) {
                top -= j;
            }
            j = fontRenderer.FONT_HEIGHT + border * 2 - cellHeight;

            // prevent adjacent lines background from overlapping
            if (i < tooltipSize - 1) {

                int nextWidth = fontRenderer.func_238414_a_(tooltip.get(i + 1)) / 2;
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
        return cutTop;
    }

    private void renderRecordOverlay(MatrixStack matrixStack, int width, int height, int hotbarOffsetX, int hotbarOffsetY, float partialTicks) {

        if (this.overlayMessageTime > 0) {

            this.mc.getProfiler().startSection("overlayMessage");
            float hue = (float) this.overlayMessageTime - partialTicks;
            int opacity = Math.min(255, (int) (hue * 255.0F / 20.0F));

            if (opacity > 8) {

                FontRenderer fontRenderer = this.mc.fontRenderer;
                RenderSystem.pushMatrix();

                width /= 2;
                width -= fontRenderer.func_238414_a_(this.ingameGUI.getOverlayMessage()) / 2;
                height -= 72;
                width += hotbarOffsetX;
                height -= hotbarOffsetY;
                int k1 = 16777215;
                if (this.ingameGUI.getAnimateOverlayMessageColor()) {
                    k1 = MathHelper.hsvToRGB(hue / 50.0F, 0.7F, 0.6F) & 16777215;
                }
                int j = opacity << 24 & -16777216;
                int background = (int) (opacity * this.mc.gameSettings.accessibilityTextBackgroundOpacity);
                if (!this.mc.gameSettings.accessibilityTextBackground) {
                    AbstractGui.fill(matrixStack, width - 2, height - 2, width + fontRenderer.func_238414_a_(this.ingameGUI.getOverlayMessage()) + 2,
                            height + fontRenderer.FONT_HEIGHT + 2, background << 24);
                }

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                AbstractGui.drawString(matrixStack, fontRenderer, this.ingameGUI.getOverlayMessage(), width, height, k1 | j);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }

            this.mc.getProfiler().endSection();
        }
    }

    private int getPosX(float scale, int width, int hotbarOffsetX) {

        int posX = (int) (width / (2.0F * scale));
        if (super.isEnabled()) {

            posX += this.xOffset;
        }

        posX += hotbarOffsetX;
        return posX;
    }

    private int getPosY(float scale, int height, int hotbarOffsetY, int tooltipSize) {

        int posY = (int) (height / scale);
        posY -= super.isEnabled() ? this.yOffset / scale : this.defaultYOffset / scale;
        if (this.mc.playerController != null && !this.mc.playerController.shouldDrawHUD()) {

            posY += 14;
        }

        posY -= hotbarOffsetY;
        posY -= tooltipSize > 1 ? (tooltipSize - 1) * 10 + 2 : (tooltipSize - 1) * 10;
        return posY;
    }

    private List<ITextComponent> getTooltip() {
        
        boolean isOriginalPosition = this.xOffset == this.defaultXOffset && this.yOffset == this.defaultYOffset;
        // clears the action bar so it won't overlap with the tooltip
        if (this.overlayMessageTime > 0 && !isOriginalPosition) {

            this.overlayMessageTime = 0;
        }

        if ((this.displayTime - this.remainingHighlightTicks) % this.updateInterval == 0) {

            this.tooltipBuilder.reset();
        }

        if (!super.isEnabled() || this.blacklist.contains(this.highlightingItemStack.getItem())) {

            return this.tooltipBuilder.create(this.highlightingItemStack);
        }

        int rows = this.overlayMessageTime > 0 && isOriginalPosition ? 1 : this.maximumRows;
        return this.tooltipBuilder.create(this.highlightingItemStack, this.mc.player, rows);
    }

}
