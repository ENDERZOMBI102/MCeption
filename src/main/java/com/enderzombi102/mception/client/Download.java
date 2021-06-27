package com.enderzombi102.mception.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Download {

	private final URL downloadUrl;
	private ByteBuffer buf;

	public Download(String download) throws MalformedURLException {
		this( URI.create( download ) );
	}

	public Download(URI download) throws MalformedURLException {
		this.downloadUrl = download.toURL();
	}

	public Download process() throws IOException {
		this.buf = ByteBuffer.wrap( this.downloadUrl.openConnection().getInputStream().readAllBytes() );
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T processConvert() throws IOException {
		return ( T ) this.downloadUrl.getContent();
	}

	public ByteBuffer getData() {
		return this.buf;
	}

	public Download saveTo(Path path) throws IOException {
		Files.write( path, this.buf.array() );
		return this;
	}

	public Download downloadTo(Path path) throws IOException {
		this.process();
		return this.saveTo(path);
	}

	public static void downloadTo(String url, Path path) throws IOException {
		new Download(url).downloadTo(path);
	}
}
