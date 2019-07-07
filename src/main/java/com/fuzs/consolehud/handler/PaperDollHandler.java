package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.helper.PaperDollHelper;
import com.fuzs.consolehud.util.EnumPositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@SuppressWarnings("unused")
public class PaperDollHandler {

    private final Minecraft mc = Minecraft.getInstance();

    private int remainingDisplayTicks;
    private int remainingRidingTicks;
    private float prevRotationYaw;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.mc.player != null) {

            if (ConfigHandler.GENERAL_CONFIG.paperDoll.get() && (ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.always.get() || PaperDollHelper.showDoll(this.mc.player, this.remainingRidingTicks))) {
                this.remainingDisplayTicks = ConfigHandler.PAPER_DOLL_CONFIG.displayTime.get();
            } else if (this.remainingDisplayTicks > 0) {
                this.remainingDisplayTicks--;
            } else {
                this.prevRotationYaw = 0;
            }

            // don't show paper doll in sneaking position after unmounting a vehicle / mount
            if (ConfigHandler.GENERAL_CONFIG.paperDoll.get() && this.mc.player.isPassenger()) {
                this.remainingRidingTicks = 10;
            } else if (this.remainingRidingTicks > 0) {
                this.remainingRidingTicks--;
            }

        }

    }

    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.mc.player != null) {

            boolean riding = ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.always.get() || ConfigHandler.PAPER_DOLL_CONFIG.displayActionsConfig.riding.get() || !this.mc.player.isPassenger();

            if (!this.mc.player.isInvisible() && !this.mc.playerController.isSpectatorMode() && (this.mc.gameSettings.thirdPersonView == 0 || !ConfigHandler.PAPER_DOLL_CONFIG.firstPerson.get()) && riding && this.remainingDisplayTicks > 0) {

                int scale = ConfigHandler.PAPER_DOLL_CONFIG.scale.get() * 5;
                EnumPositionPreset position = ConfigHandler.PAPER_DOLL_CONFIG.position.get();

                int x = position.getX(0, evt.getWindow().getScaledWidth(), (int) (scale * 1.5F) + ConfigHandler.PAPER_DOLL_CONFIG.xOffset.get());

                // can't use EnumPositionPreset#getY as the orientation point isn't in the top left corner of the image
                int yOffset = ConfigHandler.PAPER_DOLL_CONFIG.yOffset.get();
                int y = position.isBottom() ? evt.getWindow().getScaledHeight() - scale - yOffset : (int) (scale * 2.5F) + yOffset;

                if (ConfigHandler.PAPER_DOLL_CONFIG.potionShift.get() && position.shouldShift()) {
                    y += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
                }

                this.prevRotationYaw = PaperDollHelper.drawEntityOnScreen(this.mc, x, y, scale, this.mc.player, evt.getPartialTicks(), this.prevRotationYaw);

            }

        }

    }

    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent evt) {
        if (ConfigHandler.GENERAL_CONFIG.paperDoll.get() && ConfigHandler.PAPER_DOLL_CONFIG.burning.get() && evt.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            evt.setCanceled(true);
        }
    }

}
