package de.olivergeisel.materialgenerator.finalization.export.opal;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Exception for the creation of the OPAL document. Occurs when the document for a new XML-element cannot be created.
 */
public class CreateOPALException extends RuntimeException {

	public CreateOPALException() {
	}

	public CreateOPALException(String message) {
		super(message);
	}

	public CreateOPALException(Throwable cause) {
		super(cause);
	}

	public CreateOPALException(ParserConfigurationException e) {
		super(e);
	}

	public CreateOPALException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateOPALException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CreateOPALException(Throwable cause, String message, Object... args) {
		super(String.format(message, args), cause);
	}
}
