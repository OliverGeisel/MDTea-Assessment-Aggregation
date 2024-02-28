package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Task;

public class TaskPrompt extends ElementPrompt<Task> {


	private static final String DEFAULT_INSTRUCTION = """
			program:
			- List all questions in the following query, separated by new line and every question start with a "+".
			- Questions can one of the following types: single choice (SINGLE_CHOICE), multiple choice (MULTIPLE_CHOICE), true or false (TRUE_FALSE), fill out the blank (FILL_BLANK).
			- Follow the pattern:"+ [Type] | [Task] | [Options]".
			- If the question is a single choice, the options are separated by a comma. The first option is the correct one.
			- If the question is a multiple choice, the options are separated by a comma. Every correct options is marked with "(c)" at the end.
			- If the question is a true or false, the options are "true" or "false".
			- If the question is a fill out the blank, every blank is represented by a "<_____>" and the options are separated by a comma.
						
			example:
			- query: 'The capital of Germany is Berlin. Germany is a country in Europe. Some cities in Germany are Munich, Hamburg and Frankfurt.'
			+ SINGLE_CHOICE | What is the capital of Germany? | Berlin, Munich, Hamburg, Frankfurt
			+ TRUE_FALSE | The capital of Germany is Dresden. | false
			+ FILL_BLANK | The capital of Germany is <_____>. | Berlin
			+ MULTIPLE_CHOICE | What is a country in europe? | Germany(c), Canada, China, France(c)
			""";


	private static final String DEFAULT_FORMAT = "+ [Type] | [Task] | [Answers]\\n";

	public TaskPrompt(String fragment) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE);
	}

	protected TaskPrompt(String instruction, String wantedFormat, String fragment, DeliverType type) {
		super(instruction, wantedFormat, fragment, type);
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
				\{getInstruction()}

				query:
				\{getFragment()}
				""";
	}
//endregion
}
