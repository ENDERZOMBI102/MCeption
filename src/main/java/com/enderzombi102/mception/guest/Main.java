package com.enderzombi102.mception.guest;

import static com.enderzombi102.mception.guest.Pipe.Side;

public class Main {

	static Pipe mainPipe;

	public static void main(String[] argv) {
		mainPipe = new Pipe(Side.Guest);
		System.setProperty("org.lwjgl.util.Debug", "true");
		System.setProperty("org.lwjgl.librarypath", "");

	}

}
