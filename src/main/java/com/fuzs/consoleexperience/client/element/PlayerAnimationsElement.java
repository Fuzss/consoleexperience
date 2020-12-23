package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.mixin.FirstPersonRendererAccessorMixin;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class PlayerAnimationsElement extends GameplayElement {

    private FirstPersonRendererAccessorMixin firstPersonRenderer;

    private boolean eatingAnimation;
    private boolean supermanGliding;
    private boolean rowingAnimation;
    private boolean handIdleAnimation;
    private boolean fallingAsleep;

    @Override
    public void setup() {

        this.addListener(this::onCameraSetup);
        this.addListener(this::onRenderHand);
    }

    @Override
    public void init() {

        this.firstPersonRenderer = (FirstPersonRendererAccessorMixin) this.mc.getFirstPersonRenderer();
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Player Animations";
    }

    @Override
    public String getDescription() {

        return "A collection of small animations for the player model.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Animate eating in third-person view.").define("Eating Animation", true), v -> this.eatingAnimation = v);
        registerClientEntry(builder.comment("Superman pose when crouching and gliding at the same time.").define("Superman Gliding", true), v -> this.supermanGliding = v);
        registerClientEntry(builder.comment("The player's arms actually move when rowing in a boat.").define("Rowing Animation", false), v -> this.rowingAnimation = v);
        registerClientEntry(builder.comment("Fall into bed slowly and smoothly.").define("Falling Asleep", true), v -> this.fallingAsleep = v);
        registerClientEntry(builder.comment("Subtle hand swing animation in first-person mode while standing still.").define("Hand Idle Animation", true), v -> this.handIdleAnimation = v);
    }

    public boolean getEatingAnimation() {

        return this.isEnabled() && this.eatingAnimation;
    }

    public boolean getSupermanGliding() {

        return this.isEnabled() && this.supermanGliding;
    }

    public boolean getRowingAnimation() {

        return this.isEnabled() && this.rowingAnimation;
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        if (!this.fallingAsleep) {

            return;
        }

        ClientPlayerEntity player = this.mc.player;
        assert player != null;
        if (player.isSleeping()) {

            evt.setPitch(Math.min(0.0F, (float) Math.pow(player.getSleepTimer() + evt.getRenderPartialTicks(), 2) * 0.008F - 45.0F));
        }
    }

    private void onRenderHand(final RenderHandEvent evt) {

        if (!this.handIdleAnimation || evt.getItemStack().isEmpty()) {

            return;
        }

        ClientPlayerEntity player = this.mc.player;
        assert player != null;
        boolean isPrimary = evt.getHand() == Hand.MAIN_HAND;
        HandSide handside = isPrimary ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float ageInTicks = this.mc.player.ticksExisted + evt.getPartialTicks();
        if (handside == HandSide.LEFT) {

            if (!isPrimary && !this.firstPersonRenderer.getItemStackMainHand().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(-this.getAngleX(ageInTicks), -this.getAngleY(ageInTicks), 0.0F);
            }

            // offset 1/3 of a period so both hand sides aren't identical
            evt.getMatrixStack().translate(this.getOppositeAngleX(ageInTicks), this.getOppositeAngleY(ageInTicks), 0.0F);
        } else {

            if (!isPrimary && !this.firstPersonRenderer.getItemStackMainHand().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(-this.getOppositeAngleX(ageInTicks), -this.getOppositeAngleY(ageInTicks), 0.0F);
            }

            evt.getMatrixStack().translate(this.getAngleX(ageInTicks), this.getAngleY(ageInTicks), 0.0F);
        }
    }

    private float getAngleX(float ageInTicks) {

        return MathHelper.sin(ageInTicks * 0.067F) * 0.005F;
    }

    private float getAngleY(float ageInTicks) {

        return MathHelper.cos(ageInTicks * 0.09F) * 0.005F;
    }

    private float getOppositeAngleX(float ageInTicks) {

        return -this.getAngleX(ageInTicks - (int) (Math.PI / 0.022));
    }

    private float getOppositeAngleY(float ageInTicks) {

        return this.getAngleY(ageInTicks + (int) (Math.PI / 0.03));
    }

}
