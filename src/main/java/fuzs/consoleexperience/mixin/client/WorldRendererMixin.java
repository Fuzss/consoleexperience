package fuzs.consoleexperience.mixin.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings({"unused", "deprecation"})
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements IResourceManagerReloadListener, AutoCloseable{

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;isDetached()Z"))
    public boolean isDetached(ActiveRenderInfo activeRenderInfo) {

        return true;
    }

}
