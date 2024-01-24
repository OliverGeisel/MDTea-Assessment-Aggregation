package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

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

	public PromptAnswer<Definition> requestDefinitions(DefinitionPrompt prompt, String url, String modelName,
			GPT_Request.ModelLocation location, int maxTokens, double temperature, double topP, double frequencyPenalty,
			int retries) throws ServerNotAvailableException, TimeoutException {
		var answer = new DefinitionPromptAnswer(prompt);
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
