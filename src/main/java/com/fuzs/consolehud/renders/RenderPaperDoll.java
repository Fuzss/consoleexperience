package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderPaperDoll {
    private Minecraft mc;
    private int remainingTicks = 0;
    private int remainingRidingTicks = 0;
    private float rotationYawPrev;
    private float renderYawOffsetPrev;
    private float positionOnScreen;
    private boolean wasActive;

    public RenderPaperDoll(Minecraft mcIn) {
        mc = mcIn;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.mc.isGamePaused() || event.phase != TickEvent.Phase.END || !ConfigHandler.paperDoll)
            return;
        if (this.mc.player != null) {
            boolean sprinting = mc.player.isSprinting() && ConfigHandler.paperDollConfig.sprinting;
            boolean crouching = mc.player.isSneaking() && remainingRidingTicks == 0 && ConfigHandler.paperDollConfig.crouching;
            boolean flying = mc.player.capabilities.isFlying && ConfigHandler.paperDollConfig.flying;
            boolean elytra = mc.player.isElytraFlying() && ConfigHandler.paperDollConfig.elytraFlying;
            boolean burning = mc.player.isBurning() && ConfigHandler.paperDollConfig.burning;
            boolean mounting = mc.player.isRiding() && ConfigHandler.paperDollConfig.riding;

            if (ConfigHandler.paperDollConfig.always || crouching || sprinting || burning || elytra || flying || mounting) {
                remainingTicks = 20;
            } else if (remainingTicks > 0) {
                remainingTicks--;
            }

            if (mc.player.isRiding()) {
                remainingRidingTicks = 10;
            } else if (remainingRidingTicks > 0) {
                remainingRidingTicks--;
            }
        }
    }

    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE && ConfigHandler.paperDollConfig.burning && ConfigHandler.paperDoll) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (this.mc.player != null && ConfigHandler.paperDoll) {
            positionOnScreen = ConfigHandler.paperDollConfig.position > 1 ? 22.5F : -22.5F;
            if (!mc.player.isInvisible() && !mc.playerController.isSpectator() && (!mc.player.isRiding() || ConfigHandler.paperDollConfig.riding || ConfigHandler.paperDollConfig.always) && remainingTicks > 0) {
                if (!wasActive) {
                    rotationYawPrev = positionOnScreen;
                    renderYawOffsetPrev = mc.player.renderYawOffset;
                    wasActive = true;
                }
                int scale = ConfigHandler.paperDollConfig.scale * 5;
                int positionScale = (int) (scale * 1.5F);
                int scaledWidth = event.getResolution().getScaledWidth();
                int scaledHeight = event.getResolution().getScaledHeight();
                int xMargin = ConfigHandler.paperDollConfig.xOffset / event.getResolution().getScaleFactor();
                int yMargin = ConfigHandler.paperDollConfig.yOffset / event.getResolution().getScaleFactor();
                int x = ConfigHandler.paperDollConfig.position > 1 ? scaledWidth - positionScale - xMargin : positionScale + xMargin;
                int y = ConfigHandler.paperDollConfig.position % 2 == 0 ? (int) (scale * 2.5F) + yMargin : scaledHeight - positionScale - yMargin;
                drawEntityOnScreen((x % scaledWidth + scaledWidth) % scaledWidth, (y % scaledHeight + scaledWidth) % scaledWidth, scale, mc.player, event.getPartialTicks());
            } else if (wasActive) {
                wasActive = false;
            }
        }
    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    private void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent, float partialTicks)
    {
        GlStateManager.enableDepth();
        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(40 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        rotateEntity(ent.renderYawOffset - renderYawOffsetPrev, partialTicks);
        renderYawOffsetPrev = ent.renderYawOffset;
        ent.renderYawOffset = rotationYawPrev;
        ent.rotationYawHead = rotationYawPrev;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
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
