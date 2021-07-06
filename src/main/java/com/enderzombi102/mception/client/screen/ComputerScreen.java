package com.enderzombi102.mception.client.screen;

import com.enderzombi102.mception.client.BinInstaller;
import com.enderzombi102.mception.client.GuestRunner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.io.EOFException;

import static com.enderzombi102.mception.MCeption.ID;
import static com.enderzombi102.mception.guest.Dataclasses.Input;
import static com.enderzombi102.mception.guest.Dataclasses.Message;
import static com.enderzombi102.mception.MCeption.LOGGER;

public class ComputerScreen extends Screen {

	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final GuestRunner RUNNER = new GuestRunner();
	private static final NativeImageBackedTexture TEXTURE;
	public static final int WIDTH = 854, HEIGHT = 480;

	private int offsetX, offsetY;

	static {
		TEXTURE = new NativeImageBackedTexture(
				WIDTH,
				HEIGHT,
				true
		);
		CLIENT.getTextureManager().registerTexture( ID("minecraft_screen"), TEXTURE );
	}

	public ComputerScreen() {
		super( Text.of("Computer") );
		offsetX = ( CLIENT.getWindow().getWidth() - WIDTH ) / 2;
		offsetY = ( CLIENT.getWindow().getHeight() - HEIGHT ) / 2;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		LOGGER.info("mouseX: " + mouseX);
		LOGGER.info("mouseY: " + mouseY);
		LOGGER.info("button: " + button);
//		Input input = new Input();
//		input.clickButton(button);
//		RUNNER.send( new Message() );
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices, 0);
		super.render(matrices, mouseX, mouseY, delta);
		drawTexture(matrices, offsetX, offsetY, 0, 0, 854, 480);
		RUNNER.send( new Message(true) );
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);

	}

	@Override
	protected void init() {
		super.init();
		if (! RUNNER.running ) {
			RUNNER.run();
			try {
				RUNNER.getPipe().send( BinInstaller.getBinary("lwjgl").getParent().toString() );
				RUNNER.getPipe().send( CLIENT.getSession().getUsername() );
				RUNNER.getPipe().send( CLIENT.getSession().getUuid() );
				RUNNER.send( new Message( true ) );
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
