package com.enderzombi102.mception.host;

import com.enderzombi102.mception.ClassPath;
import com.enderzombi102.mception.error.LibraryNotFoundError;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Utilities {

	/**
	 * Finds a library location on disk by searching a class's location
	 * @param klass A class that is in the requested library
	 * @return Library path
	 */
	public static Path findPath(Class<?> klass ) throws LibraryNotFoundError {
		try {
			return Path.of( klass.getProtectionDomain().getCodeSource().getLocation().toURI() );
		} catch (URISyntaxException e) {
			throw new LibraryNotFoundError( e );
		}
	}

	/**
	 * Generates the classpath of the guest jvm
	 * @param mceptionDir the folder where all mception stuff is stored
	 * @return A {@link List<String>} object
	 * @throws LibraryNotFoundError if any of the required libraries are missing
	 * @throws ClassNotFoundException same as above
	 */
	public static List<String> genClassPath( Path mceptionDir, @Nullable Path guestClasses, @Nullable Path guestResources ) throws LibraryNotFoundError, ClassNotFoundException {
		final var cp = new ClassPath();

		// mception stuff
		if ( guestClasses != null ) {
			cp.addPath( guestClasses );  // mception-guest classes
			assert guestResources != null;
			cp.addPath( guestResources );  // mception-guest resources
		} else {
			cp.addPath( findPath( Utilities.class ) );
		}

		// libraries
		cp.addPath( findPath( Class.forName( "org.apache.logging.log4j.Logger") ) );  // log4j api
		cp.addPath( findPath( Class.forName( "org.apache.logging.log4j.core.Logger" ) ) );  // log4j core
		cp.addPath( findPath( Class.forName( "com.google.gson.Gson" ) ) );  // GSON

		// mc 1.2.5 stuff MUST BE LAST
		cp.addPath( mceptionDir.resolve("bin/client.jar") );
		cp.addPath( mceptionDir.resolve("bin/client.jar") );
		cp.addPath( mceptionDir.resolve("bin/jinput.jar") );
		cp.addPath( mceptionDir.resolve("bin/jutils.jar") );
		cp.addPath( mceptionDir.resolve("bin/lwjgl.jar") );
		cp.addPath( mceptionDir.resolve("bin/lwjgl-util.jar") );
		cp.addPath( mceptionDir.resolve("bin/") );
		cp.addPath( mceptionDir.resolve("resources/") );

		return cp;
	}

	public static Path getJava8Path() {
		// FIXME: This is an hack and should be replaced by localizing the java 8 install
		return Path.of("\"C:\\\\Program Files\\\\Eclipse Adoptium\\\\jdk-8.0.312.7-hotspot\\\\bin\\\\java.exe\"");
	}

	public static ArrayList<String> getCommand(  Path mceptionDir, @Nullable Path guestClasses, @Nullable Path guestResources  ) throws LibraryNotFoundError, ClassNotFoundException {
		final var cmd = new ClassPath();
		cmd.addPath( getJava8Path() );
		cmd.add( "-classpath" );
		cmd.add( "\"" + join( genClassPath( mceptionDir, guestClasses, guestResources), ";" ) + "\"" );
		cmd.add( "com.enderzombi102.mception.guest.Main" );
		return cmd;
	}


	@SuppressWarnings("SameParameterValue")
	public static String join( List<String> strings, String delimiter ) {
		final var builder = new StringBuilder( strings.remove(0) );
		for ( String cppart : strings ) {
			builder.append(delimiter).append(cppart);
		}
		return builder.toString();
	}
}
