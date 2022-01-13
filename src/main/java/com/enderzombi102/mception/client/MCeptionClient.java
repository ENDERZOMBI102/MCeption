package com.enderzombi102.mception.client;

import com.enderzombi102.mception.host.BinInstaller;
import com.enderzombi102.mception.host.ResourceInstaller;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Path;

import static com.enderzombi102.mception.MCeption.LOGGER;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class MCeptionClient implements ClientModInitializer {

	static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
	static final Path MCEPTION_DIR = GAME_DIR.resolve("MCeption");
	public static final BinInstaller BIN_INSTALLER = new BinInstaller( MCEPTION_DIR.resolve("bin"), false );
	public static final ResourceInstaller RESOURCE_INSTALLER = new ResourceInstaller( MCEPTION_DIR.resolve("bin") );
	public static boolean installationSucceeded = false;
	public static GuestRunner runner = new GuestRunner();


	@Override
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void onInitializeClient() {
		LOGGER.info("[MCeption] Initializing!");
		LOGGER.info("[MCeption] Checking installation...");
		MCEPTION_DIR.toFile().mkdirs();
		if (! BIN_INSTALLER.isInstalled() ) {
			LOGGER.info("[MCeption] Missing binaries detected!");
			LOGGER.info("[MCeption] Installing minecraft 1.2.5 binaries...");
			try {
				BIN_INSTALLER.doInstall();
				installationSucceeded = true;
				LOGGER.info("[MCeption] Binaries installation finished!");
			} catch (IOException e) {
				LOGGER.fatal("[MCeption] Binaries installation failed! mc 1.2.5 will not work!", e);
				return;
			}
		} else LOGGER.info("[MCeption] Binaries present!");

		if (! RESOURCE_INSTALLER.isInstalled() ) {
			LOGGER.info("[MCeption] No resources detected!");
			LOGGER.info("[MCeption] Installing minecraft 1.2.5 resources...");
			try {
				RESOURCE_INSTALLER.doInstall();
				LOGGER.info("[MCeption] Resources installation finished!");
			} catch ( IOException e ) {
				LOGGER.fatal("[MCeption] Resources installation failed! mc 1.2.5 will not work correctly!", e);
				installationSucceeded = false;
				return;
			}
		} else LOGGER.info("[MCeption] Resources present!");

		LOGGER.info("[MCeption] Loading finished! Let's revive the past!");
	}
}
