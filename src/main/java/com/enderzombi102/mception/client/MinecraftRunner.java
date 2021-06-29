package com.enderzombi102.mception.client;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static com.enderzombi102.mception.MCeption.LOGGER;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class MinecraftRunner {

	private final Canvas mcCanvas;
	private McClassLoader mcClassLoader;
	private Thread mcThread;
	public boolean running = false;

	public MinecraftRunner() {
		mcCanvas = new Canvas();
		try {
			mcClassLoader = new McClassLoader();
		} catch (MalformedURLException e) {
			LOGGER.fatal("[MCeption] I have no idea why, but java just gave me wrong urls... WTF!", e);
			mcClassLoader = null;
		}
		// setup thread
		assert mcClassLoader != null;
		mcThread = getThread(mcClassLoader);
		assert mcThread != null;
		setProperty( mcThread, "libpath", BinInstaller.getBinary("lwjgl").getParent().toString() );
	}

	// this successfully runs mc
	// java -cp client.jar;jinput.jar;jutils.jar;lwjgl.jar;lwjgl-util.jar;../resources net.minecraft.client.Minecraft
	public void run() {
		setProperty( mcThread, "argv", new String[] {} );
		mcThread.start();
		running = true;
	}

	public Canvas getMcCanvas() {
		return mcCanvas;
	}

	public void show() {
		mcCanvas.setVisible(true);
	}

	public void hide() {
		mcCanvas.setVisible(false);
	}

	public void destroy() {
		running = false;
	}

	private static Thread getThread(McClassLoader loader) {
		try {
			return (Thread) loader
					.loadClass("com.enderzombi102.mception.client.MinecraftThread")
					.getConstructors()[0]
					.newInstance();
		} catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void setProperty(Object object, String name, Object value) {
		try {
			object.getClass()
					.getMethod("setValue", String.class, Object.class)
					.invoke(object, name, value);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static class McClassLoader extends URLClassLoader {
		public McClassLoader() throws MalformedURLException {
			super(
					new URL[] {
							BinInstaller.getBinary("jinput").toUri().toURL(),
							BinInstaller.getBinary("jutils").toUri().toURL(),
							BinInstaller.getBinary("lwjgl").toUri().toURL(),
							BinInstaller.getBinary("lwjgl-util").toUri().toURL(),
							BinInstaller.getBinary("client").toUri().toURL(),
							BinInstaller.getBinary("client").getParent().toUri().toURL(),
							ResourceInstaller.getResources().toUri().toURL(),
							getLocation(McClassLoader.class)
					}
			);
		}

		private static URL getLocation(Class<?> clazz) {
			return clazz.getProtectionDomain().getCodeSource().getLocation();
		}
	}
}
