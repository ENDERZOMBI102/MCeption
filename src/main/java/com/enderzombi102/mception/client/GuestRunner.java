package com.enderzombi102.mception.client;

import blue.endless.jankson.Jankson;
import com.enderzombi102.mception.guest.Dataclasses;
import com.enderzombi102.mception.guest.Message;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import com.enderzombi102.mception.guest.Pipe;

import static com.enderzombi102.mception.MCeption.LOGGER;
import static com.enderzombi102.mception.guest.Pipe.Side;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused", "OptionalGetWithoutIsPresent"})
public class GuestRunner {

	private static final Jankson JANKSON = new Jankson.Builder().allowBareRootObject().build();
	private Process mcProcess;
	private Pipe mainPipe;
	public boolean running = false;

	// this successfully runs mc
	// java -cp client.jar;jinput.jar;jutils.jar;lwjgl.jar;lwjgl-util.jar;../resources net.minecraft.client.Minecraft
	public void run() {
		try {
			LOGGER.error( "[GuestRunner] Creating pipe!" );
			mainPipe = new Pipe(Side.Host);
		} catch (IOException e) {
			LOGGER.error( "[GuestRunner] Failed to open pipe!", e );
			return;
		}
		try {
			LOGGER.error( "[GuestRunner] Strating process!" );
			mcProcess = new ProcessBuilder()
					.directory( MCeptionClient.MCEPTION_DIR.toFile() )
					.command( getCommand() )
					.start();
			running = true;
		} catch (IOException e) {
			LOGGER.error( "[GuestRunner] Failed to start guest process!", e );
		}
	}

	public void destroy() {
		running = false;
	}

	public Pipe getPipe() {
		return mainPipe;
	}

	private static String getModPath() {
		Path path = FabricLoader.getInstance()
				.getModContainer("mception")
				.get()
				.getRootPath()
				.toAbsolutePath();
		if ( FabricLoader.getInstance().isDevelopmentEnvironment() ) {
			return path.getParent().getParent().resolve("classes").resolve("java").resolve("main").toString();
		} else {
			return path.toString();
		}
	}

	private static ArrayList<String> getCommand() {
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add( System.getProperty("java.home") + "\\bin\\java.exe" );
		cmd.add("-cp");
		cmd.add( getClasspath() );
		cmd.add("com.enderzombi102.mception.guest.Main");
		return cmd;
	}

	@SuppressWarnings("StringConcatenationInLoop")
	private static String getClasspath() {
		ArrayList<String> cp = new ArrayList<>();
		cp.add("bin/client.jar");
		cp.add("bin/jinput.jar");
		cp.add("bin/jutils.jar");
		cp.add("bin/lwjgl.jar");
		cp.add("bin/lwjgl-util.jar");
		cp.add("bin/");
		cp.add("resources/");
		if ( FabricLoader.getInstance().isDevelopmentEnvironment() ) {
			cp.add( BinInstaller.class.getProtectionDomain().getCodeSource().getLocation().toString() );
		} else {
			cp.add( BinInstaller.class.getProtectionDomain().getCodeSource().getLocation().toString() );
		}

		String classpath = "";
		for (String cppart : cp ) {
			classpath += ( cppart + ";" );
		}
		return classpath;
	}

	public void send(Message message) {
		try {
			mainPipe.send( JANKSON.toJson(message).toJson() );
		} catch (EOFException e) {
			e.printStackTrace();
		}
	}
}
