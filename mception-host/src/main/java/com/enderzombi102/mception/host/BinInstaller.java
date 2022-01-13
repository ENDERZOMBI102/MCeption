package com.enderzombi102.mception.host;

import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class BinInstaller implements Installer {
	private static final Logger LOGGER = LogManager.getLogger("BinInstaller");

	private final Path binDir;
	private final Path client;
	private final File clientFile;
	private final boolean clientOnly;

	public BinInstaller( Path binDir, boolean clientOnly ) {
		this.binDir = binDir;
		this.clientOnly = clientOnly;
		this.client = getBinary("client");
		this.clientFile = getBinary("client").toFile();
	}

	public void doInstall( boolean remap ) throws IOException {
		// create main file dir
		Files.createDirectories(binDir);
		if (! clientOnly ) {
			// install jinput
			installIfNotPresent(getJinput(), binDir.resolve("jinput-natives.jar"), true);
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
			installIfNotPresent(getLwjgl(), binDir.resolve("lwjgl-natives.jar"), true);
			installIfNotPresent(
					"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.0/lwjgl-2.9.0.jar",
					getBinary("lwjgl")
			);
			// install lwjgl-util
			installIfNotPresent(
					"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.0/lwjgl_util-2.9.0.jar",
					getBinary("lwjgl-util")
			);
		}
		// install minecraft 1.2.5
		installIfNotPresent(
				"https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar",
				getBinary("client-obf")
		);
		if ( remap )
			remapClient();
//		extractJar();
		// remove meta inf
		Files.deleteIfExists( binDir.resolve("META-INF").resolve("MANIFEST.MF") );
		Files.deleteIfExists( binDir.resolve("META-INF") );
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
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-windows.jar";
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-osx.jar";
		else if ( SystemUtils.IS_OS_LINUX )
			return "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-linux.jar";
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	private void installIfNotPresent( String url, Path path ) throws IOException {
		installIfNotPresent(url, path, false);
	}

	private void installIfNotPresent( String url, Path path, boolean unzip ) throws IOException {
		if (! path.toFile().exists() ) {
			LOGGER.info( path.toFile().getName() + " doesn't exists, downloading it." );
			new Download(url).downloadTo(path);

			if ( unzip )
				UnzipUtility.unzip( path, path.getParent() );
		} else {
			LOGGER.info( path.toFile().getName() + " already exists, skipping." );
		}

	}

	public void doCleanup() {

	}

	public boolean isInstalled() {
		return binDir.toFile().exists() &&
				getBinary("jinput").toFile().exists() &&
				getBinary("jutils").toFile().exists() &&
				getBinary("lwjgl").toFile().exists() &&
				getBinary("lwjgl-util").toFile().exists() &&
				clientFile.exists() &&
				clientFile.length() > getBinary("client-obf").toString().length();
	}

	public Path getBinary( String bin ) {
		return binDir.resolve( bin + ".jar" );
	}

	private void extractJar() throws IOException {
		if ( clientFile.exists() )
			return;
		Files.write(
				client,
				Objects.requireNonNull(
						BinInstaller.class.getResourceAsStream("/client.jar")
				).readAllBytes()
		);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void remapClient() {
		// don't remap if already done
		if ( isInstalled() )
			return;
		if ( clientFile.exists() )
			clientFile.delete();

		LOGGER.info("[MCeption] Remapping client.jar");
		TinyRemapper remapper = TinyRemapper.newRemapper()
				.withMappings( MappingUtils.create( getMappings(), "client", "named" ) )
				.rebuildSourceFilenames(true)
				.build();
		try (
				OutputConsumerPath outConsumer = new OutputConsumerPath
						.Builder( client )
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
		try ( InputStream stream = Objects.requireNonNull( BinInstaller.class.getResourceAsStream("/mappings.tiny") ) ) {
			return MappingUtils.wrapTree(
				TinyMappingFactory.loadWithDetection(
					new BufferedReader(
							new InputStreamReader( stream )
					)
				)
			);
		} catch (IOException e) {
			LOGGER.error( "[MCeption] error while reading stream!", e );
			return TinyMappingFactory.EMPTY_TREE;
		}
	}
}
