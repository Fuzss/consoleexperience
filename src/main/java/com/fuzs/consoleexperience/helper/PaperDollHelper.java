package com.fuzs.consoleexperience.helper;

import com.fuzs.consoleexperience.handler.ConfigBuildHandler;
import com.fuzs.consoleexperience.util.HeadMovement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.util.Collection;

public class PaperDollHelper {

    private final Minecraft mc;
    private final float maxRotation = 30.0F;
    private float lastSwimAnimation = 1.0F;

    public PaperDollHelper(Minecraft mc) {
        this.mc = mc;
    }

    public boolean checkConditions(int remainingRidingTicks) {

        ClientPlayerEntity player = this.mc.player;

        if (player != null) {

            boolean sprinting = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.sprinting.get() && player.isSprinting() && !player.isSwimming();
            boolean swimming = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.swimming.get() && player.isSwimming();
            boolean crawling = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crawling.get() && player.getPose() == Pose.SWIMMING && !player.isSwimming();
            boolean crouching = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crouching.get() && remainingRidingTicks == 0 && player.movementInput.field_228350_h_;
            boolean flying = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.flying.get() && player.abilities.isFlying;
            boolean elytra = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.elytraFlying.get() && player.isElytraFlying();
            boolean mounting = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.riding.get() && player.isPassenger();
            boolean spinning = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.spinAttacking.get() && player.isSpinAttacking();
            boolean moving = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.moving.get() && !player.movementInput.getMoveVector().equals(Vec2f.ZERO);
            boolean jumping = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.jumping.get() && player.movementInput.jump;
            boolean attacking = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.attacking.get() && player.isSwingInProgress;
            boolean using = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.using.get() && player.isHandActive();
            boolean hurt = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.hurt.get() && player.hurtTime > 0;
            boolean burning = ConfigBuildHandler.PAPER_DOLL_CONFIG.burning.get() && player.isBurning();

            return sprinting || swimming || crawling || crouching || flying || elytra || mounting || spinning || moving || jumping || attacking || using || hurt || burning;

        }

        return false;

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public float drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks, float prev) {

        // prepare
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) posX, (float) posY, 950.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);

        // set angles and lighting
        MatrixStack stack = new MatrixStack();
        stack.func_227861_a_(0.0D, 0.0D, 1000.0D);
        stack.func_227862_a_((float) scale, (float) scale, (float) scale);
        Quaternion quat1 = Vector3f.field_229183_f_.func_229187_a_(180.0F);
        Quaternion quat2 = Vector3f.field_229179_b_.func_229187_a_(15.0F);
        quat1.multiply(quat2);
        stack.func_227863_a_(quat1);

        // save rotation as we don't want to change the actual entity
        float f = entity.rotationPitch;
        float f1 = entity.renderYawOffset;
        float f2 = entity.rotationYawHead;
        float f3 = entity.prevRotationPitch;
        float f4 = entity.prevRenderYawOffset;
        float f5 = entity.prevRotationYawHead;

        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        float defaultRotationYaw = 180.0F + ConfigBuildHandler.PAPER_DOLL_CONFIG.position.get().getRotation(this.maxRotation / 2.0F);
        if (ConfigBuildHandler.PAPER_DOLL_CONFIG.headMovement.get() == HeadMovement.YAW || entity.isElytraFlying()) {
            entity.rotationPitch = 7.5F;
            entity.prevRotationPitch = 7.5F;
        }
        entity.renderYawOffset = defaultRotationYaw;
        entity.prevRenderYawOffset = defaultRotationYaw;
        if (ConfigBuildHandler.PAPER_DOLL_CONFIG.headMovement.get() == HeadMovement.PITCH) {
            entity.prevRotationYawHead = defaultRotationYaw;
            entity.rotationYawHead = defaultRotationYaw;
        } else {
            entity.prevRotationYawHead = defaultRotationYaw + prev;
            prev = rotateEntity(prev, f2 - f5, partialTicks);
            entity.rotationYawHead = defaultRotationYaw + prev;
        }

        // do render
        EntityRendererManager manager = this.mc.getRenderManager();
        quat2.conjugate();
        manager.func_229089_a_(quat2);
        manager.setRenderShadow(false);
        IRenderTypeBuffer.Impl impl = Minecraft.getInstance().func_228019_au_().func_228487_b_();
        manager.func_229084_a_(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, stack, impl, 15728880);
        impl.func_228461_a_();
        manager.setRenderShadow(true);

        // restore entity rotation
        entity.rotationPitch = f;
        entity.renderYawOffset = f1;
        entity.rotationYawHead = f2;
        entity.prevRotationPitch = f3;
        entity.prevRenderYawOffset = f4;
        entity.prevRotationYawHead = f5;

        // finish
        RenderSystem.popMatrix();

        return prev;

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
        partialTicks = rotationYaw - partialTicks * rotationYaw / 15.0F;
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

        if (player == null) {
            return 0.0F;
        }

        float standingHeight = player.getSize(Pose.STANDING).height;
        float relativeHeight = player.getHeight() / standingHeight;

        if (player.getPose() == Pose.CROUCHING) {

            if (player.isCrouching()) {
                return player.getSize(Pose.CROUCHING).height / standingHeight;
            }

        } else if (player.getPose() == Pose.FALL_FLYING) {

            if (player.getTicksElytraFlying() > 0) {

                float ticksElytraFlying = (float) player.getTicksElytraFlying() + partialTicks;
                float f = 1.0F - MathHelper.clamp(ticksElytraFlying * ticksElytraFlying / 100.0F, 0.0F, 1.0F);
                float flyingHeight = player.getSize(Pose.FALL_FLYING).height / standingHeight;
                return flyingHeight + (1.0F - flyingHeight) * f;

            }

        } else if (player.func_213314_bj()) {

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
            return relativeHeight <= 0.0F ? 1.0F : relativeHeight;
        }

        return 1.0F;

    }

}
