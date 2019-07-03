package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.helper.TooltipShulkerBoxHelper;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class MiscHandler {

    private LinkedList<Double> list = new LinkedList<>();

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
                double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

                if (d0 > 0.0 && d1 > 0.0) {

                    double d2 = (player.motionX * vec3d.x + player.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                    double d3 = player.motionX * vec3d.z - player.motionZ * vec3d.x;

                    // fixed Math#acos returning NaN when d2 > 1.0
                    this.list.add(Math.signum(d3) * Math.acos(Math.min(d2, 1.0)) * 180.0 / (Math.PI * 2));
                    if (this.list.size() > 3) {
                        this.list.removeFirst();
                    }

                    evt.setRoll((float) this.list.stream().mapToDouble(it -> it).average().orElse(0.0));

                }

            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void makeTooltip(ItemTooltipEvent evt) {

        if (ConfigHandler.miscConfig.sumShulkerBox && evt.getItemStack().getItem() instanceof ItemShulkerBox) {

            List<String> tooltip = evt.getToolTip();
            List<String> contents = Lists.newArrayList();

            evt.getItemStack().getItem().addInformation(evt.getItemStack(), evt.getEntityPlayer() == null ? null : evt.getEntityPlayer().world, contents, evt.getFlags());

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
