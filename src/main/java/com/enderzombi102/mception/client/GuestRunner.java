package com.enderzombi102.mception.client;

import blue.endless.jankson.Jankson;
import com.enderzombi102.mception.guest.Dataclasses.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.enderzombi102.mception.guest.McPipe;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.enderzombi102.mception.MCeption.LOGGER;
import static com.enderzombi102.mception.guest.McPipe.Side;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused", "OptionalGetWithoutIsPresent"})
public class GuestRunner {

	private static final Jankson JANKSON = new Jankson.Builder().allowBareRootObject().build();
	private static final Logger GUEST_LOGGER = LogManager.getLogger("MCeptionGuest");
	private static final HashMap<String, String> JAVA_INSTALLATIONS = new HashMap<>() {{
		put("ENDERZOMBI102", "C:\\Program Files\\Java\\jdk1.8.0_231\\bin\\java.exe");
	}};
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
			// create builder and setup env variables
			var builder = new ProcessBuilder()
					.directory( MCeptionClient.MCEPTION_DIR.toFile() )
					.command( getCommand() )
					.redirectErrorStream(true);
			builder.environment().putAll(
					new HashMap<>() {{
						put( "lwjgl.dir", BinInstaller.getBinary("lwjgl").getParent().toString() );
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
		} catch ( IOException e ) {
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

	public static ArrayList<String> getCommand() {
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add( JAVA_INSTALLATIONS.get( MinecraftClient.getInstance().getSession().getUsername() ) );
		cmd.add( "-classpath" );
		cmd.add( "\"" + getClasspath() + "\"" );
		cmd.add( "com.enderzombi102.mception.guest.Main" );
		return cmd;
	}

	@SuppressWarnings("ConstantConditions")
	private static String getClasspath() {
		ArrayList<String> cp = new ArrayList<>();
		cp.add("bin/client.jar");
		cp.add("bin/jinput.jar");
		cp.add("bin/jutils.jar");
		cp.add("bin/lwjgl.jar");
		cp.add("bin/lwjgl-util.jar");
		cp.add("bin/");
		cp.add("resources/");
		try {
			if ( FabricLoader.getInstance().isDevelopmentEnvironment() ) {
				cp.add(
						getLocation().toString()
				);
				cp.add(
						getLocation().getParent().getParent().getParent().resolve("resources/main").toString()
				);
				cp.add(
						getLocation().getParent().getParent().getParent().getParent()
								.resolve("mception-guest/build/classes/java/main")
								.toString()
				);
				cp.add(
						getLocation().getParent().getParent().getParent().getParent()
								.resolve("mception-guest/build/resources/main")
								.toString()
				);
				// jankson
				cp.add(
						Path.of( Jankson.class.getProtectionDomain().getCodeSource().getLocation().toURI() ).toString()
				);
				// logging
				cp.add(
						Path.of( Logger.class.getProtectionDomain().getCodeSource().getLocation().toURI() ).toString()
				);
				cp.add(
						Path.of(
								org.apache.logging.log4j.core.Logger.class
										.getProtectionDomain()
										.getCodeSource()
										.getLocation()
										.toURI()
						).toString()
				);
			} else {
				cp.add(
						Path.of(
								BinInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI()
						).toString()
				);
			}
		} catch (URISyntaxException e) {
			LOGGER.error(e);
		}
		return join(cp, ";");
	}

	public void send(Message message) {
		if (! running ) return;
		try {
			mainPipe.send( JANKSON.toJson(message).toJson() );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("StringConcatenationInLoop")
	public static String join(ArrayList<String> strings, String delimiter) {
		String finalString = strings.get(0);
		strings.remove(0);
		for (String cppart : strings ) {
			finalString += ( delimiter + cppart );
		}
		return finalString;
	}

	private static Path getLocation() {
		try {
			return Path.of(
					GuestRunner.class
							.getProtectionDomain()
							.getCodeSource()
							.getLocation()
							.toURI()
			);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	private static Path getLocation(Class<?> clazz) {
		try {
			return Path.of(
					clazz
							.getProtectionDomain()
							.getCodeSource()
							.getLocation()
							.toURI()
			);
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
