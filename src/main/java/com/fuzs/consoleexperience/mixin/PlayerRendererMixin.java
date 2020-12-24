package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public PlayerRendererMixin(EntityRendererManager renderManager, boolean useSmallArms) {

        super(renderManager, new PlayerModel<>(0.0F, useSmallArms), 0.5F);
    }

    @Inject(method = "getRenderOffset(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void getRenderOffset(AbstractClientPlayerEntity entityIn, float partialTicks, CallbackInfoReturnable<Vec3d> cir) {

        // prevent player model from shifting downwards when gliding and sneaking at the same time
        if (((PlayerAnimationsElement) GameplayElements.PLAYER_ANIMATIONS).getSupermanGliding() && entityIn.isCrouching() && entityIn.getTicksElytraFlying() > 4) {

            cir.setReturnValue(Vec3d.ZERO);
        }
    }

    @ModifyVariable(method = "applyRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", ordinal = 2, at = @At(shift = Shift.BEFORE, value = "INVOKE", target = "Ljava/lang/Math;acos(D)D"))
    private double applyRotations(double d2) {

        // fix Math#acos returning NaN when d2 > 1.0
        return Math.min(d2, 1.0);
    }

}
