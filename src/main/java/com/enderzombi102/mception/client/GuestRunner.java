package com.enderzombi102.mception.client;

import com.enderzombi102.mception.error.LibraryNotFoundError;
import com.enderzombi102.mception.guest.Dataclasses.Message;
import com.enderzombi102.mception.guest.McPipe;
import com.enderzombi102.mception.host.Utilities;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static com.enderzombi102.mception.MCeption.LOGGER;
import static com.enderzombi102.mception.client.MCeptionClient.BIN_INSTALLER;
import static com.enderzombi102.mception.client.MCeptionClient.MCEPTION_DIR;
import static com.enderzombi102.mception.guest.McPipe.Side;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused", "OptionalGetWithoutIsPresent"})
public class GuestRunner {

	private static final Gson GSON = new Gson();
	private static final Logger GUEST_LOGGER = LogManager.getLogger("MCeptionGuest");
	private Process mcProcess;
	private BufferedReader mcOutput;
	private BufferedReader mcError;
	private McPipe mainPipe;
	public boolean running = false;

	// this successfully runs mc
	// | work dir | command |
	// | MCeption/bin | java -cp client.jar;jinput.jar;jutils.jar;lwjgl.jar;lwjgl-util.jar;../resources net.minecraft.client.Minecraft
	// | MCeption | java -Djava.library.path=bin -cp bin/client.jar;bin/jinput.jar;bin/jutils.jar;bin/lwjgl.jar;bin/lwjgl-util.jar;resources net.minecraft.client.Minecraft
	public void run() {
		try {
			LOGGER.info( "[GuestRunner] Creating pipe!" );
			mainPipe = new McPipe(Side.Host);
		} catch (IOException e) {
			LOGGER.error( "[GuestRunner] Failed to open pipe!", e );
			return;
		}
		try {
			var client = MinecraftClient.getInstance();
			LOGGER.info( "[GuestRunner] Starting process!" );
			// create builder
			var builder = new ProcessBuilder()
					.directory( MCeptionClient.MCEPTION_DIR.toFile() )
					.command( getCommand() )
					.redirectErrorStream(true);
			// set env vars
			builder.environment().putAll(
					new HashMap<>() {{
						put( "lwjgl.dir", BIN_INSTALLER.getBinary("lwjgl").getParent().toString() );
						put( "mc.username", client.getSession().getUsername() );
						put( "mc.uuid", client.getSession().getUuid() );
					}}
			);
			// start process
			mcProcess = builder.start();
			mcProcess.onExit().thenAccept( process -> {
				if ( mcProcess.exitValue() != 0 ) {
					LOGGER.error("[GuestRunner] Process terminated with exit code " + mcProcess.exitValue() );
					running = false;
				}
			} );
			mcOutput = new BufferedReader( new InputStreamReader( mcProcess.getInputStream() ) );
			mcError = new BufferedReader( new InputStreamReader( mcProcess.getErrorStream() ) );
			running = true;
		} catch ( IOException | LibraryNotFoundError | ClassNotFoundException e ) {
			LOGGER.error( "[GuestRunner] Failed to start guest process!", e );
		}
	}

	public void destroy() {
		running = false;
	}

	public void tick() {
		String data;
		try {
			data = mcOutput.readLine();
			if ( data != null )
				GUEST_LOGGER.info( data );
		} catch (IOException ignored) { }
		try {
			data = mcError.readLine();
			if ( data != null )
				GUEST_LOGGER.error( data );
		} catch (IOException ignored) { }
		mainPipe.tick();
	}

	public McPipe getPipe() {
		return mainPipe;
	}

	public Process getProcess() {
		return mcProcess;
	}

	/**
	 * Sends a message to the guest process
	 */
	public void send(Message message) {
		if (! running ) return;
		try {
			mainPipe.send( GSON.toJson(message) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public static List<String> getCommand() throws LibraryNotFoundError, ClassNotFoundException {
		// devenv?
		final boolean devenv = FabricLoader.getInstance().isDevelopmentEnvironment();
		return Utilities.getCommand(
				MCEPTION_DIR,
				devenv ? getClassesPath() : null,
				devenv ? FabricLoader.getInstance().getModContainer("mception").get().getRootPath().toAbsolutePath() : null
		);
	}

	/**
	 * Returns the path of the classes in the jar, counts the dev env
	 */
	private static Path getClassesPath() {
		Path path = FabricLoader.getInstance()
				.getModContainer("mception")
				.get()
				.getRootPath()
				.toAbsolutePath();
		if ( FabricLoader.getInstance().isDevelopmentEnvironment() ) {
			return path.getParent().getParent().resolve("classes").resolve("java").resolve("main");
		} else {
			return path;
		}
	}

}
