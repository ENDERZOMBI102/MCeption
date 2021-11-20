package com.enderzombi102.mception.client;

import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

public class Download {

	private final URL downloadUrl;
	private ByteBuffer buf;

	public Download(String download) throws MalformedURLException {
		this( URI.create( download ) );
	}

	public Download(URI download) throws MalformedURLException {
		this.downloadUrl = download.toURL();
	}

	@SuppressWarnings("UnusedReturnValue")
	public Download process() throws IOException {
		this.buf = ByteBuffer.wrap( this.downloadUrl.openConnection().getInputStream().readAllBytes() );
		return this;
	}

	public ByteBuffer getData() {
		return this.buf;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Download saveTo(Path path) throws IOException {
		path.getParent().toFile().mkdirs();
		Files.write( path, this.buf.array() );
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public Download downloadTo(Path path) throws IOException {
		this.process();
		return this.saveTo(path);
	}

	public boolean checkSize(int expectedSize) {
		return this.buf.capacity() == expectedSize;

	}

	public Download checkSizeOrExec(int expectedSize, @NotNull Consumer<String> consumer) {
		// check sizes
		if ( this.checkSize(expectedSize) )
			// not equal, execute callback
			consumer.accept( this.downloadUrl.toString() );
		return this;
	}

	public boolean checkSha1(String expectedSha1) {
		try {
			// get sha1 instance
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			// use sha1 on bytes
			sha1.update(this.buf);
			if (! new String( Hex.encodeHex( sha1.digest() ) ).equals(expectedSha1) )
				return false;
		} catch ( NoSuchAlgorithmException e ) {
			throw new IllegalStateException( "Why is SHA1 missing?", e );
		}
		return true;
	}

	public Download checkSha1OrExec(String expectedSha1, @NotNull Consumer<String> consumer) {
		if (! this.checkSha1(expectedSha1) )
			consumer.accept( this.downloadUrl.toString() );
		return this;
	}

	public Download checkOrRetry(String expectedSha1, int expectedSize) throws URISyntaxException, MalformedURLException {
		if ( checkSha1(expectedSha1) && checkSize(expectedSize) ) return this;
		// check failed, redo download
		return new Download( this.downloadUrl.toURI() );
	}

	public static void downloadTo(String url, Path path) throws IOException {
		new Download(url).downloadTo(path);
	}
}
