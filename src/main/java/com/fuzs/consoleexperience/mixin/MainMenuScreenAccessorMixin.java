package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MainMenuScreen.class)
public interface MainMenuScreenAccessorMixin {

    @Accessor("PANORAMA_RESOURCES")
    static void setPanoramaResources(RenderSkyboxCube panoramaResources) {

        throw new IllegalStateException();
    }

    @Accessor("PANORAMA_OVERLAY_TEXTURES")
    static ResourceLocation getPanoramaOverlayTextures() {

        throw new IllegalStateException();
    }

    @Accessor("MINECRAFT_TITLE_TEXTURES")
    static ResourceLocation getMinecraftTitleTextures() {

        throw new IllegalStateException();
    }

    @Accessor("MINECRAFT_TITLE_EDITION")
    static ResourceLocation getMinecraftTitleEdition() {

        throw new IllegalStateException();
    }

}
