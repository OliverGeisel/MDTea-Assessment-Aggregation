package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.model.element.Term;

public class TermPrompt extends ElementPrompt<Term> {


	private static final String DEFAULT_INSTRUCTION =
			"List all definitions for a term in the following text, separated by new line and every definition start with a \"+\":";

	private static final String DEFAULT_FORMAT = "*[Term] ([Translation])\\n";

	public TermPrompt(String fragment) {
		this(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE);
	}

	public TermPrompt(String fragment, String instruction, String format, DeliverType deliverType) {
		super(instruction, format, fragment, deliverType);
	}

//region setter/getter
	@Override
	public String getPrompt() {
		return STR."""
			\{getInstruction()}

			\{getFragment()}
			""";
	}
//endregion
}
