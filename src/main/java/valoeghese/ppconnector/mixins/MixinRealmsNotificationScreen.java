package valoeghese.ppconnector.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsNotificationsScreen.class)
public class MixinRealmsNotificationScreen {
	@Inject(at = @At("HEAD"), method = "drawIcons", cancellable = true)
	public void onDrawIcons(PoseStack poseStack, int i, int j, CallbackInfo ci) {
		ci.cancel();
	}
}
