package fuzs.consolehud;

import fuzs.consolehud.config.ConfigHandler;
import fuzs.consolehud.renders.RenderPaperDoll;
import fuzs.consolehud.renders.RenderSelectedItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = ConsoleHud.MODID, name = ConsoleHud.NAME, version = ConsoleHud.VERSION, acceptedMinecraftVersions = ConsoleHud.AVERSIONS, guiFactory = ConsoleHud.GUI, clientSideOnly = ConsoleHud.CLIENT)
public class ConsoleHud
{
    public static final String MODID = "consolehud";
    public static final String NAME = "Console HUD";
    public static final String VERSION = "1.1.1";
    public static final String AVERSIONS = "[1.12,1.12.2]";
    public static final String GUI = "fuzs.consolehud.config.GuiFactory";
    public static final boolean CLIENT = true;

    private final Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ConfigHandler.init(new File(event.getModConfigurationDirectory(), MODID + ".cfg"));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RenderSelectedItem si = new RenderSelectedItem(mc);
        RenderPaperDoll pd = new RenderPaperDoll(mc);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(si);
        MinecraftForge.EVENT_BUS.register(pd);
    }
}
