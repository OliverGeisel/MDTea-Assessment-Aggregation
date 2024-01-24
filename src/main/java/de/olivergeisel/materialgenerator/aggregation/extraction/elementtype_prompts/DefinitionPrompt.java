package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;

public class DefinitionPrompt extends ElementPrompt<Definition> {

	private static final String DEFAULT_INSTRUCTION =
			"List all definitions for a term in the following text, separated by new line and every definition start "
			+ "with a \\\"+\\\":";

	private static final String DEFAULT_FORMAT = "+ [Term] ([Translation])\\n";


	public DefinitionPrompt(String fragment) {
		super(fragment, DEFAULT_INSTRUCTION, DEFAULT_FORMAT, DeliverType.MULTIPLE);
	}


	//region setter/getter
	@Override
	public String getPrompt() {
		return STR."""
		\{getInstruction()}

		query:
		\{getFragment()}
		""";
	}
//endregion
}
