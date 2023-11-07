package de.olivergeisel.materialgenerator.core.courseplan.structure;

public enum Relevance {


	/**
	 * Not part of course but extra knowledge that can help to understand the topic.
	 */
	INFORMATIONAL,
	/**
	 * Part of the topic but not essential part and can be skipped by the user.
	 */
	OPTIONAL,
	/**
	 * Part of the course but if it's missing a warning should appear.
	 */
	IMPORTANT,
	/**
	 * Without these part the course isn't complete and can't be created.
	 */
	MANDATORY,
	/**
	 * State that is not allowed. Only for creation used.
	 */
	TO_SET

}
