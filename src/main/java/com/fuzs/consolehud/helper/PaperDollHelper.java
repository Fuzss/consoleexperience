package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;

public class PaperDollHelper {

    public static boolean showDoll(EntityPlayerSP player, int remainingRidingTicks) {

        boolean sprinting = ConfigHandler.paperDollConfig.displayActionsConfig.sprinting && player.isSprinting();
        boolean crouching = ConfigHandler.paperDollConfig.displayActionsConfig.crouching && player.isSneaking() && remainingRidingTicks == 0;
        boolean flying = ConfigHandler.paperDollConfig.displayActionsConfig.flying && player.capabilities.isFlying;
        boolean elytra = ConfigHandler.paperDollConfig.displayActionsConfig.elytraFlying && player.isElytraFlying();
        boolean burning = ConfigHandler.paperDollConfig.burning && player.isBurning();
        boolean mounting = ConfigHandler.paperDollConfig.displayActionsConfig.riding && player.isRiding();
        boolean hurt = ConfigHandler.paperDollConfig.displayActionsConfig.hurt && player.hurtTime > 0;

        return crouching || sprinting || burning || elytra || flying || mounting || hurt;

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static float drawEntityOnScreen(Minecraft mc, int posX, int posY, int scale, EntityLivingBase entity, float partialTicks, float prevRotationYaw) {

        GlStateManager.enableDepth();
        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        // set position and scale
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) -scale, (float) scale, (float) scale);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);

        // save rotation as we don't want to change the actual entity
        float f = entity.renderYawOffset;
        float f1 = entity.rotationYawHead;
        int i = mc.gameSettings.thirdPersonView;

        if (!ConfigHandler.paperDollConfig.blockRotation) {
            // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
            prevRotationYaw = rotateEntity(mc, prevRotationYaw, entity.rotationYawHead - entity.prevRotationYawHead, partialTicks);
        } else {
            prevRotationYaw = 0;
        }

        entity.renderYawOffset = entity.rotationYawHead = ConfigHandler.paperDollConfig.position.getRotation(22.5F) + prevRotationYaw;


        // mo' bends workaround
        if (ConfigHandler.paperDollConfig.mobends) {
            mc.gameSettings.thirdPersonView = 1;
        }

        // do render
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, true); // boolean parameter forces the bounding box to always be hidden
        rendermanager.setRenderShadow(true);

        // reset entity rotation
        entity.renderYawOffset = f;
        entity.rotationYawHead = f1;
        mc.gameSettings.thirdPersonView = i;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
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

    public static int getPotionShift(Collection<PotionEffect> collection) {

        int shift = 0;
        boolean renderInHUD = collection.stream().anyMatch(it -> it.getPotion().shouldRenderHUD(it));
        boolean doesShowParticles = collection.stream().anyMatch(PotionEffect::doesShowParticles);

        if (!collection.isEmpty() && renderInHUD && doesShowParticles) {
            shift += collection.stream().anyMatch(it -> !it.getPotion().isBeneficial()) ? 50 : 25;
        }

        return shift;

    }

}
