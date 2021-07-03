package com.enderzombi102.mception.guest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.enderzombi102.mception.guest.Pipe.Side;

public class Main {

	static Pipe mainPipe;
	static final Logger LOGGER = LogManager.getLogger("MCeptionMain");

	public static void main(String[] argv) {
		try {
			mainPipe = new Pipe(Side.Guest);
		} catch (IOException e) {
			LOGGER.error("[Main] Failed to open pipe! Aborting...");
			System.exit(1);
		}

		String playerName = "";

		try {
			LOGGER.info("[Main] Trying to get configuration data...");
			System.setProperty("org.lwjgl.util.Debug", "true");
			System.setProperty("org.lwjgl.librarypath", new String( mainPipe.read() ) );
		} catch (IOException e) {
			LOGGER.fatal("[Main] Failed to get configuration data! Aborting...");
			System.exit(1);
		}
	}

}
