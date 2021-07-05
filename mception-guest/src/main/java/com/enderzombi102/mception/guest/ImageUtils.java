package com.enderzombi102.mception.guest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

	// convert BufferedImage to byte[]
	public static byte[] toByteArray(BufferedImage img) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "png", stream);
		} catch (IOException e) {
			return null;
		}
		return stream.toByteArray();

	}

	// convert byte[] to BufferedImage
	public static BufferedImage toBufferedImage(byte[] bytes) {
		InputStream stream = new ByteArrayInputStream(bytes);
		try {
			return ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
