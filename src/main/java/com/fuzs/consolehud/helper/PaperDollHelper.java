package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;

public class PaperDollHelper {

    private final Minecraft mc;
    private final float maxRotation = 30.0F;
    private float lastSwimAnimation = 1.0F;

    public PaperDollHelper(Minecraft mc) {
        this.mc = mc;
    }

    public boolean showDoll(int remainingRidingTicks) {

        ClientPlayerEntity player = this.mc.player;

        boolean sprinting = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.sprinting.get() && player.isSprinting() && !player.isSwimming();
        boolean swimming = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.swimming.get() && player.isSwimming();
        boolean crouching = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crouching.get() && player.isSneaking() && remainingRidingTicks == 0;
        boolean flying = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.flying.get() && player.abilities.isFlying;
        boolean elytra = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.elytraFlying.get() && player.isElytraFlying();
        boolean burning = ConfigHandler.PAPER_DOLL_CONFIG.burning.get() && player.isBurning();
        boolean mounting = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.riding.get() && player.isPassenger();
        boolean spinning = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.spinAttacking.get() && player.isSpinAttacking();
        boolean hurt = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.hurt.get() && player.hurtTime > 0;

        return crouching || sprinting || swimming || burning || elytra || flying || mounting || spinning || hurt;

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public float drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks, float prevRotationYaw) {

        // prepare
        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();

        // set position and scale
        GlStateManager.translatef((float) posX, (float) posY, 50.0F);
        GlStateManager.scalef((float) -scale, (float) scale, (float) scale);

        // set angles and lighting
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);

        // save rotation as we don't want to change the actual entity
        float f = entity.rotationPitch;
        float f1 = entity.renderYawOffset;
        float f2 = entity.rotationYawHead;
        float f3 = entity.prevRotationPitch;
        float f4 = entity.prevRenderYawOffset;
        float f5 = entity.prevRotationYawHead;

        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        float defaultRotationYaw = ConfigHandler.PAPER_DOLL_CONFIG.position.get().getRotation(this.maxRotation / 2.0F);
        entity.rotationPitch = 7.5F;
        entity.prevRotationPitch = 7.5F;
        entity.renderYawOffset = defaultRotationYaw;
        entity.prevRenderYawOffset = defaultRotationYaw;
        entity.prevRotationYawHead = defaultRotationYaw + prevRotationYaw;
        prevRotationYaw = rotateEntity(prevRotationYaw, f2 - f5, partialTicks);
        entity.rotationYawHead = defaultRotationYaw + prevRotationYaw;

        // do render
        EntityRendererManager rendermanager = this.mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, 0.0F, 0.0F, 0.0F, 0.0F, partialTicks, true); // boolean parameter forces the bounding box to always be hidden
        rendermanager.setRenderShadow(true);

        // restore entity rotation
        entity.rotationPitch = f;
        entity.renderYawOffset = f1;
        entity.rotationYawHead = f2;
        entity.prevRotationPitch = f3;
        entity.prevRenderYawOffset = f4;
        entity.prevRotationYawHead = f5;

        // finish
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.disableDepthTest();
        GlStateManager.disableColorMaterial();

        return prevRotationYaw;

    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private float rotateEntity(float rotationYaw, float renderYawOffsetDiff, float partialTicks) {

        if (this.mc.isGamePaused()) {
            return rotationYaw;
        }

        // apply rotation change from entity
        rotationYaw = MathHelper.clamp(rotationYaw + renderYawOffsetDiff * 0.5F, -this.maxRotation, this.maxRotation);

        // rotate back to origin, never overshoot 0
        partialTicks = rotationYaw - partialTicks * rotationYaw / 10.0F;
        if (rotationYaw < 0.0F) {
            rotationYaw = Math.min(0, partialTicks);
        } else if (rotationYaw > 0.0F) {
            rotationYaw = Math.max(0, partialTicks);
        }

        return rotationYaw;

    }

    public static int getPotionShift(Collection<EffectInstance> collection) {

        int shift = 0;
        boolean renderInHUD = collection.stream().anyMatch(it -> it.getPotion().shouldRenderHUD(it));
        boolean doesShowParticles = collection.stream().anyMatch(EffectInstance::doesShowParticles);

        if (!collection.isEmpty() && renderInHUD && doesShowParticles) {
            shift += collection.stream().anyMatch(it -> !it.getPotion().isBeneficial()) ? 50 : 25;
        }

        return shift;

    }

    public float updateOffset(float partialTicks) {

        ClientPlayerEntity player = this.mc.player;
        float standingHeight = player.getSize(Pose.STANDING).height;
        float relativeHeight = player.getHeight() / standingHeight;

        if (player.getPose() == Pose.SNEAKING) {

            if (player.shouldRenderSneaking()) {

                return player.getSize(Pose.SNEAKING).height / standingHeight;

            }

        } else if (player.getPose() == Pose.FALL_FLYING) {

            if (player.getTicksElytraFlying() > 0) {

                float ticksElytraFlying = (float) player.getTicksElytraFlying() + partialTicks;
                float f = 1.0F - MathHelper.clamp(ticksElytraFlying * ticksElytraFlying / 100.0F, 0.0F, 1.0F);
                float flyingHeight = player.getSize(Pose.FALL_FLYING).height / standingHeight;

                return flyingHeight + (1.0F - flyingHeight) * f;

            }

        } else if (player.getPose() == Pose.SWIMMING) {

            if (player.getSwimAnimation(partialTicks) > 0) {

                float swimmingHeight = player.getSize(Pose.SWIMMING).height / standingHeight;
                float swimAnimation = player.getSwimAnimation(partialTicks);

                if (this.lastSwimAnimation > swimAnimation) {
                    swimmingHeight += (1.0F - swimmingHeight) * (1.0F - swimAnimation);
                }

                this.lastSwimAnimation = swimAnimation;
                return swimmingHeight;

            }

        } else if (relativeHeight < 1.0F) {

            return relativeHeight <= 0 ? 1.0F : relativeHeight;

        }

        return 1.0F;

    }

}
