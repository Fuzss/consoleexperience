package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;

public class PaperDollHelper {

    public static boolean showDoll(ClientPlayerEntity player, int remainingRidingTicks) {

        boolean sprinting = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.sprinting.get() && player.isSprinting() && !player.isSwimming();
        boolean swimming = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.swimming.get() && player.isSwimming();
        boolean crouching = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.crouching.get() && player.isSneaking() && remainingRidingTicks == 0;
        boolean flying = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.flying.get() && player.abilities.isFlying;
        boolean elytra = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.elytraFlying.get() && player.isElytraFlying();
        boolean burning = ConfigHandler.PAPER_DOLL_CONFIG.burning.get() && player.isBurning();
        boolean mounting = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.riding.get() && player.isPassenger();
        boolean hurt = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.hurt.get() && player.hurtTime > 0;

        return crouching || sprinting || swimming || burning || elytra || flying || mounting || hurt;

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static float drawEntityOnScreen(Minecraft mc, int posX, int posY, int scale, LivingEntity entity, float partialTicks, float prevRotationYaw) {

        GlStateManager.enableDepthTest();
        GlStateManager.enableColorMaterial();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        // set position and scale
        GlStateManager.translatef((float) posX, (float) posY, 50.0F);
        GlStateManager.scalef((float) -scale, (float) scale, (float) scale);

        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);

        // save rotation as we don't want to change the actual entity
        float f = entity.renderYawOffset;
        float f1 = entity.rotationYawHead;

        if (!ConfigHandler.PAPER_DOLL_CONFIG.blockRotation.get()) {
            // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
            prevRotationYaw = rotateEntity(mc, prevRotationYaw, entity.rotationYawHead - entity.prevRotationYawHead, partialTicks);
        } else {
            prevRotationYaw = 0;
        }

        entity.renderYawOffset = entity.rotationYawHead = ConfigHandler.PAPER_DOLL_CONFIG.position.get().getRotation(22.5F) + prevRotationYaw;

        // do render
        EntityRendererManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, true); // boolean parameter forces the bounding box to always be hidden
        rendermanager.setRenderShadow(true);

        // reset entity rotation
        entity.renderYawOffset = f;
        entity.rotationYawHead = f1;

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
    private static float rotateEntity(Minecraft mc, float rotationYaw, float renderYawOffsetDiff, float partialTicks) {

        if (mc.isGamePaused()) {
            return rotationYaw;
        }

        // apply rotation change from entity
        if (Math.abs(renderYawOffsetDiff) >= 0.05F) {
            rotationYaw += renderYawOffsetDiff * 0.5F;
        }

        rotationYaw = MathHelper.clamp(rotationYaw, -45.0F, 45.0F);

        // rotate back to origin, never overshoot 0
        if (rotationYaw < -0.05F) {
            rotationYaw = Math.min(0, rotationYaw + partialTicks * 2.0F);
        } else if (rotationYaw > 0.05F) {
            rotationYaw = Math.max(0, rotationYaw - partialTicks * 2.0F);
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

}
