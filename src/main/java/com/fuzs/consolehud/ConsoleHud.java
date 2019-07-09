package com.fuzs.consolehud;

import com.fuzs.consolehud.handler.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ConsoleHud.MODID,
        name = ConsoleHud.NAME,
        version = ConsoleHud.VERSION,
        acceptedMinecraftVersions = ConsoleHud.RANGE,
        clientSideOnly = ConsoleHud.CLIENT,
        certificateFingerprint = ConsoleHud.FINGERPRINT
)
@SuppressWarnings({"WeakerAccess", "unused"})
public class ConsoleHud
{
    public static final String MODID = "consolehud";
    public static final String NAME = "Console HUD";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.11, 1.11.2]";
    public static final boolean CLIENT = true;
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(ConsoleHud.NAME);

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new SelectedItemHandler());
        MinecraftForge.EVENT_BUS.register(new PaperDollHandler());
        MinecraftForge.EVENT_BUS.register(new HoveringHotbarHandler());
        MinecraftForge.EVENT_BUS.register(new SaveIconHandler());
        MinecraftForge.EVENT_BUS.register(new MiscHandler());

    }

    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}