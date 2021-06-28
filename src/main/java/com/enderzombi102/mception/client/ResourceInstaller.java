package com.enderzombi102.mception.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class ResourceInstaller {

	private static final String RES_INDEX = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";
	private static final String RES_DL_URL = "https://resources.download.minecraft.net/";
	private static final Path RESOURCES_DIR =  Installer.MCEPTION_DIR.resolve("resources");

	private static final Jankson JANKSON = Jankson.builder().build();

	private static class Resource {
		public String hash;
		public int size;

		String getUrl() {
			return RES_DL_URL + hash.substring(0, 2) + '/' + hash;
		}
	}

	public static void doInstall() throws IOException, SyntaxError {
		JsonObject res_json = JANKSON.load( URI.create( RES_DL_URL ).toURL().openStream() );
		JsonObject resources = res_json.getObject("objects");
		assert resources != null;
		resources.entrySet().parallelStream().forEach( resource -> {
			JsonObject res = ( (JsonObject) resource.getValue() );
			String filename = resource.getKey();
			String hash = res.get(String.class, "hash");
			int size = res.getInt("size", -1);
			Download.downloadTo();
		} );
	}
}
