package com.enderzombi102.mception.guest;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Message implements MappedBusMessage {

	private byte[] data;

	public Message(byte[] data) {
		this.data = data;
	}

	public Message() { }

	@Override
	public void write(MemoryMappedFile mem, long pos) {
		mem.setBytes( pos, this.data, 0, this.data.length );
	}

	@Override
	public void read(MemoryMappedFile mem, long pos) {
		mem.getBytes( pos, this.data, 0, this.data.length );
	}

	@Override
	public int type() {
		return 0;
	}
}
