package com.enderzombi102.mception.client;

import java.lang.reflect.InvocationTargetException;

public class MinecraftThread extends Thread implements McRunnable {

	private String libpath;
	private String[] argv = new String[] { };

	public MinecraftThread() {
		super("GuestMinecraftThread");
		setContextClassLoader( getClass().getClassLoader() );
		// it SHOULD be the McClassLoader
		assert getClass().getClassLoader().getClass().getSimpleName().equals("McClassLoader");
	}

	@Override
	public void run() {
		try {
			System.setProperty( "org.lwjgl.librarypath", libpath);
			Class.forName("net.minecraft.client.Minecraft")
					.getMethod("main", String[].class)
					.invoke(null, new Object[] { argv } );
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(String name, Object value) {
		if ( name.equals("libpath") ) libpath = (String) value;
		else if ( name.equals("argv") ) argv = (String[]) value;
	}
}
