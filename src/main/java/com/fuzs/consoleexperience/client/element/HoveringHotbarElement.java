package com.fuzs.consoleexperience.client.element;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;

@SuppressWarnings("deprecation")
public class HoveringHotbarElement extends GameplayElement {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    // list of gui elements to be moved, idea is to basically wrap around them and whatever other mods would be doing
    private static final List<RenderGameOverlayEvent.ElementType> SHIFTED_ELEMENTS = Lists.newArrayList(
            ElementType.ARMOR, ElementType.HEALTH, ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR,
            ElementType.EXPERIENCE, ElementType.HEALTHMOUNT, ElementType.JUMPBAR, ElementType.CHAT
    );
    
    private int xOffset;
    private int yOffset;

    @Override
    public void setup() {

        this.addListener(EventPriority.HIGHEST, true, this::onRenderGameOverlayPre1);
        this.addListener(EventPriority.LOWEST, true, this::onRenderGameOverlayPre2);
        this.addListener(EventPriority.LOWEST, this::onRenderGameOverlayPost);
        this.addListener(this::onRenderGameOverlayPostHotbar);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Hovering Hotbar";
    }

    @Override
    public String getDescription() {

        return "Enable the hotbar to hover anywhere on the screen. By default just moves it up a little from the screen bottom.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from screen bottom.").defineInRange("Y-Offset", 18, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
    }

    private void onRenderGameOverlayPre1(final RenderGameOverlayEvent.Pre evt) {

        if (SHIFTED_ELEMENTS.contains(evt.getType())) {

            RenderSystem.translatef(this.xOffset, -this.yOffset, 0.0F);
        }
    }

    private void onRenderGameOverlayPre2(final RenderGameOverlayEvent.Pre evt) {

        if (evt.isCanceled() && SHIFTED_ELEMENTS.contains(evt.getType())) {

            RenderSystem.translatef(-this.xOffset, this.yOffset, 0.0F);
        }
    }

    private void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post evt) {

        if (SHIFTED_ELEMENTS.contains(evt.getType())) {
            
            RenderSystem.translatef(-this.xOffset, this.yOffset, 0.0F);
        }
    }

    private void onRenderGameOverlayPostHotbar(final RenderGameOverlayEvent.Post evt) {

        if (evt.getType() == ElementType.HOTBAR) {

            this.redrawSelectedSlot(evt.getMatrixStack(), evt.getWindow().getScaledWidth(), evt.getWindow().getScaledHeight());
        }
    }

    public int getXOffset() {

        return this.xOffset;
    }

    public int getYOffset() {

        return this.yOffset;
    }

    /**
     * draw current item highlight again as it's missing two rows of pixels normally
     */
    private void redrawSelectedSlot(MatrixStack matrixStack, int width, int height) {
        
        assert this.mc.player != null;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(WIDGETS);
        RenderSystem.enableBlend();
        AbstractGui.blit(matrixStack, width / 2 - 91 - 1 + this.mc.player.inventory.currentItem * 20, height - 1, 0, 44, 24, 2, 256, 256);
        RenderSystem.disableBlend();
    }

}
