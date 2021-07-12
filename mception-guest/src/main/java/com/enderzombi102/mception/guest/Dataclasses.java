package com.enderzombi102.mception.guest;

public class Dataclasses {
	public static class Input {
		// for mouse click
		public Integer clickButton = null; // right 0, middle 2, left 1
		public Double mouseX = null, mouseY = null;
		// for mouse move event
		public Integer moveMouseX = null, moveMouseY = null;
		// for key press/release event
		public Integer keyPressed = null, keyPModifiers = null, keyRelease = null, keyRModifiers = null;

		public Input(int clickButton, double mouseX, double mouseY) {
			this.clickButton = clickButton;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}

		public Input(int key, int modifier, boolean pressed) {
			if ( pressed ) {
				keyPressed = (int) (char) key;
				keyPModifiers = modifier;
			} else {
				keyRelease = (int) (char) key;
				keyRModifiers = modifier;
			}
		}

		public Input() { }

	}

	public static class Screen {
		public String img = null;

		public Screen() { }
		public Screen(String img) {
			this.img = img;
		}
	}

	public static class Message {
		public Input input = null;
		public Screen screen = null;
		public boolean needScreen = false;
		public String additionalData = null;

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

		public Message(String additionalData) {
			this.additionalData = additionalData;
		}

		public Message(Screen screen) {
			this.screen = screen;
		}
	}
}
