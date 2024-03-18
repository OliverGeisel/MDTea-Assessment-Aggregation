package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;

import java.util.List;

/**
 * Represents a Prompt for a specific {@link KnowledgeElement}-Type.
 * <p>
 * Every Prompt has an instruction, a wanted format, a fragment and a deliver type.
 * The instruction is a list of the commands/orders/instruction for the model. Its a list of rules to follow.
 * The wanted format is the format of the answer.
 * The fragment is the text that should be analyzed.
 * The deliver type is the type of the answer. It can be a single answer or multiple answers.
 * Additionally, the fragment and the answers can have a language. This can be important for the model.
 * </p>
 * <h2>Example Prompt:</h2>
 * <pre>
 *     language:
 *     - the language of the fragment is english
 *     - the answers should be in english
 *
 *     program:
 *     - List all definitions for a list of terms in the following query, separated by new line and every definition start with a "+".
 *     - Definitions are the explanation of a term.
 *     - Definitions can be multiple sentences.
 *     - Only the definitions in the query are used.
 *     - No duplications of definitions.
 *     - Ignore a term, if no matching definition is in the query.
 *     - Follow the pattern:+ [Term] | [Definition]
 *
 *     example:
 *     - terms: fish, animal, water, gill
 *     - query: 'A fish is an animal that lives in the water. It has shed, fins and gills. A fish use gills to breathe.'
 *     + fish | A fish is an animal that lives in the water. It has shed, fins and gills.
 *     + animal | An animal is a living organism that feeds on organic matter, typically having specialized sense organs and nervous system and able to respond rapidly to stimuli.
 *     + water | Water is a transparent, tasteless, odorless, and nearly colorless chemical substance, which is the main constituent of Earth's hydrosphere and the fluids of all known living organisms.
 *     + gill | A gill is a respiratory organ found in many aquatic organisms that extracts dissolved oxygen from water and excretes carbon dioxide.
 *
 *     query:
 *     MDTea is a process to extract knowledge from a knowledge source.
 *     The knowledge can be used to create a material for a specific topic.
 *     The material can be used to teach a specific topic.
 *     Together wit a courseplan, the material be structured to a course.
 *     Additionally MDTea can update the material or course.
 *     These functions are the main functions of MDTea. MDTea is splittet in four phases.
 *     The <b>Aggregation</b>, <b>Generation</b>, <b>Finalization</b> and <b>Synchronization</b>.
 * </pre>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see PromptAnswer
 * @since 1.1.0
 */
public abstract class ElementPrompt<T extends KnowledgeElement> {

	/**
	 * Possible symbol for the start of a new element.
	 */
	public static final List<String> START_CHARS              = List.of("+", "-", "*", "#", "~");
	public static final String START_CHARS_STRING_REGEX = "[+\\-#*~]";

	private String      instruction;
	private String      wantedFormat;
	private String      fragment;
	private Language fragmentLanguage = Language.ENGLISH;
	private Language targetLanguage   = Language.ENGLISH;
	private DeliverType deliverType;

	protected ElementPrompt(String instruction, String wantedFormat, String fragment, DeliverType type)
			throws IllegalArgumentException {
		if (instruction == null || wantedFormat == null || fragment == null || type == null) {
			throw new IllegalArgumentException("No argument can be null");
		}
		this.instruction = instruction;
		this.wantedFormat = wantedFormat;
		this.fragment = fragment;
		this.deliverType = type;
	}

	protected ElementPrompt(String instruction, String wantedFormat, String fragment, DeliverType type,
			Language fragmentLanguage, Language targetLanguage)
			throws IllegalArgumentException {
		if (wantedFormat == null || fragment == null || type == null) {
			throw new IllegalArgumentException("No argument can be null");
		}
		this.instruction = instruction;
		this.wantedFormat = wantedFormat;
		this.fragment = fragment;
		this.deliverType = type;
		this.fragmentLanguage = fragmentLanguage;
		this.targetLanguage = targetLanguage;

	}

	protected ElementPrompt(String instruction, String wantedFormat, String fragment, DeliverType type,
			String fragmentLanguage, String targetLanguage)
			throws IllegalArgumentException {
		if (instruction == null || wantedFormat == null || fragment == null || type == null) {
			throw new IllegalArgumentException("No argument can be null");
		}
		this.instruction = instruction;
		this.wantedFormat = wantedFormat;
		this.fragment = fragment;
		this.deliverType = type;
		this.fragmentLanguage = Language.fromString(fragmentLanguage);
		this.targetLanguage = Language.fromString(targetLanguage);
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
	public abstract String getPrompt();


	/**
	 * Every Prompt has a query (the fragment) and the model gives answers. Sometimes it is important to know the
	 * languages of the fragment and answers. So this method returns a string with the languages.
	 * This method <b>should</b> be used in the {@link #getPrompt()} method.
	 *
	 * @return a string with the languages of the fragment and the answers.
	 * <pre>{@snippet example}
	 * return STR.""" <br>
	 * 	language:<br>
	 * 	- the language of the fragment is \{fragmentLanguage.getLanguageText()}<br>
	 * 	- the answers should be in \{targetLanguage.getLanguageText()}<br>
	 * 	""";
	 * 	</pre>
	 */
	public String getLanguageString() {
		//
		return STR."""
		language:
		- the language of the fragment is \{fragmentLanguage.getLanguageText()}
		- the answers should be in \{targetLanguage.getLanguageText()}""";

	}

	public DeliverType getDeliverType() {
		return deliverType;
	}

	protected void setDeliverType(DeliverType deliverType) {
		this.deliverType = deliverType;
	}

	public Language getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(
			Language targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public Language getFragmentLanguage() {
		return fragmentLanguage;
	}

	public void setFragmentLanguage(
			Language fragmentLanguage) {
		this.fragmentLanguage = fragmentLanguage;
	}

	public String getWantedFormat() {
		return wantedFormat;
	}

	protected void setWantedFormat(String wantedFormat) {
		this.wantedFormat = wantedFormat;
	}

	public String getInstruction() {
		return instruction;
	}

	protected void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getFragment() {
		return fragment;
	}

	protected void setFragment(String fragment) {
		this.fragment = fragment;
	}
//endregion


}
