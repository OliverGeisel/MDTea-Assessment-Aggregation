package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;

public class TermPrompt extends ElementPrompt<Term> {

/*
prompts to tests

- Separate each term by new line and every term start with a \"+\".
			- If you know a translation for the term then add it in brackets after the term. Otherwise leave it out.
			- If you find multiple translations, separate them with a \";\".
			- The terms should be in german, the translations in english.





 */

	private static final String DEFAULT_INSTRUCTION = """   
			program:
			- List all terms in the following query.
			- Terms are subjects in the query.
			- Terms are single words in singular.
			- Terms are a single nouns.
			- Terms start with a capital letter.
			- Follow the pattern:+ [Term] | ([Translation])
			- in example section you can see an example.
						
			example:
			- language: 'The language of the query is english; the language of the answer is english'
			- query: 'A fish is an animal that lives in the water. A fish use gills to breathe.'
			+ Fish | Fisch
			+ Animal | Tier
			+ Water | Wasser
			+ Gill | Kieme
			""";

	private static final String DEFAULT_FORMAT = "+ [Term] | ([Translation])\\n";

	public TermPrompt(String fragment) {
		this(fragment, DEFAULT_INSTRUCTION, DEFAULT_FORMAT, DeliverType.MULTIPLE);
	}

	public TermPrompt(String fragment, String queryLanguage, String targetLanguage) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE, queryLanguage, targetLanguage);
	}

	public TermPrompt(String fragment, String instruction, String format, DeliverType deliverType) {
		super(instruction, format, fragment, deliverType);
	}

	//region setter/getter
	@Override
	public String getPrompt() {
		return STR."""
		\{getLanguageString()}

		\{getInstruction()}

		query:
		\{getFragment()}
		""";
	}
//endregion
}
