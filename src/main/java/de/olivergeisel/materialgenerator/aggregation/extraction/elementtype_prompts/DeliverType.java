package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;


/**
 * The type of how many Elements an answer for a prompt can deliver.
 * <br>
 * SINGLE: The prompt answer is a single value.<br>
 * MULTIPLE: The prompt answer is a list of values.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see PromptAnswer
 * @see ElementExtractor
 * @see ElementPrompt
 * @since 1.1.0
 */
public enum DeliverType {
	SINGLE,
	MULTIPLE
}
