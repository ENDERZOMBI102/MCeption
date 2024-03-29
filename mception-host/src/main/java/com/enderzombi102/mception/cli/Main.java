package com.enderzombi102.mception.cli;

import com.enderzombi102.mception.host.BinInstaller;
import com.enderzombi102.mception.version.VersionProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Console;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class Main {
	private static final Logger LOGGER = LogManager.getLogger("CliMain");
	private static final Path installDir = Path.of( System.getProperty("user.dir") );

	public static void main( String[] argv ) {
		LOGGER.info( "Downloading 1.3.2 client..." );
		var installer = new BinInstaller(
				installDir,
				true
		);
		// do not download already downloaded data
		if (! installDir.resolve( "client-obf.jar" ).toFile().exists() ) {
			try {
				installer.doInstall( VersionProviders.MC_132_PROVIDER, false );
			} catch (Exception e) {
				LOGGER.fatal("Error while downloading client", e);
				System.exit(-1);
			}
		}
		// do not remap if it was already done
		if (! installDir.resolve( "client.jar" ).toFile().exists() ) {
			LOGGER.info( "Remapping client..." );
			try {
				installer.remapClient( VersionProviders.MC_132_PROVIDER.getClient().getMappings( LOGGER ) );
			} catch (Exception e) {
				LOGGER.fatal( "Error while remapping client", e );
				System.exit( -1 );
			}
		}
		LOGGER.info( "Done" );
	}
}
