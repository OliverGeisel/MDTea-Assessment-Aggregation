package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

/**
 * This class manages the requests to the GPT-Model in the Aggregation-Phase for MDTea.
 * Opens a {@link GPT_Session} and request the wanted {@link KnowledgeElement} type.
 * Every {@link GPT_Request} gives a {@link PromptAnswer} back.
 * Supported types are:
 * <ul>
 *     <li>{@link Term}</li>
 *     <li>{@link Definition}</li>
 *     <li>{@link Example}</li>
 *     <li>{@link Code}</li>
 *     <li>{@link Task}</li>
 * </ul>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see GPT_Session
 * @see GPT_Request
 * @see PromptAnswer
 * @since 1.1.0
 */
@Service
public class GPT_Manager {

	private final GPT_Session session;

	public GPT_Manager() {
		this.session = new GPT_Session();
	}

	public TermPromptAnswer requestTerms(TermPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new TermPromptAnswer(prompt);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}

	public DefinitionPromptAnswer requestDefinitions(DefinitionPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new DefinitionPromptAnswer(prompt);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}

	public DefinitionPromptAnswer requestDefinitions(DefinitionPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, ModelParameters parameters) throws ServerNotAvailableException,
			TimeoutException {
		return requestDefinitions(prompt, url, modelName, location, parameters.maxTokens(),
				parameters.temperature(), parameters.topP(), 0.2, parameters.retries());
	}

	public TaskPromptAnswer requestTasks(TaskPrompt prompt, String url, String s, GPT_Request.ModelLocation location,
			ModelParameters modelParameters) throws ServerNotAvailableException, TimeoutException {
		return requestTasks(prompt, url, s, location, modelParameters.maxTokens(), modelParameters.temperature(),
				modelParameters.topP(), 0.2, modelParameters.retries());
	}

	private TaskPromptAnswer requestTasks(TaskPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location,
			int maxTokens, double temperature, double topP, double frequencyPenalty, int retries)
			throws ServerNotAvailableException, TimeoutException {
		var answer = new TaskPromptAnswer(prompt);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}

	//region setter/getter
	public GPT_Session getSession() {
		return session;
	}
//endregion
}
