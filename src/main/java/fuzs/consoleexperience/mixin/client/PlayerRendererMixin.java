package fuzs.consoleexperience.mixin.client;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public PlayerRendererMixin(EntityRendererManager renderManager, boolean useSmallArms) {

        super(renderManager, new PlayerModel<>(0.0F, useSmallArms), 0.5F);
    }

    @Inject(method = "getRenderOffset", at = @At("HEAD"), cancellable = true)
    public void getRenderOffset(AbstractClientPlayerEntity entityIn, float partialTicks, CallbackInfoReturnable<Vector3d> callbackInfo) {

        // prevent player model from shifting downwards when gliding and sneaking at the same time
        PlayerAnimationsElement element = (PlayerAnimationsElement) ConsoleExperience.PLAYER_ANIMATIONS;
        if (element.isEnabled() && element.supermanGliding && entityIn.isCrouching() && entityIn.getFallFlyingTicks() > 4) {

            callbackInfo.setReturnValue(Vector3d.ZERO);
        }
    }

    @ModifyVariable(method = "setupRotations", ordinal = 2, at = @At(value = "INVOKE", target = "Ljava/lang/Math;acos(D)D", shift = At.Shift.BEFORE))
    public double preAcos(double d2) {

        // fix Math#acos returning NaN when d2 > 1.0 so player model doesn't occasionally vanish when gliding
        return Math.min(d2, 1.0);
    }

}
