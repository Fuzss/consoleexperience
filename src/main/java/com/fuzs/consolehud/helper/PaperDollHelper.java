package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import net.minecraft.client.entity.EntityPlayerSP;

public class PaperDollHelper {

    public static boolean showDoll(EntityPlayerSP player, int remainingRidingTicks) {

        boolean sprinting = ConfigHandler.paperDollConfig.displayActionsConfig.sprinting && player.isSprinting();
        boolean crouching = ConfigHandler.paperDollConfig.displayActionsConfig.crouching && player.isSneaking() && remainingRidingTicks == 0;
        boolean flying = ConfigHandler.paperDollConfig.displayActionsConfig.flying && player.capabilities.isFlying;
        boolean elytra = ConfigHandler.paperDollConfig.displayActionsConfig.elytraFlying && player.isElytraFlying();
        boolean burning = ConfigHandler.paperDollConfig.displayActionsConfig.burning && player.isBurning();
        boolean mounting = ConfigHandler.paperDollConfig.displayActionsConfig.riding && player.isRiding();

        return crouching || sprinting || burning || elytra || flying || mounting;

    }

}
