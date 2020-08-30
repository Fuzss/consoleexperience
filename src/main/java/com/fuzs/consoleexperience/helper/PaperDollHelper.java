package com.fuzs.consoleexperience.helper;

import com.fuzs.consoleexperience.client.config.ConfigBuildHandler;
import com.fuzs.consoleexperience.client.config.HeadMovement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Collection;

public class PaperDollHelper {

    private final Minecraft mc = Minecraft.getInstance();
    private final float maxRotation = 30.0F;
    private float lastSwimAnimation = 1.0F;

    public boolean checkConditions(int remainingRidingTicks) {

        ClientPlayerEntity player = this.mc.player;
        assert player != null;

        boolean sprinting = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.sprinting.get() && player.isSprinting() && !player.isSwimming();
        boolean swimming = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.swimming.get() && player.isSwimming();
        boolean crawling = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crawling.get() && player.getPose() == Pose.SWIMMING && !player.isSwimming();
        boolean crouching = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crouching.get() && remainingRidingTicks == 0 && player.movementInput.sneaking;
        boolean flying = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.flying.get() && player.abilities.isFlying;
        boolean elytra = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.gliding.get() && player.isElytraFlying();
        boolean mounting = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.riding.get() && player.isPassenger();
        boolean spinning = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.spinAttacking.get() && player.isSpinAttacking();
        boolean moving = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.moving.get() && !player.movementInput.getMoveVector().equals(Vector2f.ZERO);
        boolean jumping = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.jumping.get() && player.movementInput.jump;
        boolean attacking = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.attacking.get() && player.isSwingInProgress;
        boolean using = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.using.get() && player.isHandActive();
        boolean hurt = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayActionsConfig.hurt.get() && player.hurtTime > 0;
        boolean burning = ConfigBuildHandler.PAPER_DOLL_CONFIG.burning.get() && player.isBurning();

        return sprinting || swimming || crawling || crouching || flying || elytra || mounting || spinning || moving || jumping || attacking || using || hurt || burning;
    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    @SuppressWarnings("deprecation")
    public float drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks, float prevRotationYaw) {

        // prepare
        RenderSystem.pushMatrix();
        RenderSystem.disableCull();
        RenderSystem.translatef((float) posX, (float) posY, 950.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);

        // set angles and lighting
        MatrixStack stack = new MatrixStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternionZ = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternionX = Vector3f.XP.rotationDegrees(15.0F);
        quaternionZ.multiply(quaternionX);
        stack.rotate(quaternionZ);

        // save rotation as we don't want to change the actual entity
        float rotationPitch = entity.rotationPitch;
        float renderYawOffset = entity.renderYawOffset;
        float rotationYawHead = entity.rotationYawHead;
        float prevRotationPitch = entity.prevRotationPitch;
        float prevRenderYawOffset = entity.prevRenderYawOffset;
        float prevRotationYawHead = entity.prevRotationYawHead;

        prevRotationYaw = this.updateRotation(entity, partialTicks, prevRotationYaw, rotationYawHead, prevRotationYawHead);

        // do render
        EntityRendererManager entityrenderermanager = this.mc.getRenderManager();
        quaternionX.conjugate();
        entityrenderermanager.setCameraOrientation(quaternionX);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, stack, impl, 15728880);
        impl.finish();
        entityrenderermanager.setRenderShadow(true);

        // restore entity rotation
        entity.rotationPitch = rotationPitch;
        entity.renderYawOffset = renderYawOffset;
        entity.rotationYawHead = rotationYawHead;
        entity.prevRotationPitch = prevRotationPitch;
        entity.prevRenderYawOffset = prevRenderYawOffset;
        entity.prevRotationYawHead = prevRotationYawHead;

        // finish
        RenderSystem.enableCull();
        RenderSystem.popMatrix();

        return prevRotationYaw;

    }

    private float updateRotation(LivingEntity entity, float partialTicks, float prevRotationYaw, float rotationYawHead, float prevRotationYawHead) {

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

            entity.prevRotationYawHead = defaultRotationYaw + prevRotationYaw;
            prevRotationYaw = rotateEntity(prevRotationYaw, rotationYawHead - prevRotationYawHead, partialTicks);
            entity.rotationYawHead = defaultRotationYaw + prevRotationYaw;
        }

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
        if (player.isCrouching()) {

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
        } else if (player.isActualySwimming()) {

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
