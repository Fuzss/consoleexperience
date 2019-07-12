package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.helper.PaperDollHelper;
import com.fuzs.consolehud.util.EnumPositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PaperDollHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    private int remainingDisplayTicks;
    private int remainingRidingTicks;
    private float prevRotationYaw;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            if (ConfigHandler.paperDoll && (ConfigHandler.paperDollConfig.displayTime == 0 || PaperDollHelper.showDoll(this.mc.player, this.remainingRidingTicks))) {
                this.remainingDisplayTicks = ConfigHandler.paperDollConfig.displayTime == 0 ? 1 : ConfigHandler.paperDollConfig.displayTime;
            } else if (this.remainingDisplayTicks > 0) {
                this.remainingDisplayTicks--;
            } else {
                this.prevRotationYaw = 0;
            }

            // don't show paper doll in sneaking position after unmounting a vehicle / mount
            if (ConfigHandler.paperDoll && this.mc.player.isRiding()) {
                this.remainingRidingTicks = 10;
            } else if (this.remainingRidingTicks > 0) {
                this.remainingRidingTicks--;
            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.mc.player != null) {

            boolean riding = ConfigHandler.paperDollConfig.displayTime == 0 || ConfigHandler.paperDollConfig.displayActionsConfig.riding || !this.mc.player.isRiding();

            if (!this.mc.player.isInvisible() && !this.mc.playerController.isSpectator() && (this.mc.gameSettings.thirdPersonView == 0 || !ConfigHandler.paperDollConfig.firstPerson) && riding && this.remainingDisplayTicks > 0) {

                int scale = ConfigHandler.paperDollConfig.scale * 5;
                EnumPositionPreset position = ConfigHandler.paperDollConfig.position;

                int x = position.getX(0, evt.getResolution().getScaledWidth(), (int) (scale * 1.5F) + ConfigHandler.paperDollConfig.xOffset);

                // can't use EnumPositionPreset#getY as the orientation point isn't in the top left corner of the image
                int yOffset = ConfigHandler.paperDollConfig.yOffset;
                int y = position.isBottom() ? evt.getResolution().getScaledHeight() - scale - yOffset : (int) (scale * 2.5F) + yOffset;

                if (ConfigHandler.paperDollConfig.potionShift && position.shouldShift()) {
                    y += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
                }

                this.prevRotationYaw = PaperDollHelper.drawEntityOnScreen(this.mc, x, y, scale, this.mc.player, evt.getPartialTicks(), this.prevRotationYaw);

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent evt) {

        if (ConfigHandler.paperDoll && ConfigHandler.paperDollConfig.burning && evt.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            evt.setCanceled(true);
        }

    }

}
