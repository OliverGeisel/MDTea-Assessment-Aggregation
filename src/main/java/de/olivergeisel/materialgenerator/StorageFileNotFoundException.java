package de.olivergeisel.materialgenerator;

import de.olivergeisel.materialgenerator.generation.StorageService;

import java.net.MalformedURLException;

/**
 * Exception thrown when a file is not found in the storage
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see StorageService
 * @see StorageException
 * @since 0.2.0
 */
public class StorageFileNotFoundException extends RuntimeException {

	public StorageFileNotFoundException(String s, MalformedURLException e) {
		super(s, e);
	}

	public StorageFileNotFoundException(String s) {
		super(s);
	}
}
