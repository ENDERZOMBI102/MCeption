package com.enderzombi102.mception.client.screen;

import com.enderzombi102.mception.client.MinecraftRunner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class ComputerScreen extends HandledScreen<ScreenHandler> {

	private final MinecraftClient client = MinecraftClient.getInstance();

	public ComputerScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void init() {
		super.init();
//		if ( MinecraftRunner.instance != null ) {
//			MinecraftRunner.instance = new MinecraftRunner();
//		}
//		if (! MinecraftRunner.instance.running ) {
//			MinecraftRunner.instance.run();
//		}
//		MinecraftRunner.instance.show();
		var x = new MinecraftRunner();
		x.run();
		x.show();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
