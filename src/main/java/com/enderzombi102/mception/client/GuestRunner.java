package com.enderzombi102.mception.client;

import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

import static com.enderzombi102.mception.MCeption.LOGGER;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused", "OptionalGetWithoutIsPresent"})
public class GuestRunner {

	private static final String COMMAND;

	static {
		StringBuilder builder = new StringBuilder();
		// java
		builder.append( System.getProperty("java.home") ).append("/bin/java.exe");
		// classpath
		builder.append(" -cp ").append(
				"bin/client.jar;bin/jinput.jar;bin/jutils.jar;bin/lwjgl.jar;bin/lwjgl-util.jar;bin/;resources/"
		).append( ";\"" ).append(
				getModPath()
		).append("\"");
		if ( FabricLoader.getInstance().isDevelopmentEnvironment() )
			// add chronicle queue
			builder.append(";");
		// main class
		builder.append(" com.enderzombi102.mception.guest.Main");

		COMMAND = builder.toString();
	}

	private final Canvas mcCanvas;
	private Process mcProcess;
	public boolean running = false;


	public GuestRunner() {
		mcCanvas = new Canvas();

	}

	// this successfully runs mc
	// java -cp client.jar;jinput.jar;jutils.jar;lwjgl.jar;lwjgl-util.jar;../resources net.minecraft.client.Minecraft
	public void run() {
		try {
			mcProcess = new ProcessBuilder()
					.directory( MCeptionClient.MCEPTION_DIR.toFile() )
					.command( COMMAND )
					.start();
			running = true;
		} catch (IOException e) {
			LOGGER.error( "[GuestRunner] Failed to start guest process!", e );
		}
	}

	public Canvas getMcCanvas() {
		return mcCanvas;
	}

	public void show() {
		mcCanvas.setVisible(true);
	}

	public void hide() {
		mcCanvas.setVisible(false);
	}

	public void destroy() {
		running = false;
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
}
