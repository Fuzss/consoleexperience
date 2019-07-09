package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.helper.TooltipShulkerBoxHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MiscHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private LinkedList<Double> list = new LinkedList<>();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup evt) {

        if (!ConfigHandler.miscConfig.elytraTilt) {
            return;
        }

        if (evt.getEntity() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) evt.getEntity();
            if (player.isElytraFlying()) {

                // code from RenderPlayer#applyRotations which is used there for rotating the player model
                Vec3d vec3d = player.getLook((float) evt.getRenderPartialTicks());
                double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
                double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

                if (d0 > 0.0 && d1 > 0.0) {

                    double d2 = (player.motionX * vec3d.xCoord + player.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                    double d3 = player.motionX * vec3d.zCoord - player.motionZ * vec3d.xCoord;

                    // fixed Math#acos returning NaN when d2 > 1.0
                    this.list.add(Math.signum(d3) * Math.acos(Math.min(d2, 1.0)) * 180.0 / (Math.PI * (1.0 / ConfigHandler.miscConfig.elytraMultiplier)));
                    if (this.list.size() > (ConfigHandler.miscConfig.elytraMultiplier > 0.5 ? 5 : 3)) {
                        this.list.removeFirst();
                    }

                    evt.setRoll((float) this.list.stream().mapToDouble(it -> it).average().orElse(0.0));

                }

            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void drawScreen(GuiScreenEvent.DrawScreenEvent evt) {

        if (!ConfigHandler.miscConfig.deathCoords) {
            return;
        }

        if (evt.getGui() instanceof GuiGameOver) {

            try {
                String s = String.format(Locale.ROOT, ConfigHandler.miscConfig.deathCoordsFormat, this.mc.player.posX, this.mc.player.getEntityBoundingBox().minY, this.mc.player.posZ);
                evt.getGui().drawCenteredString(this.mc.fontRendererObj, s, evt.getGui().width / 2, 115, 16777215);
            } catch (IllegalFormatException e) {
                ConsoleHud.LOGGER.error("Caught exception while parsing string format. Go to config file > miscellaneous > Death Coordinates Format to fix this.");
            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void makeTooltip(ItemTooltipEvent evt) {

        if (ConfigHandler.miscConfig.sumShulkerBox && evt.getItemStack().getItem() instanceof ItemShulkerBox) {

            List<String> tooltip = evt.getToolTip();
            List<String> contents = Lists.newArrayList();

            evt.getItemStack().getItem().addInformation(evt.getItemStack(), evt.getEntityPlayer(), contents, this.mc.gameSettings.advancedItemTooltips);

            if (!tooltip.isEmpty() && !contents.isEmpty()) {

                int i = tooltip.indexOf(contents.get(0));

                if (i != -1 && tooltip.removeAll(contents)) {

                    List<String> list = Lists.newArrayList();
                    TooltipShulkerBoxHelper.getLootTableTooltip(list, evt.getItemStack());
                    TooltipShulkerBoxHelper.getContentsTooltip(list, evt.getItemStack(), new Style().setColor(TextFormatting.GRAY), 6);
                    tooltip.addAll(i, list);

                }

            }

        }

    }

}
