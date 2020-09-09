package com.fuzs.consoleexperience.client.gui;

import com.fuzs.consoleexperience.config.ConfigManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class PaperDollRenderer {
    
    private PositionPreset position;
    private HeadMovement headMovement;

    private final float maxRotation = 30.0F;

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
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternionX.conjugate();
        entityrenderermanager.setCameraOrientation(quaternionX);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() -> entityrenderermanager.renderEntityStatic(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, stack, impl, 15728880));
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
        float defaultRotationYaw = 180.0F + this.position.getRotation(this.maxRotation / 2.0F);
        if (this.headMovement == HeadMovement.YAW || entity.isElytraFlying()) {

            entity.rotationPitch = 7.5F;
            entity.prevRotationPitch = 7.5F;
        }

        entity.renderYawOffset = defaultRotationYaw;
        entity.prevRenderYawOffset = defaultRotationYaw;
        if (this.headMovement == HeadMovement.PITCH) {

            entity.prevRotationYawHead = defaultRotationYaw;
            entity.rotationYawHead = defaultRotationYaw;
        } else {

            entity.prevRotationYawHead = defaultRotationYaw + prevRotationYaw;
            prevRotationYaw = this.rotateEntity(prevRotationYaw, rotationYawHead - prevRotationYawHead, partialTicks);
            entity.rotationYawHead = defaultRotationYaw + prevRotationYaw;
        }

        return prevRotationYaw;
    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private float rotateEntity(float rotationYaw, float renderYawOffsetDiff, float partialTicks) {

        if (Minecraft.getInstance().isGamePaused()) {

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

    public void setPositionPreset(PositionPreset position) {

        this.position = position;
    }

    public void setupConfig(ForgeConfigSpec.Builder builder) {

        ConfigManager.registerEntry(ModConfig.Type.CLIENT, builder.comment("Set axis the player head can move on.").defineEnum("Head Movement", HeadMovement.YAW), v -> this.headMovement = v);
    }

    @SuppressWarnings("unused")
    private enum HeadMovement {

        YAW, PITCH, BOTH
    }

}
