package com.enderzombi102.mception.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static com.enderzombi102.mception.client.MCeptionClient.MCEPTION_DIR;

public class ResourceInstaller {

	private static final String RES_INDEX = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";
	private static final String RES_DL_URL = "https://resources.download.minecraft.net/";
	private static final Path RESOURCES_DIR =  MCEPTION_DIR.resolve("resources");

	private static final Jankson JANKSON = Jankson.builder().build();

	public static void doInstall() throws IOException, SyntaxError {
		// preliminary values
		JsonObject res_json = JANKSON.load( URI.create( RES_INDEX ).toURL().openStream() );
		JsonObject resources = res_json.getObject("objects");
		assert resources != null;
		// download everything
		resources.entrySet().parallelStream().forEach( resource -> {
			// get hash, filename and size
			JsonObject res = ( (JsonObject) resource.getValue() );
			String filename = resource.getKey();
			String hash = res.get(String.class, "hash");
			int size = res.getInt("size", -1);
			assert hash != null;
			// download
			try {
				new Download( RES_DL_URL + hash.substring(0, 2) + '/' + hash )
						.process()
						.checkOrRetry(hash, size)
						.saveTo( RESOURCES_DIR.resolve( filename ) );
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} );
	}

	public static void doCleanup() {

	}

	public static boolean isInstalled() {
		return RESOURCES_DIR.toFile().exists() &&
				RESOURCES_DIR.resolve("music").toFile().exists() &&
				RESOURCES_DIR.resolve("newmusic").toFile().exists() &&
				RESOURCES_DIR.resolve("newsound").toFile().exists() &&
				RESOURCES_DIR.resolve("pe").toFile().exists() &&
				RESOURCES_DIR.resolve("sound").toFile().exists() &&
				RESOURCES_DIR.resolve("sound3").toFile().exists() &&
				RESOURCES_DIR.resolve("streaming").toFile().exists();
	}

	public static Path getResources() {
		return RESOURCES_DIR;
	}
}
