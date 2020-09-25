package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.mixin.ContainerScreenAccessorMixin;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class ScreenAnimationsElement extends GameplayElement {

    private final int maxAnimationTime = 10;

    private boolean menuTransition;
    private boolean containerAnimation;
    private boolean debugScreen;

    private boolean isMoved;
    private boolean isClosed;
    public int animationTime;

    private boolean showDebugInfo;
    private int maxLength;
    private float animationProgess;

    @Override
    public void setup() {

        this.addListener(this::onClientTick);
        this.addListener(this::onGuiOpen);
        this.addListener(this::onBackgroundDrawn);
        this.addListener(this::onDrawScreen, EventPriority.LOW);
        this.addListener(this::onDrawScreenPre);
        this.addListener(this::onRenderGameOverlayPre, EventPriority.LOW, true);
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

        registerClientEntry(builder.comment("Animate debug screen").define("Debug Screen", true), v -> this.debugScreen = v);
    }

    public float getAnimationProgress() {

        return this.animationTime / (float) this.maxAnimationTime;
    }

    public void isMoved() {

        this.isMoved = true;
    }

    public float getAnimationTranslation() {

        return this.isEnabled() ? this.maxLength * (1.0F - this.animationProgess) : 0.0F;
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.START) {

            return;
        }

        if (this.animationTime < this.maxAnimationTime) {

            this.animationTime++;
        }

        this.isMoved = false;
    }

    private void onGuiOpen(final GuiOpenEvent evt) {

        if (evt.getGui() instanceof ContainerScreen) {

            this.animationTime = 0;
        }
    }

    private void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (this.isMoved) {

            evt.getMatrixStack().translate(0.0F, this.mc.getMainWindow().getScaledHeight() * (1.0F - this.getAnimationProgress()), 0.0F);
        }
    }

    private void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (this.isMoved) {

            evt.getMatrixStack().translate(0.0F, -this.mc.getMainWindow().getScaledHeight() * (1.0F - this.getAnimationProgress()), 0.0F);
        }
    }

    private void onDrawScreenPre(final GuiScreenEvent.DrawScreenEvent.Pre evt) {

        if (evt.getGui() instanceof ContainerScreen) {

            ContainerScreen<?> container = (ContainerScreen<?>) evt.getGui();
            ContainerScreenAccessorMixin accessor = (ContainerScreenAccessorMixin) evt.getGui();
            int guiTop = (container.height - container.getYSize()) / 2;
            accessor.setGuiTop(guiTop + (int) (this.mc.getMainWindow().getScaledHeight() * (1.0F - this.getAnimationProgress())));
//            evt.getMatrixStack().translate(0.0F, this.mc.getMainWindow().getScaledHeight() * (1.0F - this.getAnimationProgress()), 0.0F);
        }
    }

    private void onDrawScreenPost(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (this.isMoved) {

            evt.getMatrixStack().translate(0.0F, -this.mc.getMainWindow().getScaledHeight() * (1.0F - this.getAnimationProgress()), 0.0F);
        }
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
