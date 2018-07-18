package fuzs.consolehud.renders;

import fuzs.consolehud.config.ConfigHandler;
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
            if (ConfigHandler.fireOnDoll) {
                if ((mc.player.isSneaking() && remainingRidingTicks == 0) || mc.player.isSprinting() || mc.player.isBurning() || mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
                    remainingTicks = 20;
                } else if (remainingTicks > 0) {
                    remainingTicks--;
                }
            } else {
                if ((mc.player.isSneaking() && remainingRidingTicks == 0) || mc.player.isSprinting() || mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
                    remainingTicks = 20;
                } else if (remainingTicks > 0) {
                    remainingTicks--;
                }
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
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE && ConfigHandler.fireOnDoll && ConfigHandler.paperDoll) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderGameOverlayText(RenderGameOverlayEvent.Text event) { // this is not the right event, but there are problems with pre and post
        if (this.mc.player != null && ConfigHandler.paperDoll) {
            positionOnScreen = ConfigHandler.dollPosition > 1 ? 22.5F : -22.5F;
            if (!mc.player.isInvisible() && !mc.playerController.isSpectator() && !mc.player.isRiding() && remainingTicks > 0) {
                if (!wasActive) {
                    rotationYawPrev = positionOnScreen;
                    renderYawOffsetPrev = mc.player.renderYawOffset;
                    wasActive = true;
                }
                drawEntityOnScreen(ConfigHandler.dollPosition > 1 ? event.getResolution().getScaledWidth() - 30 : 30, ConfigHandler.dollPosition % 2 == 0 ? 50 : event.getResolution().getScaledHeight() - 30, 20, mc.player);
            } else if (wasActive) {
                wasActive = false;
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
        rotateEntity(ent.renderYawOffset - renderYawOffsetPrev);
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
    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private void rotateEntity(float renderYawOffsetDiff) {
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
        if (rotationYawPrev > positionOnScreen) {
            rotationYawPrev -= 1F;
        } else if (rotationYawPrev < positionOnScreen) {
            rotationYawPrev += 1F;
        }
        rotationYawPrev = Math.round(rotationYawPrev * 50F) / 50F;
    }
}
