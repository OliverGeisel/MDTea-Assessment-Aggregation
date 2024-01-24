package de.olivergeisel.materialgenerator.aggregation.extraction;


/**
 * Exception thrown when the server is not available.
 */
public class ServerNotAvailableException extends RuntimeException {

	public ServerNotAvailableException(String message) {
		super(message);
	}

	public ServerNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerNotAvailableException(Throwable cause) {
		super(cause);
	}

	public ServerNotAvailableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
