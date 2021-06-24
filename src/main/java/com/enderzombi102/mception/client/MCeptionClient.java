package com.enderzombi102.mception.client;

import com.enderzombi102.mception.MCeption;
import com.enderzombi102.mception.client.screen.ComputerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class MCeptionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenRegistry.register(MCeption.COMPUTER_SCREEN_HANDLER, ComputerScreen::new);
	}
}
