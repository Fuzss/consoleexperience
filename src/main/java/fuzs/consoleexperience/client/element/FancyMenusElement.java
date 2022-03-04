package fuzs.consoleexperience.client.element;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.gui.screen.*;
import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import fuzs.consoleexperience.mixin.client.accessor.DisconnectedScreenAccessor;
import fuzs.consoleexperience.mixin.client.accessor.MainMenuScreenAccessor;
import fuzs.consoleexperience.mixin.client.accessor.WorldLoadProgressScreenAccessor;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.side.IClientElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.resources.*;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class FancyMenusElement extends AbstractElement implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void constructClient() {

        this.addListener(this::onGuiOpen);
        this.addListener(this::onDrawScreenPre);
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Replace boring dirt backgrounds with the fancy main menu panorama and some bold fonts."};
    }

    @Override
    public void setupClient() {

        this.addResourcePack();
    }

    private void onGuiOpen(final GuiOpenEvent evt) {

        if (evt.getGui() instanceof MainMenuScreen) {

            ((MainMenuScreenAccessor) evt.getGui()).setPanorama(FancyScreenUtil.MENU_PANORAMA);
        } else if (evt.getGui() instanceof WorkingScreen) {

            evt.setGui(new FancyWorkingScreen((WorkingScreen) evt.getGui()));
        } else if (evt.getGui() instanceof DownloadTerrainScreen) {

            evt.setGui(new FancyDownloadTerrainScreen());
        } else if (evt.getGui() instanceof DirtMessageScreen) {

            evt.setGui(new FancyDirtMessageScreen(evt.getGui().getTitle()));
        } else if (evt.getGui() instanceof WorldLoadProgressScreen) {

            evt.setGui(new FancyWorldLoadProgressScreen(((WorldLoadProgressScreenAccessor) (evt.getGui())).getProgressListener()));
        } else if (evt.getGui() instanceof DisconnectedScreen) {

            DisconnectedScreenAccessor accessor = (DisconnectedScreenAccessor) evt.getGui();
            evt.setGui(new FancyDisconnectedScreen(accessor.getParent(), evt.getGui().getTitle(), accessor.getReason()));
        } else if (evt.getGui() instanceof ConnectingScreen) {

            FancyConnectingScreen.onGuiOpen();
        }
    }

    private void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre evt) {

        if (evt.getGui() instanceof ConnectingScreen) {

            evt.setCanceled(true);
            FancyConnectingScreen.render(this.mc, evt.getMatrixStack(), evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks(), (ConnectingScreen) evt.getGui());
        }
    }

    private void addResourcePack() {

        ResourcePackList packList = Minecraft.getInstance().getResourcePackRepository();
        packList.addPackFinder(new IPackFinder() {

            @Override
            public void loadPacks(Consumer<ResourcePackInfo> p_230230_1_, ResourcePackInfo.IFactory packInfoFactory) {

                try {
                    File file = new File(this.getClass().getResource("/fancy_menus.zip").toURI());
                    ResourcePackInfo pack = ResourcePackInfo.create(ConsoleExperience.MODID, false, () -> createProgrammerArtZipPack(file), packInfoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN);
                    p_230230_1_.accept(pack);
                } catch (URISyntaxException e) {
                    ConsoleExperience.LOGGER.error("Unable to load fancy menus resource pack", e);
                }

            }

        });
    }

    private IResourcePack createProgrammerArtZipPack(File file) {

        return new FilePack(file) {

            @Override
            public String getName() {

                return FancyMenusElement.this.getDisplayName();
            }

            @SuppressWarnings("unchecked")
            @Nullable
            @Override
            public <T> T getMetadataSection(IMetadataSectionSerializer<T> deserializer) {

                if (deserializer == PackMetadataSection.SERIALIZER) {

                    return (T) new PackMetadataSection(new StringTextComponent("More fancy menus with the looks."), SharedConstants.getCurrentVersion().getPackVersion());
                }

                return null;
            }

        };

    }

}
