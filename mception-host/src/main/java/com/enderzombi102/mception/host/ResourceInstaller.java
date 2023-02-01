package com.enderzombi102.mception.host;

import com.enderzombi102.mception.version.VersionProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class ResourceInstaller implements Installer {

	private static final String RES_INDEX = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";
	private static final String RES_DL_URL = "https://resources.download.minecraft.net/";
	private static final Gson GSON = new GsonBuilder().create();

	private final Path resourcesDir;

	public ResourceInstaller( Path resourcesDir ) {
		this.resourcesDir = resourcesDir;
	}


	public void doInstall( VersionProvider provider, boolean remap ) throws IOException {
		// preliminary values
		JsonObject res_json = GSON.fromJson(
				new InputStreamReader( URI.create( RES_INDEX ).toURL().openStream() ),
				JsonObject.class
		);
		JsonObject resources = res_json.getAsJsonObject("objects");
		assert resources != null;
		// download everything
		resources.entrySet().parallelStream().forEach( resource -> {
			// get hash, filename and size
			JsonObject res = ( (JsonObject) resource.getValue() );
			String filename = resource.getKey();
			String hash = res.get( "hash" ).getAsString();
			int size = res.get("size").getAsInt();
			assert hash != null;
			// download
			try {
				new Download( RES_DL_URL + hash.substring(0, 2) + '/' + hash )
						.process()
						.checkOrRetry(hash, size)
						.saveTo( resourcesDir.resolve( filename ) );
			} catch ( IOException | URISyntaxException e ) {
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
