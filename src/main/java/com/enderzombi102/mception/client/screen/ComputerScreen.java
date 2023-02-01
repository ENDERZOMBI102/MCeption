package com.enderzombi102.mception.client.screen;

import com.enderzombi102.mception.error.LibraryNotFoundError;
import com.enderzombi102.mception.guest.ImageUtils;
import com.enderzombi102.mception.host.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.enderzombi102.mception.MCeption.getId;
import static com.enderzombi102.mception.guest.Dataclasses.Input;
import static com.enderzombi102.mception.guest.Dataclasses.Message;
import static com.enderzombi102.mception.MCeption.LOGGER;

public class ComputerScreen extends Screen {

	private static final GuestRunner RUNNER = new GuestRunner();
	private static final NativeImageBackedTexture TEXTURE;
	public static final int WIDTH = 854, HEIGHT = 480;

	private int offsetX, offsetY;

	static {
		TEXTURE = new NativeImageBackedTexture(
				WIDTH,
				HEIGHT,
				false
		);
		MinecraftClient.getInstance().getTextureManager().registerTexture( getId("minecraft_screen"), TEXTURE );

		try {
			//noinspection ConstantConditions
			updateTexture(
					ByteBuffer.wrap(
							ImageUtils.toByteArray(
									ImageIO.read(
											ComputerScreen.class.getResource("/assets/mception/loadingScreen.png")
									)
							)
					)
			);
		} catch (IOException e) {
			// FUCK
			e.printStackTrace();
		}
	}

	public ComputerScreen() {
		super( Text.of("Computer") );
		client = MinecraftClient.getInstance();
		offsetX = ( client.getWindow().getWidth() - WIDTH ) / 2;
		offsetY = ( client.getWindow().getHeight() - HEIGHT ) / 2;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		LOGGER.info("mouseX: " + mouseX + " mouseY: " + mouseY + " button: " + button);
		// left 0, middle 2, right 1
		RUNNER.send( new Message( new Input(button, mouseX, mouseY) ) );
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		RUNNER.send( new Message( new Input(keyCode, modifiers, true) ) );
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		RUNNER.send( new Message( new Input(keyCode, modifiers, false) ) );
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices, 0);
		super.render(matrices, mouseX, mouseY, delta);
		drawTexture(matrices, offsetX, offsetY, 0, 0, 854, 480);
		RUNNER.send( new Message(true) );
		RUNNER.tick();
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		offsetX = ( width - WIDTH ) / 2;
		offsetY = ( height - HEIGHT ) / 2;
	}

	@Override
	protected void init() {
		super.init();

		try {
			LOGGER.info( Utilities.join( GuestRunner.getCommand(), " " ) );
		} catch ( LibraryNotFoundError | ClassNotFoundException ignored ) { }

		if (! RUNNER.running ) {
			RUNNER.run();
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public static void updateTexture(ByteBuffer buf) {
		try {
			TEXTURE.getImage().copyFrom( NativeImage.read( buf ) );
			TEXTURE.upload();
		} catch (IOException ignored) { }
	}
}
