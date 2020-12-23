package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MainMenuScreen.class)
public interface MainMenuScreenAccessorMixin {

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

    @Accessor
    void setPanorama(RenderSkybox panorama);

}
