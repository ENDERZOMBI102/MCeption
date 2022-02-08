package com.enderzombi102.mception.version;

import com.enderzombi102.mception.host.MappingUtils;
import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;


public final class BinaryFile {
	private final String windowsUrl;
	private final String linuxUrl;
	private final String macosUrl;
	private final String destFile;
	private final String mappings;

	public final boolean needExtraction;
	public final boolean isOsSpecific;

	public BinaryFile( String downloadUrl, String destFile ) {
		this( downloadUrl, downloadUrl, downloadUrl, destFile, false, false, null );
	}

	public BinaryFile( String downloadUrl, String destFile, String mappings ) {
		this( downloadUrl, downloadUrl, downloadUrl, destFile, false, false, mappings );
	}

	public BinaryFile(
			String windowsUrl,
			String linuxUrl,
			String macosUrl,
			String destFile,
			boolean needExtraction,
			boolean isOsSpecific,
			@Nullable String mappings
	) {
		this.windowsUrl = windowsUrl;
		this.linuxUrl = linuxUrl;
		this.macosUrl = macosUrl;
		this.destFile = "bin/" + destFile + ".jar";
		this.needExtraction = needExtraction;
		this.isOsSpecific = isOsSpecific;
		this.mappings = mappings;
	}

	public String getDownloadUrl() {
		if ( SystemUtils.IS_OS_WINDOWS )
			return this.windowsUrl;
		else if ( SystemUtils.IS_OS_MAC_OSX )
			return this.macosUrl;
		else if ( SystemUtils.IS_OS_LINUX )
			return this.linuxUrl;
		throw new IllegalStateException("OS NOT SUPPORTED! how are you running this?");
	}

	public Path getFilePath( Path rootPath ) {
		return rootPath.resolve( this.destFile );
	}

	public @Nullable TinyTree getMappings( Logger logger ) {
		if ( this.mappings != null ) {
			try ( InputStream stream = Objects.requireNonNull( BinaryFile.class.getResourceAsStream( this.mappings ) ) ) {
				return MappingUtils.wrapTree(
						TinyMappingFactory.loadWithDetection(
								new BufferedReader(
										new InputStreamReader(stream)
								)
						)
				);
			} catch (IOException e) {
				logger.error("[MCeption] error while reading stream!", e);
				return TinyMappingFactory.EMPTY_TREE;
			}
		}
		return null;
	}
}
