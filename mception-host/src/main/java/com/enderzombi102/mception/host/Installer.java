package com.enderzombi102.mception.host;


import com.enderzombi102.mception.version.VersionProvider;

import java.io.IOException;

public interface Installer {
	default void doInstall( VersionProvider provider ) throws IOException {
		doInstall( provider, true );
	}

	void doInstall( VersionProvider provider,  boolean remap ) throws IOException;
	void doCleanup();
	boolean isInstalled();
}
