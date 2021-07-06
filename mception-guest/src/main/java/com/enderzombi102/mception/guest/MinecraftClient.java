package com.enderzombi102.mception.guest;

import net.minecraft.client.*;
import net.minecraft.client.crash.CrashInfo;
import net.minecraft.client.util.Session;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

import static java.util.Base64.Encoder;

import static com.enderzombi102.mception.guest.Main.sendError;
import static com.enderzombi102.mception.guest.Dataclasses.Input;
import static com.enderzombi102.mception.guest.Dataclasses.Screen;


public class MinecraftClient extends Minecraft {

	private static Thread mcMainThread;
	public static MinecraftClient mcClient;
	public static Frame frame;
	private static Canvas canvas;
	private static final Encoder B64_ENCODER = Base64.getEncoder();
	private final InputSimulator simulator = new InputSimulator();

	public MinecraftClient(
			Component component,
			Canvas canvas,
			int width,
			int height,
			boolean fullscreen
	) {
		super(component, canvas, null, width, height, fullscreen);
	}

	@Override
	public void method_3285(CrashInfo crashInfo) {
		// on crash
		Main.sendCrash(crashInfo);
	}

	public static MinecraftClient main(String playerName, String uuid) {

		frame = new Frame("Minecraft");
		canvas = new Canvas();
		frame.setLayout( new BorderLayout() );

		frame.add(canvas, "Center");

		canvas.setPreferredSize( new Dimension(854, 480) );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		mcClient = new MinecraftClient(
				frame,
				canvas,
				854,
				480,
				false
		);

		mcMainThread = new Thread(mcClient, "Minecraft main thread");
		mcMainThread.setPriority(10);
		mcClient.field_4186 = "www.minecraft.net";


		if (playerName != null && uuid != null) {
			mcClient.session = new Session(playerName, uuid);
		} else {
			mcClient.session = new Session(
					"Player" + (System.currentTimeMillis() % 1000L),
					""
			);
		}

		frame.setVisible(true);
		frame.addWindowListener( new Minecraft$class_994( mcClient, mcMainThread ) );

		mcMainThread.start();

		return mcClient;
	}

	public Screen getScreen() {
		BufferedImage img = new BufferedImage(
				canvas.getWidth(),
				canvas.getHeight(),
				BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g2d = img.createGraphics();
		canvas.printAll(g2d);
		g2d.dispose();
		Screen screen =  new Screen();
		screen.img = B64_ENCODER.encodeToString( ImageUtils.toByteArray(img) );
		screen.width = canvas.getWidth();
		screen.height = canvas.getHeight();
		return screen;
	}

	public void doInput(Input input) {
		simulator.doInput(input);
	}

	public static void stopMinecraft() {
		if (mcMainThread == null)
			return;
		mcClient.scheduleStop();
		try {
			mcMainThread.join(10000L);
		} catch (InterruptedException interruptedException) {
			try {
				mcClient.stop();
			} catch (Exception e) {
				sendError(e);
			}
		}
		mcMainThread = null;
	}
}
