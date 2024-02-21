package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

/**
 * Exception to throw if an unsupported type of element is given to a Method or class.
 */
public class UnsupportedElementType extends RuntimeException {
	public UnsupportedElementType() {
	}

	public UnsupportedElementType(String message) {
		super(message);
	}

	public UnsupportedElementType(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedElementType(Throwable cause) {
		super(cause);
	}

	public UnsupportedElementType(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


}
