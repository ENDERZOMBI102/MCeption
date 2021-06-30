package com.enderzombi102.mception.guest;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import net.openhft.chronicle.core.io.Closeable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class Pipe implements Closeable {

	private MappedBusReader reader;
	private MappedBusWriter writer;
	private boolean closed = false;


	public Pipe(Side side) throws IOException {
		reader = new MappedBusReader( getFileLocation( side == Side.Host ? "guestOut" : "guestIn" ), 100000L, 32 );
		writer = new MappedBusWriter( getFileLocation( side == Side.Host ? "guestIn" : "guestOut" ), 100000L, 32 );
		reader.open();
		writer.open();
	}

	public byte[] read() throws IOException {
		return reader.readMessage();
	}

	public void send(byte[] bytes) {
		writer.open();
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

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	enum Side {
		Host,
		Guest
	}
}
