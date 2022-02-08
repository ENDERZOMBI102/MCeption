package com.enderzombi102.mception.version;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public interface VersionProvider {
	@Nullable String getAssetsUrl();

	/**
	 * The client must be the last binary
	 */
	List<BinaryFile> getBinaries();
	String getMainClassName();

	default boolean validateBinaries( Path path ) {
		for ( var binary : this.getBinaries() ) {
			if (! binary.getFilePath(path).toFile().exists() )
				return false;
		}
		return true;
	}

	default BinaryFile getClient() {
		return this.getBinaries().get( this.getBinaries().size() - 1 );
	}
}
