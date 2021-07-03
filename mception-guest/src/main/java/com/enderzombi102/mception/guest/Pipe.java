package com.enderzombi102.mception.guest;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class Pipe {

	private final MappedBusReader reader;
	private final MappedBusWriter writer;


	public Pipe(Side side) throws IOException {
		reader = new MappedBusReader( getFileLocation( side == Side.Host ? "guestOut" : "guestIn" ), 100000L, 32 );
		writer = new MappedBusWriter( getFileLocation( side == Side.Host ? "guestIn" : "guestOut" ), 100000L, 32 );
		reader.open();
		writer.open();
	}

	public byte[] read() throws IOException {
		byte[] data = new byte[] {};
		reader.readBuffer( data, 0 );
		return data;
	}

	public void send(byte[] bytes) throws EOFException {
		writer.write(bytes, 0, bytes.length);
	}

	private static String getFileLocation(String file) {
		try {
			Object floader = Class.forName("net.fabricmc.loader.api.FabricLoader")
					.getMethod("getInstance")
					.invoke(null);
			Path gameDir = (Path) floader.getClass()
					.getMethod("getGameDir")
					.invoke(floader);
			return gameDir.resolve("MCeption").resolve(file).toString();
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			Path workDir = Path.of( System.getProperty("user.dir") );
			// working directory should always exist
			assert workDir.toFile().exists();
			if ( workDir.toFile().getName().equals("MCeption") ) {
				return workDir.resolve(file).toString();
			} else {
				assert false;
				return null;
			}
		}
	}

	enum Side {
		Host,
		Guest
	}
}
