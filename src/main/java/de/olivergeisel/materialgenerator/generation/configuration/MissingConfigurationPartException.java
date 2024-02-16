package de.olivergeisel.materialgenerator.generation.configuration;


/**
 * Exception to be thrown if a required configuration part is missing.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 1.1.0
 */
public class MissingConfigurationPartException extends RuntimeException {

	public MissingConfigurationPartException() {
		super();
	}

	public MissingConfigurationPartException(String message) {
		super(message);
	}

	public MissingConfigurationPartException(Throwable cause) {
		super(cause);
	}

	public MissingConfigurationPartException(String message, Throwable cause) {
		super(message, cause);
	}

}
