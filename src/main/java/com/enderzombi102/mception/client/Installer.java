package com.enderzombi102.mception.client;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Installer {

	private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
	static final Path MCEPTION_DIR = GAME_DIR.resolve("MCeption");
	private static final Path BIN_DIR = MCEPTION_DIR.resolve("bin");

	public static void doInstall() throws IOException {
		// create main file dir
		Files.createDirectories(BIN_DIR);
		// install jinput
		new Download( getJinput() ).downloadTo( MCEPTION_DIR.resolve("jinput-natives.jar") );
		UnzipUtility.unzip( BIN_DIR.resolve("jinput-natives.jar"), BIN_DIR);
		new Download( "https://libraries.minecraft.net/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar" )
				.downloadTo( BIN_DIR.resolve("jinput.jar") );
		// install jutils
		new Download( "https://libraries.minecraft.net/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar" )
				.downloadTo( BIN_DIR.resolve("jutils.jar") );
		// install lwjgl
		new Download( getLwjgl() ).downloadTo( BIN_DIR.resolve("lwjgl-natives.jar") );
		UnzipUtility.unzip( BIN_DIR.resolve("lwjgl-natives.jar"), BIN_DIR);
		new Download( "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar" )
				.downloadTo( BIN_DIR.resolve("lwjgl.jar") );
		// install lwjgl-util
		new Download( "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar" )
				.downloadTo( BIN_DIR.resolve("lwjgl-util.jar") );
		// install minecraft
		new Download( "https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar" )
				.downloadTo( BIN_DIR.resolve("client.jar") );
		// install resources
		doInstallResources();
	}

	public static String getJinput() {
		if ( SystemUtils.IS_OS_WINDOWS )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar";
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar";
		else if ( SystemUtils.IS_OS_LINUX )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar";
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	public static String getLwjgl() {
		if ( SystemUtils.IS_OS_WINDOWS )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar";
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-osxs.jar";
		else if ( SystemUtils.IS_OS_LINUX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-linux.jar";
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	private static void doInstallResources() {

	}

}
