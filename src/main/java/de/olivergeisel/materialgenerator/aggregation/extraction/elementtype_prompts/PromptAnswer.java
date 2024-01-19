package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;

/**
 * This class represents a prompt-answer pair for a specific {@link KnowledgeElement}.
 * It can be that the answer is not set yet, so it is possible to set it later. It should be set by the {@link GPT_Request}
 *
 * @param <T> The type of the {@link KnowledgeElement} this prompt-answer pair is for.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see GPT_Request
 * @see ElementPrompt
 * @see KnowledgeElement
 * @since 1.1.0
 */
public abstract class PromptAnswer<T extends KnowledgeElement> {

	private final String      prompt;
	private final DeliverType deliverType;
	private       String      answer;


	protected PromptAnswer(String prompt, String answer, DeliverType deliverType) {
		this.prompt = prompt;
		this.answer = answer;
		this.deliverType = deliverType;
	}

	protected PromptAnswer(String prompt, DeliverType deliverType) {
		this(prompt, null, deliverType);
	}

	//region setter/getter
	public String getPrompt() {
		return prompt;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public DeliverType getDeliverType() {
		return deliverType;
	}

	public boolean isAnswered() {
		return answer != null;
	}
//endregion
}
