package fuzs.consolehud;

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

public class PaperDoll {
    private Minecraft mc;
    private int remainingTicks = 0;
    private int remainingRidingTicks = 0;
    private float rotationYawHeadOld = -22.5F;
    private float renderYawOffsetOld;
    private boolean wasOn;

    public PaperDoll(Minecraft mcIn) {
        mc = mcIn;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.mc.isGamePaused() || event.phase != TickEvent.Phase.END)
            return;

        if (this.mc.player != null) {
            if ((mc.player.isSneaking() && remainingRidingTicks == 0) || mc.player.isSprinting() || mc.player.isBurning() || mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
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
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderGameOverlayPost(RenderGameOverlayEvent.Text event) {
        if (this.mc.player != null) {
            if (!mc.gameSettings.showDebugInfo && !mc.player.isInvisible() && !mc.playerController.isSpectator() && !mc.player.isRiding() && remainingTicks > 0) {
                if (!wasOn) {
                    rotationYawHeadOld = -22.5F;
                    renderYawOffsetOld = mc.player.renderYawOffset;
                    wasOn = true;
                }
                drawEntityOnScreen(30, 50, 20, mc.player);
            } else if (wasOn) {
                wasOn = false;
            }
        }
    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    private void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
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
        float renderYawOffsetDiff = ent.renderYawOffset - renderYawOffsetOld;
        renderYawOffsetOld = ent.renderYawOffset;
        if (rotationYawHeadOld < 22.5F) {
            rotationYawHeadOld -= renderYawOffsetDiff;
        } else {
            rotationYawHeadOld += renderYawOffsetDiff;
        }
        rotationYawHeadOld = rotationYawHeadOld > 22.5F ? 22.5F : rotationYawHeadOld < -67.5F ? -67.5F : rotationYawHeadOld;
        if (rotationYawHeadOld > -22.5F) {
            rotationYawHeadOld -= 1F;
        } else if (rotationYawHeadOld < -22.5F) {
            rotationYawHeadOld += 1F;
        }
        rotationYawHeadOld = Math.round(rotationYawHeadOld * 50F) / 50F;
        ent.renderYawOffset = rotationYawHeadOld;
        ent.rotationYawHead = rotationYawHeadOld;
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
    }
}
