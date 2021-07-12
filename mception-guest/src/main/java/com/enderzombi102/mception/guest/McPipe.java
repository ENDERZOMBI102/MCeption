package com.enderzombi102.mception.guest;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.enderzombi102.mception.guest.Main.LOGGER;

@SuppressWarnings("FieldCanBeLocal")
public class McPipe {

	private final ServerSocketChannel server = ServerSocketChannel.open();;
	private final SocketChannel writer = SocketChannel.open();
	private SocketChannel reader = null;
	private final Side side;

	public McPipe(Side side) throws IOException {
		LOGGER.info("[McPipe] Starting " + side + " server pipe!");
		this.side = side;
		server.bind( new InetSocketAddress("127.0.0.1", side == Side.Host ? 20306 : 20305 ) );
		server.configureBlocking(false);
		if ( side == Side.Guest ) {
			LOGGER.info("[McPipe] This is the guest side! connecting to host server...");
			if (! writer.connect( new InetSocketAddress("127.0.0.1", 20306 ) ) )
					writer.finishConnect();
		}
	}

	public void tick() {
		try {
			reader = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if ( reader != null && side == Side.Host ) {
			LOGGER.info("[McPipe] Guest has connected! connecting host to guest server...");
			try {
				writer.connect( new InetSocketAddress("127.0.0.1", 20305 ) );
			} catch (IOException e) {
				// wtf how
				e.printStackTrace();
			}
		}
	}

	public String readString() throws IOException, NoMessage {
		return new String( read() );
	}

	public byte[] read() throws IOException, NoMessage {
		byte[] data = new byte[] {};
		return data;
	}

	public void send(String data) throws EOFException {
		send( data.getBytes() );
	}

	public void send(byte[] bytes) throws EOFException {

	}

	public enum Side {
		Host,
		Guest
	}
}
