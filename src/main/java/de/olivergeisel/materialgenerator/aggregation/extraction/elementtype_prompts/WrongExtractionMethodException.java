package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

/**
 * Exception thrown when an extract-Method from an {@link ElementExtractor} is used but the {@link DeliverType} is
 * not compatible with the method. For example when the {@link DeliverType} is {@link DeliverType#MULTIPLE} but the
 * {@link ElementExtractor#extract}-method is used.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see DeliverType
 * @see ElementExtractor
 * @since 1.1.0
 */
public class WrongExtractionMethodException
		extends RuntimeException {

	public WrongExtractionMethodException(String message) {
		super(message);
	}

	public WrongExtractionMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongExtractionMethodException(Throwable cause) {
		super(cause);
	}

	public WrongExtractionMethodException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
