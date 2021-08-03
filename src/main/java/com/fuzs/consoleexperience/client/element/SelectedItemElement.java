package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.client.tooltip.TooltipBuilder;
import com.fuzs.consoleexperience.config.EntryCollectionBuilder;
import com.fuzs.consoleexperience.mixin.client.accessor.IngameGuiAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "SuspiciousNameCombination"})
public class SelectedItemElement extends GameplayElement implements IHasDisplayTime {

    private IngameGuiAccessor ingameGUI;
    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();
    private final int defaultScale = 6;
    private final int defaultXOffset = 0;
    private final int defaultYOffset = 59;
    private final int defaultDisplayTime = 40;
    private final TextFormatting defaultTextColor = TextFormatting.GRAY;

    private int scale;
    private int xOffset;
    private int yOffset;
    private int displayTime;
    private int maximumRows;
    private Set<Item> blacklist;
    private int updateInterval;
    public boolean moddedInfo;
    public boolean lastLine;
    private BackgroundMode backgroundMode;
    public TextFormatting textColor;

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

        this.ingameGUI = (IngameGuiAccessor) this.mc.ingameGUI;
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
    protected boolean isAlwaysEnabled() {

        return true;
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Scale of held item tooltips. Works together with \"GUI Scale\" option in \"Video Settings\". A smaller scale might make room for more rows.").defineInRange("Scale", this.defaultScale, 1, 24), v -> this.scale = v);
        registerClientEntry(builder.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", this.defaultXOffset, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from screen bottom.").defineInRange("Y-Offset", this.defaultYOffset, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Amount of ticks the held item tooltip will be displayed for. Set to 0 to always display the tooltip as long as an item is being held.").defineInRange("Display Time", this.defaultDisplayTime, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
        registerClientEntry(builder.comment("Maximum amount of rows to be displayed for held item tooltips.").defineInRange("Maximum Rows", 4, 1, Integer.MAX_VALUE), v -> this.maximumRows = v);
        registerClientEntry(builder.comment("Disables held item tooltips for specified items, mainly to prevent custom tooltips from overlapping.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blacklist", new ArrayList<String>()),
                v -> this.blacklist = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS, ConsoleExperience.LOGGER).buildEntrySet(v));
        registerClientEntry(builder.comment("Interval in ticks after which the tooltip will be remade. Some stats such as durability aren't affected.").defineInRange("Update Interval", 20, 1, Integer.MAX_VALUE), v -> this.updateInterval = v);
        registerClientEntry(builder.comment("Enable tooltip information added by other mods to be included on the tooltip.").define("Modded Information", false), v -> this.moddedInfo = v);
        registerClientEntry(builder.comment("Show how many more lines there are that currently don't fit the tooltip.").define("Last Line", true), v -> this.lastLine = v);
        registerClientEntry(builder.comment("Show black chat background behind tooltip lines for better visibility.").defineEnum("Background Mode", BackgroundMode.NONE), v -> this.backgroundMode = v);
        registerClientEntry(builder.comment("Default text color used in json config.", "Allowed Values: " + Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).map(Enum::name).collect(Collectors.joining(", "))).define("Default Color", this.defaultTextColor.name()), v -> {

            try {

                TextFormatting textColor = TextFormatting.valueOf(v);
                if (!textColor.isColor()) {

                    throw new IllegalArgumentException("No text color " + textColor.getClass().getName() + "." + textColor.name());
                }

                this.textColor = textColor;
            } catch (IllegalArgumentException e) {

                ConsoleExperience.LOGGER.error(e);
                this.textColor = this.defaultTextColor;
            }
        });
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

            // item instance changes when using durability, to reflect this we need to update
            if (this.highlightingItemStack != itemstack) {

                this.highlightingItemStack = itemstack;
                this.tooltipBuilder.reset();
            }

            if (this.remainingHighlightTicks > 0) {

                this.remainingHighlightTicks--;
            }
        } else {

            this.highlightingItemStack = itemstack;
            if (this.highlightingItemStack.isEmpty()) {

                this.remainingHighlightTicks = 0;
            } else {

                // get default vanilla value if not enabled
                this.remainingHighlightTicks = this.isEnabled() ? this.displayTime : this.defaultDisplayTime;
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

        if (this.overlayMessageTime > 0 && --this.overlayMessageTime == 0) {

            this.tooltipBuilder.reset();
        }
    }

    private void onRenderGameOverlayText(final RenderGameOverlayEvent.Text evt) {

        if (this.ingameGUI.getOverlayMessageTime() > 0) {

            this.overlayMessageTime = this.ingameGUI.getOverlayMessageTime();
            this.ingameGUI.setOverlayMessageTime(0);
            this.tooltipBuilder.reset();
        }

        int width = evt.getWindow().getScaledWidth();
        int height = evt.getWindow().getScaledHeight();
        this.renderRecordOverlay(evt.getMatrixStack(), width, height, evt.getPartialTicks());

        assert this.mc.playerController != null;
        if (!this.mc.playerController.isSpectatorMode() && (this.isEnabled() || this.mc.gameSettings.heldItemTooltips)) {

            this.renderSelectedItem(evt.getMatrixStack(), width, height);
        }
    }

    private void renderSelectedItem(MatrixStack matrixStack, int width, int height) {

        if (this.isVisible() && !this.highlightingItemStack.isEmpty()) {

            this.mc.getProfiler().startSection("selectedItemName");
            int alpha = this.displayTime == 0 ? 255 : (int) Math.min(255.0F, (float) this.remainingHighlightTicks * 255.0F / 10.0F);
            if (alpha <= 0) {
                
                return;
            }

            final List<ITextComponent> tooltip = this.getTooltip();
            final float scale = (this.isEnabled() ? this.scale : this.defaultScale) / 6.0F;
            final int posX = this.getPosX(scale, width);
            int posY = this.getPosY(scale, height, tooltip.size());

            FontRenderer fontRenderer = this.mc.fontRenderer;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.scalef(scale, scale, 1.0F);
            this.drawBackground(matrixStack, posX, posY, alpha, tooltip);
            for (int i = 0; i < tooltip.size(); i++) {

                ITextComponent component = tooltip.get(i);
                AbstractGui.drawCenteredString(matrixStack, fontRenderer, component, posX, posY, 16777215 + (alpha << 24));
                posY += i == 0 ? fontRenderer.FONT_HEIGHT + 3 : fontRenderer.FONT_HEIGHT + 1;
            }

            RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();

            this.mc.getProfiler().endSection();
        }
    }

    private void renderRecordOverlay(MatrixStack matrixStack, int width, int height, float partialTicks) {

        if (this.overlayMessageTime > 0) {

            this.mc.getProfiler().startSection("overlayMessage");
            float timer = (float) this.overlayMessageTime - partialTicks;
            int alpha = Math.min(255, (int) (timer * 255.0F / 20.0F));

            if (alpha > 8) {

                FontRenderer fontRenderer = this.mc.fontRenderer;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                width /= 2;
                width -= fontRenderer.getStringPropertyWidth(this.ingameGUI.getOverlayMessage()) / 2;
                height -= 72;
                width += ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getXOffset();
                height -= ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getYOffset();

                int hue = 16777215;
                if (this.ingameGUI.getAnimateOverlayMessageColor()) {

                    hue = MathHelper.hsvToRGB(timer / 50.0F, 0.7F, 0.6F) & hue;
                }

                alpha = alpha << 24 & -16777216;
                int backgroundColor = this.mc.gameSettings.getTextBackgroundColor(0.0F);
                if (backgroundColor != 0) {

                    AbstractGui.fill(matrixStack, width - 2, height - 2, width + fontRenderer.getStringPropertyWidth(this.ingameGUI.getOverlayMessage()) + 2,
                            height + fontRenderer.FONT_HEIGHT + 2, ColorHelper.PackedColor.blendColors(backgroundColor, 16777215 | alpha));
                }

                AbstractGui.drawString(matrixStack, fontRenderer, this.ingameGUI.getOverlayMessage(), width, height, hue | alpha);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }

            this.mc.getProfiler().endSection();
        }
    }

    private int getPosX(float scale, int width) {

        int posX = (int) (width / (2.0F * scale));
        if (this.isEnabled()) {

            posX += this.xOffset;
        }

        posX += ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getXOffset();
        return posX;
    }

    private int getPosY(float scale, int height, int tooltipSize) {

        int posY = (int) (height / scale);
        posY -= this.isEnabled() ? this.yOffset / scale : this.defaultYOffset / scale;
        if (this.mc.playerController != null && !this.mc.playerController.shouldDrawHUD()) {

            posY += 14;
        }

        posY -= ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getYOffset();
        posY -= tooltipSize > 1 ? (tooltipSize - 1) * 10 + 2 : (tooltipSize - 1) * 10;
        return posY;
    }

    private List<ITextComponent> getTooltip() {

        if ((this.displayTime - this.remainingHighlightTicks) % this.updateInterval == 0) {

            this.tooltipBuilder.reset();
        }

        if (!this.isEnabled() || this.blacklist.contains(this.highlightingItemStack.getItem())) {

            return this.tooltipBuilder.create(this.highlightingItemStack);
        }

        assert this.mc.playerController != null;
        int rows = this.overlayMessageTime > 0 ? (this.mc.playerController.shouldDrawHUD() ? 1 : 2) : this.maximumRows;
        return this.tooltipBuilder.create(this.highlightingItemStack, this.mc.player, rows);
    }

    private void drawBackground(MatrixStack matrixStack, int posX, int posY, int alpha, List<ITextComponent> tooltip) {

        if (this.backgroundMode != BackgroundMode.NONE || !this.isEnabled()) {

            FontRenderer fontRenderer = this.mc.fontRenderer;
            alpha = (int) (alpha * this.mc.gameSettings.accessibilityTextBackgroundOpacity);
            if (this.backgroundMode == BackgroundMode.RECTANGLE || !this.isEnabled()) {

                int maximumWidth = tooltip.stream().mapToInt(fontRenderer::getStringPropertyWidth).max().orElse(0) / 2;
                int size = tooltip.size();

                AbstractGui.fill(matrixStack, posX - maximumWidth - 2, posY - 2, posX + maximumWidth + 2,
                        posY + size * (fontRenderer.FONT_HEIGHT + 1) + (size > 1 ? 1 : -1) + 2, alpha << 24);
            } else {

                for (int i = 0; i < tooltip.size(); i++) {

                    int previousWidth = this.getTextWidth(fontRenderer, tooltip, i - 1) / 2;
                    int currentWidth = this.getTextWidth(fontRenderer, tooltip, i) / 2;
                    int nextWidth = this.getTextWidth(fontRenderer, tooltip, i + 1) / 2;
                    int top = currentWidth < previousWidth ? (i == 1 ? 1 : -1) : 2;
                    int bottom = currentWidth <= nextWidth ? (i == 0 ? 1 : -1) : 2;

                    AbstractGui.fill(matrixStack, posX - currentWidth - 2, posY - top, posX + currentWidth + 2,
                            posY + fontRenderer.FONT_HEIGHT + bottom, alpha << 24);
                    posY += i == 0 ? fontRenderer.FONT_HEIGHT + 3 : fontRenderer.FONT_HEIGHT + 1;
                }
            }
        }
    }

    private int getTextWidth(FontRenderer fontRenderer, List<ITextComponent> tooltip, int index) {

        int clampedIndex = MathHelper.clamp(index, 0, tooltip.size() - 1);
        return clampedIndex == index ? fontRenderer.getStringPropertyWidth(tooltip.get(index)) : 0;
    }

    @SuppressWarnings("unused")
    private enum BackgroundMode {

        NONE, RECTANGLE, ADAPTIVE
    }

}
