package com.enderzombi102.mception.host;


import java.io.IOException;

public interface Installer {
	default void doInstall() throws IOException {
		doInstall( true );
	}

	void doInstall( boolean remap ) throws IOException;
	void doCleanup();
	boolean isInstalled();
}
