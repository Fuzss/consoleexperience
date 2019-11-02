package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.util.ControlHint;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ControlHintHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final List<ControlHint> hints = Lists.newArrayList();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (this.mc.isGamePaused() || evt.phase != TickEvent.Phase.START) {
            return;
        }

        if (this.mc.player != null && this.mc.currentScreen == null) {

            this.hints.clear();

            this.hints.add(new ControlHint(this.mc.gameSettings.keyBindInventory, new TranslationTextComponent("hudScreen.tooltip.inventory"), ControlHint.Side.LEFT));

            if (this.mc.player.isPassenger()) {
                this.hints.add(new ControlHint(this.mc.gameSettings.keyBindSneak, new TranslationTextComponent("hudScreen.tooltip.dismount"), ControlHint.Side.LEFT));
            }

            if (this.mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
                this.hints.add(new ControlHint(this.mc.gameSettings.keyBindAttack, new TranslationTextComponent("hudScreen.tooltip.mine"), ControlHint.Side.RIGHT));
            } else if (this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                this.hints.add(new ControlHint(this.mc.gameSettings.keyBindAttack, new TranslationTextComponent("hudScreen.tooltip.hit"), ControlHint.Side.RIGHT));
            }

            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (!itemstack.isEmpty()) {

                this.hints.add(new ControlHint(this.mc.gameSettings.keyBindDrop, new TranslationTextComponent("hudScreen.tooltip.dropItem"), ControlHint.Side.LEFT));

                if (itemstack.getItem() instanceof ArmorItem) {

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.mc.playerController.isSpectatorMode() || this.mc.gameSettings.hideGUI) {
            return;
        }

        if (!this.hints.isEmpty()) {

            int x = 1;
            int y = this.mc.mainWindow.getScaledHeight() - 17;
            int max = this.mc.mainWindow.getScaledWidth();

            for (ControlHint hint : this.hints) {

                if (x + hint.getWidth() > max) {
                    break;
                }

                hint.draw(x, y);
                x += hint.getWidth() + 5;

            }

        }

    }

}
