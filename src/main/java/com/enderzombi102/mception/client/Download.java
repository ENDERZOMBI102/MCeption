package com.enderzombi102.mception.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class Download {

	private static final Path downloadDirectory = FabricLoader.getInstance().getGameDir().resolve("mcversions");
	private final URL downloadUrl;
	private final String fileName;
	private ByteBuffer buf;

	public Download(URI download) throws MalformedURLException {
		this.downloadUrl = download.toURL();
		String[] parts = download.getPath().split("/");
		this.fileName = parts[ parts.length - 1 ];
	}

	public void process() throws IOException {
		this.buf = ByteBuffer.wrap( this.downloadUrl.openConnection().getInputStream().readAllBytes() );
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T processConvert() throws IOException {
		return ( T ) this.downloadUrl.getContent();
	}

	public ByteBuffer getData() {
		return this.buf;
	}
}
