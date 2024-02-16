package de.olivergeisel.materialgenerator;

/**
 * Exception for storage errors
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 0.2.0
 */
public class StorageException extends RuntimeException {

	public StorageException(String s) {
		super(s);

	}

	public StorageException(String s, Throwable cause) {
		super(s, cause);
	}
}
