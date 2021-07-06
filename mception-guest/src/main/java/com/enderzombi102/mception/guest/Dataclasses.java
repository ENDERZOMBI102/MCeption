package com.enderzombi102.mception.guest;

public class Dataclasses {
	public static class Input {
		// for mouse click
		public Integer clickButton, mouseX, mouseY; // right 1, middle 2, left 3
		// for mouse move event
		public Integer moveMouseX, moveMouseY;
		// for key press/release event
		public Integer keyPressed, keyModifiers;
	}

	public static class Screen {
		public String img;
		public int width, height;
	}

	public static class Message {
		public Input input;
		public Screen screen;
		public boolean needScreen;

		public Message() { }

		public Message(boolean needScreen) {
			this.needScreen = needScreen;
		}

		public Message(Input input) {
			this.input = input;
		}

		public Message(Input input, boolean needScreen) {
			this.needScreen = needScreen;
			this.input = input;
		}
	}
}
