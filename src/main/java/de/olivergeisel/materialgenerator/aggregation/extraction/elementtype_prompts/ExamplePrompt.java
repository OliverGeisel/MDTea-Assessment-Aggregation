package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Example;


/**
 * Prompt for the extraction of {@link Example}s from a Fragment.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Example
 * @see ElementPrompt
 * @since 1.1.0
 */
public class ExamplePrompt extends ElementPrompt<Example> {

	private static final String DEFAULT_FORMAT = "+ [Term] | [Example]\\n";

	private static final String DEFAULT_INSTRUCTIONS = """
			program:
			- List all examples to a Topic/Term in the following query.
			- Terms are subjects in the query.
			- Terms are single words in singular.
			- Examples are a single word (Term) or a sentences. An example can also be a short text.
			- If the Example is a Term then the Term is in singular and you add '|' and the word 'TERM' after it.
			- If the Example is a sentence or a short text then you add '|' and the word 'EXAMPLE' after the text.
			- Follow the pattern:+ [Term] | [Example] | ('TERM' 'EXAMPLE')
			- in example section is an example.
						
			example:
			- language: 'The language of the query is english; the language of the answer is english'
			- query: 'A fish is an animal that lives in the water. A fish use gills to breathe. A salmon is a fish. A shark is a fish too.'
			+ Fish | Salmon | TERM
			+ Fish | Shark | TERM
			""";

	public ExamplePrompt(String fragment) {
		super(DEFAULT_INSTRUCTIONS, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE);
	}

	public ExamplePrompt(String fragment, String instruction, String format, DeliverType deliverType) {
		super(instruction, format, fragment, deliverType);
	}

	public ExamplePrompt(String fragment, String instruction, String format, DeliverType deliverType,
			String targetLanguage, String fragmentLanguage) {
		super(instruction, format, fragment, deliverType, targetLanguage, fragmentLanguage);

	}

	public ExamplePrompt(String fragment, String fragmentLanguage, String targetLanguage) {
		super(DEFAULT_INSTRUCTIONS, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE, fragmentLanguage, targetLanguage);
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
