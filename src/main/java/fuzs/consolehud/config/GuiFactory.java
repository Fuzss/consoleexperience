package fuzs.consolehud.config;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory {

    public void initialize(Minecraft minecraftInstance) {
    }

    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }

    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return ConfigGui.class;
    }
}
