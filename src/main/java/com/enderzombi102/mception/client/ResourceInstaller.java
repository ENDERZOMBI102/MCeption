package com.enderzombi102.mception.client;

import com.google.gson.Gson;

public class ResourceInstaller {

	private static final String RES_INDEX = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";
	private static final String RES_DL_URL = "https://resources.download.minecraft.net/";

	private static final Gson GSON = new Gson().newBuilder()
			.registerTypeAdapter(Resource.class, new ResourceTypeAdapter() ).create();
	// TODO: add type adapters so GSON does the hard work for me

	private static class Resource {
		public String filename;
		public String hash;
		public int size;

		String getUrl() {
			return RES_DL_URL + hash.substring(0, 2) + '/' + hash;
		}
	}

	private static class ResourceTypeAdapter {
	}
}
