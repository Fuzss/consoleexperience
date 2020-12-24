//package com.fuzs.consoleexperience.mixin;
//
//import net.minecraft.client.renderer.entity.model.AgeableModel;
//import net.minecraft.client.renderer.entity.model.BipedModel;
//import net.minecraft.client.renderer.model.ModelRenderer;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.util.Hand;
//import net.minecraft.util.math.MathHelper;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.Slice;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.function.Consumer;
//
//import static net.minecraft.util.math.MathHelper.lerp;
//
//@Mixin(BipedModel.class)
//public abstract class BipedModelMixin2<T extends LivingEntity> extends AgeableModel<T> {
//
//    @Shadow
//    public ModelRenderer head;
//    @Shadow
//    public ModelRenderer helmet;
//    @Shadow
//    public ModelRenderer torso;
//    @Shadow
//    public ModelRenderer rightArm;
//    @Shadow
//    public ModelRenderer leftArm;
//    @Shadow
//    public ModelRenderer rightLeg;
//    @Shadow
//    public ModelRenderer leftLeg;
//    @Shadow
//    public float swimAnimation;
//
//    @Unique
//    private static float magic1(double rad) {
//
//        return (float) Math.pow(Math.sin(rad) + 1, 2);
//    }
//
//    @Unique
//    private static float magic0(double rad) {
//
//        rad = rad % (Math.PI * 2.0);
//        if (rad <= Math.PI / 2.0) {
//
//            return (float) Math.cos(rad * 2.0);
//        }
//
//        return (float) (-Math.cos((rad - Math.PI / 2.0) * (2.0 / 3.0)));
//    }
//
//    @Shadow
//    protected abstract float lerpAngle(float f, float g, float h);
//
//    // Prevent head pitch change when in swimming pose but not in water
//    @Redirect(
//            require = 1,
//            method = "setAngles",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;lerpAngle(FFF)F",
//                    ordinal = 1
//            )
//    )
//    private float onLerp(BipedModel<T> bipedEntityModel, float f, float g, float h) {
//
//        return h; // don't change pitch
//    }
//
//    @Redirect(
//            require = 1,
//            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
//            slice = @Slice(
//                    from = @At(
//                            value = "INVOKE",
//                            target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;method_29350(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/client/model/ModelRenderer;F)V"
//                    )
//            ),
//            at = @At(
//                    value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;leaningPitch:F"
//            )
//    )
//    private float skipSwimmingRenderingIfNotOnWater(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//
//        // prevent model change when in swimming pose but not in water
//        return entityIn.isActualySwimming() ? this.swimAnimation : 0;
//    }
//
//    @Inject(
//            require = 1,
//            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
//            at = @At(
//                    value = "HEAD"
//            )
//    )
//    void beforeSetAngles(LivingEntity e, float dist, float _0, float _1, float headYawDegrees, float headPitchDegrees, CallbackInfo ci) {
//        head.setPivot(0, 0, 0);
//        head.roll = 0;
//
//        torso.roll = 0;
//        torso.pitch = 0;
//        torso.pivotZ = 0;
//
//        leftLeg.pivotX = 1.9F;
//        rightLeg.pivotX = -1.9F;
//
//        leftArm.setPivot(5, 2, 0);
//        rightArm.setPivot(-5, 2, 0);
//    }
//
//    @Unique
//    private float lerpSwimAnimation(float start, float end) {
//
//        return MathHelper.lerp(this.swimAnimation, start, end);
//    }
//
//    @Unique
//    private float la(float original, float changed) {
//
//        return lerpAngle(this.swimAnimation, original, changed);
//    }
//
//    @Unique
//    private void llPivot(ModelRenderer mp, float x, float y, float z) {
//        mp.setPivot(
//                lerpSwimAnimation(mp.pivotX, x),
//                lerpSwimAnimation(mp.pivotY, y),
//                lerpSwimAnimation(mp.pivotZ, z)
//        );
//    }
//
//    @Unique
//    private void llAngles(ModelRenderer renderer, float roll, float yaw, float pitch) {
//
//        renderer.roll = la(renderer.roll, roll);
//        renderer.yaw = la(renderer.yaw, yaw);
//        renderer.pitch = la(renderer.pitch, pitch);
//    }
//
//    @Inject(
//            require = 1,
//            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
//            at = @At(
//                    value = "RETURN"
//            )
//    )
//    private void afterSetAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitc, CallbackInfo ci) {
//
//        float torsoRollDiv = 6F;
//        float torsoPitchAngle = 0;
//        float torsoYawAngle = (float) Math.sin(limbSwing) / 5F;
//        float torsoHeight = 12F;
//
//        llPivot(
//                leftLeg,
//                1.9F + ((float) Math.sin(limbSwing) / torsoRollDiv) * torsoHeight,
//                12.0F + magic0(limbSwing - (3F / 4F) * Math.PI) * 2F,
//                magic0(limbSwing - Math.PI / 2)
//        );
//        llAngles(
//                leftLeg,
//                -magic1(limbSwing + Math.PI) / 6.F,
//                torsoYawAngle,
//                0
//        );
//
//        llPivot(rightLeg, -1.9F + ((float) Math.sin(limbSwing) / torsoRollDiv) * torsoHeight, 12.0F + magic0(limbSwing + Math.PI / 4F) * 2F, magic0(limbSwing + Math.PI / 2));
//        llAngles(
//                rightLeg,
//                magic1(limbSwing) / 6.F,
//                torsoYawAngle,
//                0
//        );
//
//        float torsoPivotY = torsoHeight - (float) Math.cos(la(torso.pitch, torsoPitchAngle)) * torsoHeight;
//        float torsoPivotZ = (float) -Math.sin(la(torso.pitch, torsoPitchAngle)) * torsoHeight;
//
//        llAngles(torso, (float) -Math.sin(limbSwing) / torsoRollDiv,
//                torsoYawAngle,
//                torsoPitchAngle
//        );
//
//        torso.pivotZ = torsoPivotZ;
//        torso.pivotY = torsoPivotY;
//
//        llAngles(
//                head,
//                -head.yaw,
//                0,
//                (float) (head.pitch - Math.PI / 2.0)
//        );
//
//        head.pivotZ = lerpSwimAnimation(0, torsoPivotZ + (float) Math.cos(limbSwing * 2) / 2.0F);
//        head.pivotY = torsoPivotY;
//
//        helmet.copyPositionAndRotation(head);
//
//        llPivot(
//                leftArm,
//                5,
//                torsoPivotY + 2,
//                torsoPivotZ
//        );
//
//        llPivot(
//                rightArm,
//                -5,
//                torsoPivotY + 2,
//                torsoPivotZ
//        );
//
//        Consumer<Hand> usingArmTransformer = (Hand hand) -> {
//            if (hand == Hand.OFF_HAND)
//                llAngles(
//                        leftArm,
//                        -leftArm.yaw,
//                        0,
//                        (float) (leftArm.pitch - Math.PI / 2.0)
//                );
//            else
//                llAngles(
//                        rightArm,
//                        -rightArm.yaw,
//                        0,
//                        (float) (rightArm.pitch - Math.PI / 2.0)
//                );
//        };
//
//        if (entityIn.isUsingItem()) {
//            usingArmTransformer.accept(Hand.MAIN_HAND);
//            usingArmTransformer.accept(Hand.OFF_HAND);
//
//            return;
//        }
//
//        if (handSwingProgress <= 0 || entityIn.preferredHand != Hand.OFF_HAND) {
//            llAngles(
//                    leftArm,
//                    (float) (-PI / 2.0) + magic0(limbSwing + PI / 2.0),
//                    torso.yaw - (float) (PI / 2.0),
//                    -0.5F
//            );
//        } else usingArmTransformer.accept(Hand.OFF_HAND);
//
//        if (handSwingProgress <= 0 || entityIn.preferredHand != Hand.MAIN_HAND) {
//            llAngles(
//                    rightArm,
//                    (float) (PI / 2.0) + -magic0(limbSwing - PI / 2.0),
//                    torso.yaw + (float) (PI / 2.0),
//                    -0.5F
//            );
//        } else usingArmTransformer.accept(Hand.MAIN_HAND);
//
//    }
//}