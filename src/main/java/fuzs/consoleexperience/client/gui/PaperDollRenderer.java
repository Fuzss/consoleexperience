package fuzs.consoleexperience.client.gui;

import fuzs.consoleexperience.client.element.GameplayElements;
import fuzs.consoleexperience.client.element.HoveringHotbarElement;
import fuzs.consoleexperience.client.element.PaperDollElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class PaperDollRenderer {

    private final PaperDollElement parent;
    private final float maxRotation = 30.0F;

    private float prevRotationYaw;

    public PaperDollRenderer(PaperDollElement parent) {

        this.parent = parent;
    }

    @SuppressWarnings("deprecation")
    public void drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks) {

        ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).run(() -> {

            // prepare
            RenderSystem.pushMatrix();
            RenderSystem.disableCull();
            RenderSystem.translatef((float) posX, (float) posY, 950.0F);
            RenderSystem.scalef(1.0F, 1.0F, -1.0F);

            // set angles and lighting
            MatrixStack stack = new MatrixStack();
            stack.translate(0.0, 0.0, 1000.0);
            stack.scale((float) scale, (float) scale, (float) scale);
            Quaternion quaternionZ = Vector3f.ZP.rotationDegrees(180.0F);
            Quaternion quaternionX = Vector3f.XP.rotationDegrees(15.0F);
            quaternionZ.mul(quaternionX);
            stack.mulPose(quaternionZ);

            // save rotation as we don't want to change the actual entity
            float xRot = entity.xRot;
            float yBodyRot = entity.yBodyRot;
            float yHeadRot = entity.yHeadRot;
            float xRotO = entity.xRotO;
            float yBodyRotO = entity.yBodyRotO;
            float yHeadRotO = entity.yHeadRotO;
            this.prevRotationYaw = this.updateRotation(entity, partialTicks, this.prevRotationYaw, yHeadRot, yHeadRotO);

            // do render
            EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
            quaternionX.conj();
            entityrenderermanager.overrideCameraOrientation(quaternionX);
            entityrenderermanager.setRenderShadow(false);
            IRenderTypeBuffer.Impl impl = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderSystem.runAsFancy(() -> entityrenderermanager.render(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, stack, impl, 15728880));
            impl.endBatch();
            entityrenderermanager.setRenderShadow(true);

            // restore entity rotation
            entity.xRot = xRot;
            entity.yBodyRot = yBodyRot;
            entity.yHeadRot = yHeadRot;
            entity.xRotO = xRotO;
            entity.yBodyRotO = yBodyRotO;
            entity.yHeadRotO = yHeadRotO;

            // finish
            RenderSystem.enableCull();
            RenderSystem.popMatrix();
        });
    }

    private float updateRotation(LivingEntity entity, float partialTicks, float prevRotationYaw, float yHeadRot, float yHeadRotO) {

        HeadMovement headMovement = this.parent.headMovement;
        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        float defaultRotationYaw = 180.0F + this.parent.position.getRotation(this.maxRotation / 2.0F);
        if (headMovement == HeadMovement.YAW || entity.isFallFlying()) {

            entity.xRot = 7.5F;
            entity.xRotO = 7.5F;
        }

        entity.yBodyRot = defaultRotationYaw;
        entity.yBodyRotO = defaultRotationYaw;
        if (headMovement == HeadMovement.PITCH) {

            entity.yHeadRotO = defaultRotationYaw;
            entity.yHeadRot = defaultRotationYaw;
        } else {

            entity.yHeadRotO = defaultRotationYaw + prevRotationYaw;
            prevRotationYaw = this.rotateEntity(prevRotationYaw, yHeadRot - yHeadRotO, partialTicks);
            entity.yHeadRot = defaultRotationYaw + prevRotationYaw;
        }

        return prevRotationYaw;
    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private float rotateEntity(float rotationYaw, float yBodyRotDiff, float partialTicks) {

        if (Minecraft.getInstance().isPaused()) {

            return rotationYaw;
        }

        // apply rotation change from entity
        rotationYaw = MathHelper.clamp(rotationYaw + yBodyRotDiff * 0.5F, -this.maxRotation, this.maxRotation);
        // rotate back to origin, never overshoot 0
        partialTicks = rotationYaw - partialTicks * rotationYaw / 15.0F;
        if (rotationYaw < 0.0F) {

            rotationYaw = Math.min(0, partialTicks);
        } else if (rotationYaw > 0.0F) {

            rotationYaw = Math.max(0, partialTicks);
        }

        return rotationYaw;
    }

    public void reset() {

        this.prevRotationYaw = 0;
    }

    public enum HeadMovement {

        YAW, PITCH, BOTH
    }

}
