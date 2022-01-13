package com.enderzombi102.mception;

import java.nio.file.Path;
import java.util.ArrayList;

public class ClassPath extends ArrayList<String> {
	public void addPath( Path path ) {
		add( path.toString() );
	}
}
