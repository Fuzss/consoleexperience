package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.PaperDollHelper;
import com.fuzs.consoleexperience.util.PositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PaperDollHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final PaperDollHelper helper = new PaperDollHelper(this.mc);

    private int remainingDisplayTicks;
    private int remainingRidingTicks;
    private float prevRotationYaw;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null && this.mc.player.movementInput != null) {

            // update display ticks
            if (ConfigBuildHandler.GENERAL_CONFIG.paperDoll.get() && (ConfigBuildHandler.PAPER_DOLL_CONFIG.displayTime.get() == 0 || this.helper.checkConditions(this.remainingRidingTicks))) {
                this.remainingDisplayTicks = ConfigBuildHandler.PAPER_DOLL_CONFIG.displayTime.get() == 0 ? 1 : ConfigBuildHandler.PAPER_DOLL_CONFIG.displayTime.get();
            } else if (this.remainingDisplayTicks > 0) {
                this.remainingDisplayTicks--;
            } else {
                this.prevRotationYaw = 0;
            }

            // don't show paper doll in sneaking position after unmounting a vehicle / mount
            if (ConfigBuildHandler.GENERAL_CONFIG.paperDoll.get() && this.mc.player.isPassenger()) {
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

            boolean flag = !this.mc.player.isInvisible() && !this.mc.playerController.isSpectatorMode() && !this.mc.gameSettings.hideGUI;
            boolean firstPerson = this.mc.gameSettings.thirdPersonView == 0 || !ConfigBuildHandler.PAPER_DOLL_CONFIG.firstPerson.get();
            boolean hideGui = ConfigBuildHandler.GENERAL_CONFIG.hideHudInGui.get() && this.mc.currentScreen instanceof ContainerScreen;

            if (flag && firstPerson && !hideGui && this.remainingDisplayTicks > 0) {

                int scale = ConfigBuildHandler.PAPER_DOLL_CONFIG.scale.get() * 5;
                PositionPreset position = ConfigBuildHandler.PAPER_DOLL_CONFIG.position.get();

                int x = position.getX(0, evt.getWindow().getScaledWidth(), (int) (scale * 1.5F) + ConfigBuildHandler.PAPER_DOLL_CONFIG.xOffset.get());

                // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
                int yOffset = ConfigBuildHandler.PAPER_DOLL_CONFIG.yOffset.get();
                int y = position.isBottom() ? evt.getWindow().getScaledHeight() - scale - yOffset : (int) (scale * 2.5F) + yOffset;
                y -= scale - this.helper.updateOffset(evt.getPartialTicks()) * scale;

                if (ConfigBuildHandler.PAPER_DOLL_CONFIG.potionShift.get() && position.shouldShift()) {
                    y += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
                }

                this.prevRotationYaw = this.helper.drawEntityOnScreen(x, y, scale, this.mc.player, evt.getPartialTicks(), this.prevRotationYaw);

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent evt) {

        if (ConfigBuildHandler.GENERAL_CONFIG.paperDoll.get() && ConfigBuildHandler.PAPER_DOLL_CONFIG.burning.get() && evt.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            evt.setCanceled(true);
        }

    }

}
