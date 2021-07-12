package com.enderzombi102.mception.guest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static com.enderzombi102.mception.guest.Dataclasses.Input;

class InputSimulator {

	public void doInput(Input input) {
		// keyboard handling
		if (input.keyRelease != null) {
			dispatch(
					new KeyEvent(
							MinecraftClient.frame,
							KeyEvent.KEY_RELEASED,
							System.currentTimeMillis(),
							input.keyRModifiers,
							input.keyRelease,
							(char) input.keyRelease.intValue()
					)
			);
		}
		if (input.keyPressed != null) {
			dispatch(
					new KeyEvent(
							MinecraftClient.frame,
							KeyEvent.KEY_PRESSED,
							System.currentTimeMillis(),
							input.keyPModifiers,
							input.keyPressed,
							(char) input.keyPressed.intValue()
					)
			);
		}
		// mouse handling
		if (input.moveMouseX != null || input.moveMouseY != null) {
			dispatch(
					new MouseEvent(
							MinecraftClient.frame,
							MouseEvent.MOUSE_MOVED,
							System.currentTimeMillis(),
							0,
							input.moveMouseX,
							input.moveMouseY,
							0,
							false
					)
			);
		}
		if (input.clickButton != null) {
			dispatch(
					new MouseEvent(
							MinecraftClient.frame,
							MouseEvent.MOUSE_CLICKED,
							System.currentTimeMillis(),
							0,
							input.mouseX.intValue(),
							input.mouseY.intValue(),
							1,
							false,
							input.clickButton + 1
					)
			);
		}
	}

	private void dispatch(AWTEvent evt) {
		MinecraftClient.frame.getInputContext().dispatchEvent( evt );
	}
}
