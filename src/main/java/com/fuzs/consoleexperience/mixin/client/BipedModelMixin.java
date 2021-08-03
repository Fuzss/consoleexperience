package com.fuzs.consoleexperience.mixin.client;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BipedModel.class)
public abstract class BipedModelMixin<T extends LivingEntity> extends AgeableModel<T> {

    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public boolean isSneak;

    @Inject(method = "setRotationAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(shift = Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;func_230486_a_(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void setRotationAngles2(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        if (((PlayerAnimationsElement) GameplayElements.PLAYER_ANIMATIONS).getSupermanGliding() && entityIn.getTicksElytraFlying() > 4 && this.isSneak) {

            // superman hand pose
            boolean isRight = entityIn.getPrimaryHand() == HandSide.RIGHT;
            ModelRenderer bipedArm = isRight ? this.bipedRightArm : this.bipedLeftArm;
            bipedArm.rotateAngleX = (float) Math.PI;
            bipedArm.rotateAngleY = 0.0F;
            bipedArm.rotateAngleZ = 0.0F;

            // disable sneaking pose while gliding
            this.isSneak = false;
        }
    }

}