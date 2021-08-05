package fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MainMenuScreen.class)
public interface MainMenuScreenAccessor {

    @Accessor("PANORAMA_OVERLAY")
    static ResourceLocation getPanoramaOverlayTextures() {

        throw new IllegalStateException();
    }

    @Accessor("MINECRAFT_LOGO")
    static ResourceLocation getMinecraftTitleTextures() {

        throw new IllegalStateException();
    }

    @Accessor("MINECRAFT_EDITION")
    static ResourceLocation getMinecraftTitleEdition() {

        throw new IllegalStateException();
    }

    @Accessor
    void setPanorama(RenderSkybox panorama);

}
