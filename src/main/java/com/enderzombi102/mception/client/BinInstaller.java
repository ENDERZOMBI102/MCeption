package com.enderzombi102.mception.client;

import net.fabricmc.loader.util.mappings.TinyRemapperMappingsHelper;
import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.enderzombi102.mception.client.MCeptionClient.MCEPTION_DIR;

public class BinInstaller {

	private static final Path BIN_DIR = MCEPTION_DIR.resolve("bin");

	public static void doInstall() throws IOException {
		// create main file dir
		Files.createDirectories(BIN_DIR);
		// install jinput
		installIfNotPresent( getJinput(), BIN_DIR.resolve("jinput-natives.jar"), true );
		installIfNotPresent(
				"https://libraries.minecraft.net/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar",
				getBinary("jinput")
		);
		// install jutils
		installIfNotPresent(
				"https://libraries.minecraft.net/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar",
				getBinary("jutils")
		);
		// install lwjgl
		installIfNotPresent( getLwjgl(), BIN_DIR.resolve("lwjgl-natives.jar"), true );
		installIfNotPresent(
				"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar",
				getBinary("lwjgl")
		);
		// install lwjgl-util
		installIfNotPresent(
				"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar",
				getBinary("lwjgl-util")
		);
		// install minecraft
		installIfNotPresent(
				"https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar",
				getBinary("client-obf")
		);
		remapClient();
	}

	private static String getJinput() {
		if ( SystemUtils.IS_OS_WINDOWS )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar";
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar";
		else if ( SystemUtils.IS_OS_LINUX )
			return "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar";
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	private static String getLwjgl() {
		if ( SystemUtils.IS_OS_WINDOWS )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar";
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-osxs.jar";
		else if ( SystemUtils.IS_OS_LINUX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-linux.jar";
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	private static void installIfNotPresent(String url, Path path, boolean ...unzip ) throws IOException {
		if (! path.toFile().exists() )
			new Download( url ).downloadTo( path );
		if ( unzip.length > 0 )
			UnzipUtility.unzip( path, path.getParent() );
	}

	public static void doCleanup() {

	}

	public static boolean isInstalled() {
		return BIN_DIR.toFile().exists() &&
				getBinary("jinput").toFile().exists() &&
				getBinary("jutils").toFile().exists() &&
				getBinary("lwjgl").toFile().exists() &&
				getBinary("lwjgl-util").toFile().exists() &&
				getBinary("client").toFile().exists();

	}

	public static Path getBinary(String bin) {
		return BIN_DIR.resolve(bin + ".jar");
	}

	private static void remapClient() {
		TinyRemapper remapper = net.fabricmc.tinyremapper.TinyRemapper.newRemapper()
				.withMappings( TinyRemapperMappingsHelper.create( getMappings(), "official", "named"  ) )
				.rebuildSourceFilenames(true)
				.build();
		try ( OutputConsumerPath outConsumer = new OutputConsumerPath
				.Builder( getBinary("client") )
				.filter( cls -> !cls.startsWith("org/apache/logging/log4j/") )
				.build()
		) {
			// try to remap
			remapper.readInputs( getBinary("client-obf") );
			remapper.apply(outConsumer);

		} catch (IOException e) {
			// an error occurred
			e.printStackTrace();
		} finally {
			// finish the remapping process
			remapper.finish();
		}
	}

	private static TinyTree getMappings() {
		InputStream stream = BinInstaller.class.getResourceAsStream("mappings.tiny");
		assert stream != null;
		try {
			return TinyMappingFactory.loadWithDetection(
					new BufferedReader(
							new InputStreamReader( stream )
					)
			);
		} catch (IOException e) {
			e.printStackTrace();
			return TinyMappingFactory.EMPTY_TREE;
		}
	}
}
