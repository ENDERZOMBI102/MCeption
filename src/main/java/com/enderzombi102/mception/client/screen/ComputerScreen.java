package com.enderzombi102.mception.client.screen;

import com.enderzombi102.mception.client.BinInstaller;
import com.enderzombi102.mception.client.GuestRunner;
import com.enderzombi102.mception.guest.Dataclasses;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.io.EOFException;

import static com.enderzombi102.mception.guest.Dataclasses.Message;
import static com.enderzombi102.mception.MCeption.LOGGER;

public class ComputerScreen extends HandledScreen<ScreenHandler> {

	private final MinecraftClient client = MinecraftClient.getInstance();
	private static final GuestRunner runner = new GuestRunner();

	public ComputerScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		NativeImageBackedTexture texture = new NativeImageBackedTexture(
				854,
				480,
				true
		);
		
		texture.getImage()
	}

	@Override
	protected void init() {
		super.init();
		if (! runner.running ) {
			runner.run();
			try {
				runner.getPipe().send( BinInstaller.getBinary("lwjgl").getParent().toString() );
				runner.getPipe().send( client.getSession().getUsername() );
				runner.getPipe().send( client.getSession().getUuid() );
			} catch (EOFException e) {
				LOGGER.error( "[GuestRunner] Failed to send essential data!", e );
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
