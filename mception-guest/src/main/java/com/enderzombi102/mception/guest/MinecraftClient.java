package com.enderzombi102.mception.guest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;

import java.awt.*;

public abstract class MinecraftClient extends Minecraft {

	public MinecraftClient(Component component, Canvas canvas, MinecraftApplet minecraftApplet, int i, int i1, boolean b) {
		super(component, canvas, minecraftApplet, i, i1, b);
	}

	public static void main(String playerName, String password) {
		a(playerName, password);
	}

	public static void a(String playerName, String password) {
		a(playerName, password, (String)null);
	}

	public static void a(String playerName, String password, String paramString3) {
		boolean bool = false;

		Frame frame = new Frame("Minecraft");
		Canvas canvas = new Canvas();
		frame.setLayout( new BorderLayout() );

		frame.add(canvas, "Center");

		canvas.setPreferredSize(new Dimension(854, 480));
		frame.pack();
		frame.setLocationRelativeTo(null);

		hd hd = new hd(frame, canvas, null, 854, 480, bool, frame);

		Thread thread = new Thread(hd, "Minecraft main thread");
		thread.setPriority(10);
		hd.l = "www.minecraft.net";


		if (playerName != null && password != null) {
			hd.k = new ev(playerName, password);
		} else {
			hd.k = new ev("Player" + (System.currentTimeMillis() % 1000L), "");
		}

		if (paramString3 != null) {
			String[] arrayOfString = paramString3.split(":");
			hd.a(arrayOfString[0], Integer.parseInt(arrayOfString[1]));
		}

		frame.setVisible(true);
		frame.addWindowListener(new gy(hd, thread));

		thread.start();
	}

	@Override
	public void a(im paramim) {
		this.a.removeAll();
		this.a.add(new to(paramim), "Center");
		this.a.validate();
	}
}
