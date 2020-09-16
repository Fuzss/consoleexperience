package com.fuzs.consoleexperience.client.element;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ScreenAnimationsElement extends GameplayElement {

    private boolean menuTransition;
    private boolean containerAnimation;
    private boolean debugScreen;

    private boolean showDebugInfo;
    private int maxLength;
    private float animationProgess;

    @Override
    public void setup() {

        this.addListener(this::onRenderGameOverlayPre, true);
        this.addListener(this::onRenderGameOverlayText, true);
        this.addListener(this::onRenderGameOverlayPost, true);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Screen Animations";
    }

    @Override
    public String getDescription() {

        return "";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("").define("", true), v -> this.debugScreen = v);
    }

    public float getAnimationTranslation() {

        return this.isEnabled() ? this.maxLength * (1.0F - this.animationProgess) : 0.0F;
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != ElementType.ALL) {

            return;
        }

        if (!this.mc.gameSettings.showDebugInfo && this.animationProgess > 0.0F) {

            this.showDebugInfo = !this.showDebugInfo;
            this.mc.gameSettings.showDebugInfo = true;
        } else if (this.showDebugInfo && this.animationProgess == 0.0F) {

            this.showDebugInfo = false;
            this.mc.gameSettings.showDebugInfo = false;
            this.maxLength = 0;
        }
    }

    private void onRenderGameOverlayText(final RenderGameOverlayEvent.Text evt) {

        FontRenderer fontRenderer = this.mc.fontRenderer;
        if (evt.isCanceled()) {

            this.maxLength = 0;
            this.animationProgess = 0.0F;
        } else if (this.showDebugInfo && this.animationProgess > 0.0F) {

            this.animationProgess = Math.max(0.0F, this.animationProgess - this.maxLength * evt.getPartialTicks() * 0.0005F);
        } else if (!evt.getRight().isEmpty() || !evt.getLeft().isEmpty()) {

            if (this.maxLength == 0) {

                this.maxLength = Math.max(evt.getRight().stream().mapToInt(fontRenderer::getStringWidth).max().orElse(0),
                        evt.getLeft().stream().mapToInt(fontRenderer::getStringWidth).max().orElse(0));
            }

            this.animationProgess = Math.min(1.0F, this.animationProgess + this.maxLength * evt.getPartialTicks() * 0.0005F);
        }

        evt.getMatrixStack().translate(-this.getAnimationTranslation(), 0.0F, 0.0F);
    }

    private void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post evt) {

        if (evt.getType() != ElementType.TEXT) {

            return;
        }

        evt.getMatrixStack().translate(-this.getAnimationTranslation(), 0.0F, 0.0F);
    }

}
