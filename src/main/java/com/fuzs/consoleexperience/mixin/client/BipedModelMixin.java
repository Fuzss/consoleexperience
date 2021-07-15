package com.fuzs.consoleexperience.mixin.client;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
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

    public BipedModelMixin() {

        super(RenderType::getEntityCutoutNoCull, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
    }

    @Inject(method = "setRotationAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(shift = Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;func_230486_a_(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void setRotationAngles1(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        int inUseCount = entityIn.getItemInUseCount();
        if (((PlayerAnimationsElement) GameplayElements.PLAYER_ANIMATIONS).getEatingAnimation() && entityIn.isHandActive() && inUseCount > 0) {

            Hand hand = entityIn.getActiveHand();
            ItemStack stack = entityIn.getHeldItem(hand);
            HandSide handSide = entityIn.getPrimaryHand();
            if (stack.getUseAction() == UseAction.EAT || stack.getUseAction() == UseAction.DRINK) {

                boolean isRight = (hand == Hand.MAIN_HAND ? handSide : handSide.opposite()) == HandSide.RIGHT;
                float partialTicks = MathHelper.frac(ageInTicks);
                float animationCount = inUseCount - partialTicks + 1.0F;
                float useRatio = animationCount / (float) stack.getUseDuration();
                float f = 1.0F - (float) Math.pow(useRatio, 27.0D);
                if (useRatio < 0.8F) {

                    f += MathHelper.abs(MathHelper.cos(animationCount / 4.0F * (float)Math.PI) * 0.1F);
                }

                ModelRenderer bipedArm = isRight ? this.bipedRightArm : this.bipedLeftArm;
                bipedArm.rotateAngleX = f * (bipedArm.rotateAngleX * 0.5F - ((float) Math.PI * 4.0F / 10.0F));
                bipedArm.rotateAngleY = f * (float) Math.PI / 6F * (isRight ? -1.0F : 1.0F);
            }
        }
    }

    @Inject(method = "setRotationAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(shift = Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;func_230486_a_(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void setRotationAngles2(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        boolean isElytraFlying = entityIn.getTicksElytraFlying() > 4;
        if (((PlayerAnimationsElement) GameplayElements.PLAYER_ANIMATIONS).getSupermanGliding() && isElytraFlying && this.isSneak) {

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

    @Inject(method = "setRotationAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(shift = Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;func_230486_a_(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void setRotationAngles3(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        // rotate arms same way as paddles so it looks like the player is actually doing something
        if (((PlayerAnimationsElement) GameplayElements.PLAYER_ANIMATIONS).getRowingAnimation() && entityIn.getRidingEntity() instanceof BoatEntity) {

            BoatEntity boatEntity = (BoatEntity) entityIn.getRidingEntity();

            float timeRight = boatEntity.getRowingTime(1, limbSwing);
            if (timeRight > 0.0F) {

                this.bipedRightArm.rotateAngleX = (float)MathHelper.clampedLerp(-(float)Math.PI / 3.0F, -(float)Math.PI / 12.0F, (MathHelper.sin(-timeRight) + 1.0F) / 2.0F);
                this.bipedRightArm.rotateAngleY = (float)MathHelper.clampedLerp(-(float)Math.PI / 24.0F, (float)Math.PI / 3.0F, (MathHelper.sin(-timeRight + 1.0F) + 1.0F) / 2.0F);
            }

            float timeLeft = boatEntity.getRowingTime(0, limbSwing);
            if (timeLeft > 0.0F) {

                this.bipedLeftArm.rotateAngleX = (float)MathHelper.clampedLerp(-(float)Math.PI / 3.0F, -(float)Math.PI / 12.0F, (MathHelper.sin(-timeLeft) + 1.0F) / 2.0F);
                this.bipedLeftArm.rotateAngleY = -(float)MathHelper.clampedLerp(-(float)Math.PI / 24.0F, (float)Math.PI / 3.0F, (MathHelper.sin(-timeLeft + 1.0F) + 1.0F) / 2.0F);
            }
        }
    }

}