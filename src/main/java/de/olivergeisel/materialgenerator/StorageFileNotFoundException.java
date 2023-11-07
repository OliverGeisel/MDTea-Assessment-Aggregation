package de.olivergeisel.materialgenerator;

import java.net.MalformedURLException;

public class StorageFileNotFoundException extends RuntimeException {
	public StorageFileNotFoundException(String s, MalformedURLException e) {
		super(s, e);
	}

	public StorageFileNotFoundException(String s) {
		super(s);
	}
}
