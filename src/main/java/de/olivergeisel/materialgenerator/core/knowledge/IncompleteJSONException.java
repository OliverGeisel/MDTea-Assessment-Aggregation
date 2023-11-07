package de.olivergeisel.materialgenerator.core.knowledge;

public class IncompleteJSONException extends RuntimeException {

	public IncompleteJSONException(String s) {
		super(s);
	}

	public IncompleteJSONException(String s, Throwable t) {
		super(s, t);
	}

	public IncompleteJSONException(Throwable t) {
		super(t);
	}
}
