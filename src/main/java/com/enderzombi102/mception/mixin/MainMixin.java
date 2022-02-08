package com.enderzombi102.mception.mixin;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {

	/**
	 * @author ENDERZOMBI102
	 */
	@Inject( method = "<clinit>", at = @At("HEAD"), cancellable = true )
	private static void onStaticInit(CallbackInfo info) {
		info.cancel();
	}

}
