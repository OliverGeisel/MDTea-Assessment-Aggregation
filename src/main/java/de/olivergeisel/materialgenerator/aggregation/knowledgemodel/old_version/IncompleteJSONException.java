package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version;

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
