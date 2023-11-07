package de.olivergeisel.materialgenerator.core.courseplan;

public class CoursePlanParserException extends RuntimeException {
	public CoursePlanParserException(String message, Exception e) {
		super(message, e);
	}

	public CoursePlanParserException(String message) {
		super(message);
	}

	public CoursePlanParserException(Exception e) {
		super(e);
	}

}
