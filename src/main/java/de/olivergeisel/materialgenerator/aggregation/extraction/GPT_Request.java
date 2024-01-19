package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.ElementPrompt;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.PromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;

import java.util.Optional;

/**
 * A request for the {@link GPT_Session}.
 * Contains all necessary information to send a request to the GPT-model.
 * It contains the prompt, the answer, the model name and the location of the model plus configuration for the model.
 *
 * @param <T> The type of the {@link KnowledgeElement} this request is for.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see GPT_Session
 * @see KnowledgeElement
 * @see ElementPrompt
 * @see PromptAnswer
 * @since 1.1.0
 */
public class GPT_Request<T extends KnowledgeElement> {

	private ElementPrompt<T> prompt;
	private PromptAnswer<T>  answer;
	private ModelLocation    modelLocation;
	private String           modelName;
	// configuration for the request
	private Optional<String> apiKey  = Optional.empty();
	private int              retries = 1;
	private int              maxTokens;
	private double           temperature;
	private double           topP;
	private double           frequencyPenalty;

	public GPT_Request(ElementPrompt<T> prompt, PromptAnswer<T> answer, String modelName, ModelLocation location,
			int maxTokens,
			double temperature, double topP, double frequencyPenalty, int retries) {
		this.prompt = prompt;
		this.answer = answer;
		this.modelLocation = location;
		this.modelName = modelName;
		this.maxTokens = maxTokens;
		this.temperature = temperature;
		this.topP = topP;
		this.frequencyPenalty = frequencyPenalty;
		this.retries = retries;
	}

	//region setter/getter
	public ElementPrompt<T> getPrompt() {
		return prompt;
	}

	public void setPrompt(ElementPrompt<T> prompt) {
		this.prompt = prompt;
	}

	public ModelLocation getModelLocation() {
		return modelLocation;
	}

	public void setModelLocation(
			ModelLocation modelLocation) {
		this.modelLocation = modelLocation;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Optional<String> getApiKey() {
		return apiKey;
	}

	public void setApiKey(Optional<String> apiKey) {
		this.apiKey = apiKey;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(int maxTokens) {
		this.maxTokens = maxTokens;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTopP() {
		return topP;
	}

	public void setTopP(double topP) {
		this.topP = topP;
	}

	public double getFrequencyPenalty() {
		return frequencyPenalty;
	}

	public void setFrequencyPenalty(double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public PromptAnswer<T> getAnswer() throws IllegalStateException {
		if (answer == null) {
			throw new IllegalStateException("Answer is not set yet");
		}
		return answer;
	}

	public void setAnswer(PromptAnswer<T> answer) {
		this.answer = answer;
	}
//endregion


	public enum ModelLocation {
		LOCAL,
		REMOTE
	}

}
