package com.enderzombi102.mception.host;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class ResourceInstaller implements Installer {

	private static final String RES_INDEX = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";
	private static final String RES_DL_URL = "https://resources.download.minecraft.net/";
	private static final Jankson JANKSON = Jankson.builder().build();

	private final Path resourcesDir;

	public ResourceInstaller( Path resourcesDir ) {
		this.resourcesDir = resourcesDir;
	}


	public void doInstall( boolean remap ) throws IOException {
		// preliminary values
		JsonObject res_json;
		try {
			res_json = JANKSON.load( URI.create( RES_INDEX ).toURL().openStream() );
		} catch (SyntaxError e) {
			throw new IOException( e );
		}
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
						.saveTo( resourcesDir.resolve( filename ) );
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} );
	}

	public void doCleanup() {

	}

	public boolean isInstalled() {
		return resourcesDir.toFile().exists() &&
				resourcesDir.resolve("music").toFile().exists() &&
				resourcesDir.resolve("newmusic").toFile().exists() &&
				resourcesDir.resolve("newsound").toFile().exists() &&
				resourcesDir.resolve("pe").toFile().exists() &&
				resourcesDir.resolve("sound").toFile().exists() &&
				resourcesDir.resolve("sound3").toFile().exists() &&
				resourcesDir.resolve("streaming").toFile().exists();
	}

	public Path getResourcesDir() {
		return resourcesDir;
	}
}
