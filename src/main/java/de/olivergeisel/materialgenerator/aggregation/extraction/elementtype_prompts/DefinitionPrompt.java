package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;

import java.util.List;

/**
 * Prompt for the extraction of {@link Definition}s from a Fragment.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Definition
 * @see ElementPrompt
 * @since 1.1.0
 */
public class DefinitionPrompt extends ElementPrompt<Definition> {

	private static final String DEFAULT_INSTRUCTION = """
			program:
			- List all definitions for a list of terms in the following query, separated by new line and every definition start with a "+".
			- Definitions are the explanation of a term.
			- Definitions can be multiple sentences.
			- Only the definitions in the query are used.
			- No duplications of definitions.
			- Skip a term in the answer, if no definition is found in the query.
			- Follow the pattern:+ [Term] | [Definition]
			- in example section you can see an example.

						
			example:
			- terms: fish, animal, water, gill
			- language: 'the language of the query is english; the language of the answer is english'
			- query: 'A fish is an animal that lives in the water. It has shed, fins and gills. A fish use gills to breathe.'
			+ fish | A fish is an animal that lives in the water. It has shed, fins and gills.
			""";

	private static final String DEFAULT_FORMAT = "+ [Term] | [Definition]\\n";

	private List<String> terms;

	public DefinitionPrompt(String fragment, List<String> terms) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE);
		this.terms = terms;
	}

	public DefinitionPrompt(String fragment, List<String> terms, String fragmentLanguage, String targetLanguage) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE, fragmentLanguage, targetLanguage);
		this.terms = terms;
	}

	public DefinitionPrompt(String fragment, String instruction, String format, DeliverType deliverType,
			List<String> terms, String fragmentLanguage, String targetLanguage) {
		super(instruction, format, fragment, deliverType, fragmentLanguage, targetLanguage);
		this.terms = terms;
	}

	//region setter/getter
	public List<String> getTerms() {
		return terms;
	}

	@Override
	public String getPrompt() {
		return STR."""
		\{getLanguageString()}

		\{getInstruction()}

		terms:
		\{getTermsString()}

		query:
		\{getFragment()}
		""";
	}

	/**
	 * Returns a string with all terms separated by a comma.
	 *
	 * @return a string with all terms separated by a comma.
	 */
	private String getTermsString() {
		return String.join(", ", terms);
	}
//endregion
}
