package com.enderzombi102.mception.host;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class UnzipUtility {

	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param zipFilePath file to unzip
	 * @param destDirectory directory where the unzipped files will be put
	 * @throws IOException yes
	 */
	public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
		unzip( zipFilePath.toString(), destDirectory.toString() );
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param zipFilePath file to unzip
	 * @param destDirectory directory where the unzipped files will be put
	 * @throws IOException yes
	 */
	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (! destDir.exists() ) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream( new FileInputStream(zipFilePath) );
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while ( entry != null ) {
			String filePath = destDirectory + File.separator + entry.getName();
			//noinspection StatementWithEmptyBody
			if ( entry.getName().contains("META-INF") ) {

			} else if (! entry.isDirectory() ) {
				// if the entry is a file, extracts it
				extractFile( zipIn, filePath );
			} else {
				// if the entry is a directory, make the directory
				new File(filePath).mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 * @param zipIn smh
	 * @param filePath msh
	 * @throws IOException yes
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		// setup environment
		Path.of( filePath ).getParent().toFile().mkdirs();
		if (! Path.of( filePath ).toFile().exists() )
			Files.createFile( Path.of( filePath ) );

		// extract file
		BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(filePath) );
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read;
		while ( ( read = zipIn.read(bytesIn) ) != -1 ) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
