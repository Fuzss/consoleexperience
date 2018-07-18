package fuzs.consolehud.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public  class ConfigGui extends net.minecraftforge.fml.client.config.GuiConfig {
    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.categoryGeneral)).getChildElements(), "consolehud", false, false, net.minecraftforge.fml.client.config.GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }
}
