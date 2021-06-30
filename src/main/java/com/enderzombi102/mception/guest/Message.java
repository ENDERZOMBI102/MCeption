package com.enderzombi102.mception.guest;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class Message implements MappedBusMessage {

	@Override
	public void write(MemoryMappedFile mem, long pos) {
		
	}

	@Override
	public void read(MemoryMappedFile mem, long pos) {

	}

	@Override
	public int type() {
		return 0;
	}
}
