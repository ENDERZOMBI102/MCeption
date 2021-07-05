package com.enderzombi102.mception.guest;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import net.minecraft.client.crash.CrashInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;

import static com.enderzombi102.mception.guest.Pipe.Side;
import static com.enderzombi102.mception.guest.Dataclasses.Message;

public class Main {

	static Pipe mainPipe;
	static final Logger LOGGER = LogManager.getLogger("MCeptionMain");
	static Thread responseLoop;
	static MinecraftClient client;
	static final Jankson JANKSON = Jankson.builder().allowBareRootObject().build();

	public static void main(String[] argv) {
		try {
			mainPipe = new Pipe(Side.Guest);
		} catch (IOException e) {
			LOGGER.error("[Main] Failed to open pipe! Aborting...");
			System.exit(1);
		}

		String playerName = "Player";
		String uuid = "";

		try {
			LOGGER.info("[Main] Trying to get configuration data...");
			System.setProperty("org.lwjgl.util.Debug", "true");
			System.setProperty("org.lwjgl.librarypath", mainPipe.readString() );
			playerName = mainPipe.readString();
			uuid = mainPipe.readString();
		} catch (IOException e) {
			LOGGER.fatal("[Main] Failed to get configuration data! Aborting...");
			System.exit(1);
		}

		LOGGER.info("[Main] Starting game!");
		client = MinecraftClient.main(playerName, uuid);
		LOGGER.info("[Main] Starting response loop!");
		responseLoop = new Thread( () -> {
			while ( true ) {
				try {
					Message msg = JANKSON.fromJson( mainPipe.readString(), Message.class );
					if ( msg.needScreen ) {
						Message newMsg = new Message();
						newMsg.screen = client.getScreen();
						send( JANKSON.toJson(newMsg).toJson() );
					}
					client.doInput(msg.input);
				} catch (IOException | SyntaxError e) {
					sendError(e);
				}
			}
		} );
	}

	public static void send(String text) {
		try {
			mainPipe.send(text);
		} catch (EOFException e) {
			e.printStackTrace();
		}
	}

	public static void sendError(Throwable e) {
		send( "{ error: \"" + e.toString() + "\" }" );
	}

	public static void sendCrash(CrashInfo crashInfo) {
	}
}
