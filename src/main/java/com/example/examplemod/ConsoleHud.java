package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ConsoleHud.MODID, name = ConsoleHud.NAME, version = ConsoleHud.VERSION)
public class ConsoleHud
{
    public static final String MODID = "consolehud";
    public static final String NAME = "Console HUD";
    public static final String VERSION = "1.0";

    private final Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        RenderSelectedItem rsi = new RenderSelectedItem(mc);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(rsi);
    }

    @SubscribeEvent
    public void onPlayerPickupXp(PlayerPickupXpEvent event) {
        // Rewrite this event's handling.
        event.setCanceled(true);

        EntityPlayer player = event.getEntityPlayer();
        EntityXPOrb orb = event.getOrb();

        // See EntityXPOrb#onCollideWithPlayer for details.
        // All requirements for picking up XP are met at this point.

        // -> EntityPlayer#xpCooldown is set to 2.
        player.xpCooldown = 2;

        // -> EntityPlayer#onItemPickup is called with the xp orb and 1 (quantity).
        player.onItemPickup(orb, 1);

        // -> The XP are added to the player's experience.
        player.addExperience(orb.xpValue);

        // -> The XP orb is killed.
        orb.setDead();
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        // check if the target item has mending and if there is something in the right slot to possibly repair with
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, event.getLeft()) > 0 && !event.getRight().isEmpty()) {

            // check if the item in the right slot is either a repair material or an unenchanted tool
            //if (!event.getRight().isItemEnchanted() || !event.getRight().isItemStackDamageable()) {

                System.out.println("The COST before is: " + event.getCost());
                // j = j + event.getLeft().getRepairCost() + (event.getRight().isEmpty() ? 0 : event.getRight().getRepairCost());
                //event.setCost((event.getCost() - 1) / 2);
                event.getOutput().setRepairCost((event.getCost() - 5) / 2);
                System.out.println("The COST after is: " + event.getCost());
                System.out.println("The COST after is: " + event.getOutput().getRepairCost());
            //}
        }
    }
}
