package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.handler.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@SuppressWarnings("unused")
public class RenderPaperDoll {

    private final Minecraft mc;
    private int remainingTicks = 0;
    private int remainingRidingTicks = 0;
    private float rotationYawPrev;
    private float renderYawOffsetPrev;
    private float positionOnScreen;
    private boolean wasActive;

    public RenderPaperDoll(Minecraft mcIn) {
        this.mc = mcIn;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            if (ConfigHandler.paperDoll && (ConfigHandler.paperDollConfig.displayActionsConfig.always || this.showDoll(this.mc.player))) {
                this.remainingTicks = ConfigHandler.paperDollConfig.displayTime;
            } else if (this.remainingTicks > 0) {
                this.remainingTicks--;
            }

            if (ConfigHandler.paperDoll && this.mc.player.isRiding()) {
                this.remainingRidingTicks = 10;
            } else if (remainingRidingTicks > 0) {
                this.remainingRidingTicks--;
            }

        }

    }

    private boolean showDoll(EntityPlayerSP player) {

        boolean sprinting = ConfigHandler.paperDollConfig.displayActionsConfig.sprinting && player.isSprinting();
        boolean crouching = ConfigHandler.paperDollConfig.displayActionsConfig.crouching && player.isSneaking() && remainingRidingTicks == 0;
        boolean flying = ConfigHandler.paperDollConfig.displayActionsConfig.flying && player.capabilities.isFlying;
        boolean elytra = ConfigHandler.paperDollConfig.displayActionsConfig.elytraFlying && player.isElytraFlying();
        boolean burning = ConfigHandler.paperDollConfig.displayActionsConfig.burning && player.isBurning();
        boolean mounting = ConfigHandler.paperDollConfig.displayActionsConfig.riding && player.isRiding();

        return crouching || sprinting || burning || elytra || flying || mounting;

    }

    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent evt) {
        if (ConfigHandler.paperDoll && ConfigHandler.paperDollConfig.displayActionsConfig.burning && evt.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.mc.player != null) {

            this.positionOnScreen = ConfigHandler.paperDollConfig.oldPosition > 1 ? 22.5F : -22.5F;
            boolean riding = ConfigHandler.paperDollConfig.displayActionsConfig.always || ConfigHandler.paperDollConfig.displayActionsConfig.riding || !this.mc.player.isRiding();

            if (!mc.player.isInvisible() && !this.mc.playerController.isSpectator() && riding && this.remainingTicks > 0) {

                if (!this.wasActive) {
                    this.rotationYawPrev = this.positionOnScreen;
                    this.renderYawOffsetPrev = this.mc.player.renderYawOffset;
                    this.wasActive = true;
                }

                int scale = ConfigHandler.paperDollConfig.scale * 5;
                int positionScale = (int) (scale * 1.5F);

                int scaledWidth = evt.getResolution().getScaledWidth();
                int scaledHeight = evt.getResolution().getScaledHeight();

                int xMargin = ConfigHandler.paperDollConfig.xOffset / evt.getResolution().getScaleFactor();
                int yMargin = ConfigHandler.paperDollConfig.yOffset / evt.getResolution().getScaleFactor();
                int x = ConfigHandler.paperDollConfig.oldPosition > 1 ? scaledWidth - positionScale - xMargin : positionScale + xMargin;
                int y = ConfigHandler.paperDollConfig.oldPosition % 2 == 0 ? (int) (scale * 2.5F) + yMargin : scaledHeight - positionScale - yMargin;

                // 12 40
                //drawEntityOnScreen((x % scaledWidth + scaledWidth) % scaledWidth, (y % scaledHeight + scaledWidth) % scaledWidth, scale, this.mc.player, evt.getPartialTicks());
                drawEntityOnScreen(ConfigHandler.paperDollConfig.xOffset, ConfigHandler.paperDollConfig.yOffset, scale, this.mc.player, evt.getPartialTicks());

            } else if (this.wasActive) {
                this.wasActive = false;
            }

        }

    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    private void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase entity, float partialTicks) {

        GlStateManager.enableDepth();
        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = entity.renderYawOffset;
        float f1 = entity.rotationYaw;
        float f2 = entity.rotationPitch;
        float f3 = entity.prevRotationYawHead;
        float f4 = entity.rotationYawHead;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(40 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        this.rotateEntity(entity.renderYawOffset - renderYawOffsetPrev, partialTicks);
        renderYawOffsetPrev = entity.renderYawOffset;
        entity.renderYawOffset = rotationYawPrev;
        entity.rotationYawHead = rotationYawPrev;

        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        // boolean parameter forces the bounding box to always be hidden
        rendermanager.renderEntity(entity, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, true);
        rendermanager.setRenderShadow(true);

        entity.renderYawOffset = f;
        entity.rotationYaw = f1;
        entity.rotationPitch = f2;
        entity.prevRotationYawHead = f3;
        entity.rotationYawHead = f4;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private void rotateEntity(float renderYawOffsetDiff, float partialTicks) {

        if (rotationYawPrev < -positionOnScreen) {
            rotationYawPrev -= renderYawOffsetDiff;
        } else {
            rotationYawPrev += renderYawOffsetDiff;
        }
        if (rotationYawPrev > positionOnScreen + 45F) {
            rotationYawPrev = positionOnScreen + 45F;
        } else if (rotationYawPrev < positionOnScreen - 45F) {
            rotationYawPrev = positionOnScreen - 45F;
        }
        if (rotationYawPrev > positionOnScreen + 0.5F) {
            rotationYawPrev -= partialTicks * 2F;
        } else if (rotationYawPrev < positionOnScreen - 0.5F) {
            rotationYawPrev += partialTicks * 2F;
        }
        rotationYawPrev = Math.round(rotationYawPrev * 50F) / 50F;

    }
}
