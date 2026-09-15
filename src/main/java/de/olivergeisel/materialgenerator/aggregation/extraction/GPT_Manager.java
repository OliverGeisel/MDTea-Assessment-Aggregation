package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.ai.llm.LlmManager;
import de.olivergeisel.materialgenerator.core.exceptions.ServerNotAvailableException;
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
 *     <li>{@link Item}</li>
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
	private final LlmManager llmManager;

	public GPT_Manager(LlmManager llmManager) {
		this.llmManager = llmManager;
		this.session = new GPT_Session(llmManager.getModel());
	}


	@Deprecated(since = "1.2.0", forRemoval = true)
	public TermPromptAnswer requestTerms(TermPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new TermPromptAnswer(prompt);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}

	public TermPromptAnswer requestTermsOllama(TermPrompt prompt)
			throws ServerNotAvailableException, TimeoutException {
		var res = session.requestOllama(prompt);
		TermPromptAnswer answer = new TermPromptAnswer(prompt);
		answer.setAnswer(res);
		return answer;
	}

	@Deprecated(since = "1.2.0", forRemoval = true)
	public DefinitionPromptAnswer requestDefinitions(DefinitionPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new DefinitionPromptAnswer(prompt);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}


	@Deprecated(since = "1.2.0", forRemoval = true)
	public DefinitionPromptAnswer requestDefinitions(DefinitionPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, ModelParameters parameters) throws ServerNotAvailableException,
			TimeoutException {
		return requestDefinitions(prompt, url, modelName, location, parameters.maxTokens(),
				parameters.temperature(), parameters.topP(), 0.2, parameters.retries());
	}

	public DefinitionPromptAnswer requestDefinitionsOllama(DefinitionPrompt prompt)
			throws ServerNotAvailableException, TimeoutException {
		var res = session.requestOllama(prompt);
		DefinitionPromptAnswer answer = new DefinitionPromptAnswer(prompt);
		answer.setAnswer(res);
		return answer;
	}


	@Deprecated(since = "1.2.0", forRemoval = true)
	public ExamplePromptAnswer requestExamples(ExamplePrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, ModelParameters modelParameters)
			throws ServerNotAvailableException, TimeoutException {
		return requestExamples(prompt, url, modelName, location, modelParameters.maxTokens(),
				modelParameters.temperature(), modelParameters.topP(), 0.2, modelParameters.retries());
	}

	private ExamplePromptAnswer requestExamples(ExamplePrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new ExamplePromptAnswer(prompt, DeliverType.MULTIPLE);
		var newRequest = new GPT_Request<>(prompt, answer, url, modelName, location, maxTokens, temperature, topP,
				frequencyPenalty, retries);
		return session.request(newRequest);
	}

	public ExamplePromptAnswer requestExamplesOllama(ExamplePrompt prompt)
			throws ServerNotAvailableException, TimeoutException {
		var res = session.requestOllama(prompt);
		ExamplePromptAnswer answer = new ExamplePromptAnswer(prompt, DeliverType.MULTIPLE);
		answer.setAnswer(res);
		return answer;
	}

	@Deprecated(since = "1.2.0", forRemoval = true)
	public ItemPromptAnswer requestItems(ItemPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, ModelParameters modelParameters)
			throws ServerNotAvailableException, TimeoutException {
		return requestItems(prompt, url, modelName, location, modelParameters.maxTokens(),
				modelParameters.temperature(),
				modelParameters.topP(), 0.2, modelParameters.retries());
	}

	public ItemPromptAnswer requestItemsOllama(ItemPrompt prompt)
			throws ServerNotAvailableException, TimeoutException {
		var res = session.requestOllama(prompt);
		ItemPromptAnswer answer = new ItemPromptAnswer(prompt);
		answer.setAnswer(res);
		return answer;
	}

	private ItemPromptAnswer requestItems(ItemPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new ItemPromptAnswer(prompt);
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
