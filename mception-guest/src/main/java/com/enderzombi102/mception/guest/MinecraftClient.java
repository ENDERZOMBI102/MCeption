package com.enderzombi102.mception.guest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft$class_994;
import net.minecraft.client.Minecraft$class_996;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.client.crash.CrashInfo;
import net.minecraft.client.util.Session;

import java.awt.*;

public class MinecraftClient extends Minecraft {

	public MinecraftClient(Component component, Canvas canvas, MinecraftApplet minecraftApplet, int i, int i1, boolean b) {
		super(component, canvas, minecraftApplet, i, i1, b);
	}

	@Override
	public void method_3285(CrashInfo crashInfo) {
		// on crash

	}

	public static void main(String playerName, String session, String nullByDefault) {
		boolean bool = false;
		String str = playerName;

		Frame frame = new Frame("Minecraft");
		Canvas canvas = new Canvas();
		frame.setLayout( new BorderLayout() );

		frame.add(canvas, "Center");

		canvas.setPreferredSize( new Dimension(854, 480) );
		frame.pack();
		frame.setLocationRelativeTo(null);

		Minecraft$class_996 minecraft$class_996 = new Minecraft$class_996(
				frame,
				canvas,
				null,
				854,
				480,
				bool,
				frame
		);

		Thread thread = new Thread(minecraft$class_996, "Minecraft main thread");
		thread.setPriority(10);
		minecraft$class_996.field_4186 = "www.minecraft.net";


		if (str != null && session != null) {
			minecraft$class_996.session = new Session(str, session);
		} else {
			minecraft$class_996.session = new Session(
					"Player" + (System.currentTimeMillis() % 1000L),
					""
			);
		}

		if (nullByDefault != null) {
			String[] arrayOfString = nullByDefault.split(":");
			minecraft$class_996.method_3297(
					arrayOfString[0],
					Integer.parseInt(arrayOfString[1])
			);
		}

		frame.setVisible(true);
		frame.addWindowListener( new Minecraft$class_994( minecraft$class_996, thread ) );

		thread.start();
	}
}
