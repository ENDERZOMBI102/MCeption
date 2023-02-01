package com.enderzombi102.mception.version;

import java.util.ArrayList;
import java.util.List;

public final class VersionProviders {
	private VersionProviders() {}

	public static final VersionProvider MC_125_PROVIDER = new VersionProvider() {
		private static final List<BinaryFile> BINARIES = new ArrayList<>() {{
			add(
					new BinaryFile(
						"https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar",
						"https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar",
						"https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar",
						"jinput-natives",
						true,
						true,
						null
					)
			);
			add(
					new BinaryFile(
							"https://libraries.minecraft.net/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar",
							"jutils"
					)
			);
			add(
					new BinaryFile(
							"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-windows.jar",
							"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-osx.jar",
							"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-linux.jar",
							"lwjgl-natives",
							true,
							true,
							null
					)
			);
			add(
					new BinaryFile(
							"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.0/lwjgl-2.9.0.jar",
							"lwjgl"
					)
			);
			add(
					new BinaryFile(
							"https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.0/lwjgl_util-2.9.0.jar",
							"lwjgl"
					)
			);
			add(
					new BinaryFile(
							"https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar",
							"client-obf",
							"mappings-125.tiny"
					)
			);
		}};
		private static final String RES_URL = "https://launchermeta.mojang.com/v1/packages/4759bad2824e419da9db32861fcdc3a274336532/pre-1.6.json";

		@Override
		public String getAssetsUrl() {
			return RES_URL;
		}

		@Override
		public List<BinaryFile> getBinaries() {
			return BINARIES;
		}

		@Override
		public String getMainClassName() {
			return "com.enderzombi102.guest.mc125.Main";
		}
	};

	public static final VersionProvider MC_132_PROVIDER = new VersionProvider() {
		private static final List<BinaryFile> BINARIES = new ArrayList<>() {{
			addAll( MC_125_PROVIDER.getBinaries() );  // 1.3.2 uses the same libraries
			remove( size() - 1 );  // remove 1.2.5 client
			add(
					new BinaryFile(
							"https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar",
							"client-obf",
							"mappings-132.tiny"
					)
			);
		}};

		@Override
		public String getAssetsUrl() {
			return MC_125_PROVIDER.getAssetsUrl(); // uses same assets
		}

		@Override
		public List<BinaryFile> getBinaries() {
			return BINARIES;
		}

		@Override
		public String getMainClassName() {
			return "com.enderzombi102.guest.mc125.Main";
		}
	};

}
