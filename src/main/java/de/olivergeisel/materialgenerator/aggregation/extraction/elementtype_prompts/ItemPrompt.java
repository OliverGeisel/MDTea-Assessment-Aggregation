package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Item;

/**
 * Prompt to get {@link Item}s from a Fragment.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ElementPrompt
 * @see Item
 * @since 1.1.0
 */
public class ItemPrompt extends ElementPrompt<Item> {


	private static final String DEFAULT_INSTRUCTION = """
			program:
			- List all questions in the following query, separated by new line and every question start with a "+".
			- Questions can one of the following types: single choice (SINGLE_CHOICE), multiple choice (MULTIPLE_CHOICE), true or false (TRUE_FALSE), fill out the blank (FILL_BLANK).
			- Follow the pattern:+ [Type] | [Question] | [Options]
			- If the question is a single choice (Type = SINGLE_CHOICE), the options are separated by a comma. The first option is the correct one, all other options are wrong. There must be at least 2 options.
			- If the question is a multiple choice (Type = MULTIPLE_CHOICE), the options are separated by a comma. Every correct options is marked with '(c)' at the end.
			- If the question is a true or false(Type = TRUE_FALSE), the options are 'true' or 'false'.
			- If the question is a fill out the blank (Type = FILL_OUT_BLANKS), every blank in the text is replaced by '<_____>', in the options are all parts, that are correct for the blank. Every blank has exact one part. The parts are listed in the order they should appear in the text. Every part is separated by a ';'.
			- Consider the language of the query and the language of the answer. The language of the query and the wanted language of the answer are given in the 'language' section. If the question and options in each answer are the same language. The instruction for the answer language is 'the language of the answers should be in [LANGUAGE]'
			- in example section you can see an example with some answers.
						
			example:
			- query: 'The capital of Germany is Berlin. Germany is a country in Europe. Some cities in Germany are Munich, Hamburg and Frankfurt. Germany has 16 federal states and has democracy as its form of government.'
			- language: 'The language of the query is english; the language of the answers should be in english'
			+ SINGLE_CHOICE | What is the capital of Germany? | Berlin; Munich; Hamburg; Frankfurt
			+ TRUE_FALSE | The capital of Germany is Dresden. | false
			+ FILL_OUT_BLANKS | The capital of Germany is <_____>. | Berlin
			+ FILL_OUT_BLANKS | Germany has <_____> federal states and is a <_____>. | 16; democracy
			+ MULTIPLE_CHOICE | What is a country in europe? | Germany(c); Canada; China; France(c)""";


	private static final String DEFAULT_FORMAT = "+ [Type] | [Question] | [Options]\\n";

	public ItemPrompt(String fragment) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE);
	}

	protected ItemPrompt(String instruction, String wantedFormat, String fragment, DeliverType type) {
		super(instruction, wantedFormat, fragment, type);
	}

	public ItemPrompt(String fragment, String fragmentLanguage, String targetLanguage) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE, fragmentLanguage, targetLanguage);
	}

//region setter/getter
	/**
	 * Create the Prompt for the GPT-Model. The Prompt is a string with the instruction, the fragment and the wanted format.
	 * <p>
	 * The instruction is a description of the task for the user. Its a list of rules to follow. For better
	 * results, an example can be added (separated by a new line).
	 * The fragment is the text that should be analyzed.
	 * The wanted format is the format of the answer.
	 * </p>
	 *
	 * @return the Prompt for the GPT-Model.
	 * @see ElementPrompt#getWantedFormat
	 */
	@Override
	public String getPrompt() {
		return STR."""
		\{getLanguageString()}

		\{getInstruction()}

		query:
		\{getFragment()}""";
	}
//endregion
}
