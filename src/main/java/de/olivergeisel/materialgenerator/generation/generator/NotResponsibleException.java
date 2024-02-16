package de.olivergeisel.materialgenerator.generation.generator;


import de.olivergeisel.materialgenerator.generation.material.Material;

/**
 * Exception that is thrown when a type of {@link Material} should be generated but the {@link Generator} is not responsible for
 *
 */
public class NotResponsibleException extends RuntimeException {

	public NotResponsibleException() {
		super();
	}

	public NotResponsibleException(String message) {
		super(message);
	}

	public NotResponsibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotResponsibleException(Throwable cause) {
		super(cause);
	}

	protected NotResponsibleException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
