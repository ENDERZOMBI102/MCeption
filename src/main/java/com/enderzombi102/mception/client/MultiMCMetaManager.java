package com.enderzombi102.mception.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.JsonArray;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MultiMCMetaManager {

	private static final URI MULTI_MC_META = URI.create("https://meta.multimc.org/v1/");
	private static final HashMap<String, JsonObject> dependencyMap = new HashMap<>();
	private static final Jankson JANKSON = new Jankson.Builder().build();

	public static MinecraftManager getMCVersion(String version) {

		return new MinecraftManager(version);
	}

	public static @Nullable List<Download> getDownloads(String dep, String version, boolean useCache) throws MalformedURLException {
		if ( (! useCache ) && dependencyMap.containsKey(dep) ) {
			try {
				dependencyMap.putIfAbsent(
						dep,
						JANKSON.load(
								MULTI_MC_META.resolve(dep).toURL().openStream()
						)
				);
			} catch (IOException | SyntaxError e) {
				e.printStackTrace();
				return null;
			}
		}
		return getDownloads( dependencyMap.get(dep), version );
	}


	public static @Nullable List<Download> getDownloads(JsonObject doc, String version) throws MalformedURLException {
		// TODO: FINISH DEPENDECY DOWNLOAD
		for (JsonElement element : ( JsonArray ) Objects.requireNonNull( doc.get("versions") ) ) {
			if ( ( (JsonObject) element ).get("version").equals(version) ) {

			}
		}
		return URI.create("").toURL();
	}



}
