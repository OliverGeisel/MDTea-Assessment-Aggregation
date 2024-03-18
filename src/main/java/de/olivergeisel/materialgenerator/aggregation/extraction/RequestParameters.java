package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.PromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to store the parameters of a request to the GPT-Model.
 * It is used to store the model location, model name, url, api key and prompt parameters.
 *
 * @param modelLocation The location of the model. It can be either "local" or "remote".
 * @param modelName The name of the model. It is used to identify the model that will be loaded.
 * @param url The url of the server where the model is located. It is only used if the model location is "remote".
 * @param apiKey The api key of the server where the model is located. It is only used if the model location is "remote".
 * @param promptParameters The parameters of the prompt. It is used to generate the prompt for the model.
 *
 * @see PromptParameters
 * @see GPT_Request
 * @see GPT_Session
 *
 * @version 1.1.0
 * @since 1.1.0
 * @author Oliver Geisel
 */
public record RequestParameters(GPT_Request.ModelLocation modelLocation, String modelName, String url, String apiKey,
								PromptParameters promptParameters) {

	public <T extends KnowledgeElement, A extends PromptAnswer<T>> RequestParameters(GPT_Request<T, A> request) {
		this(request.getModelLocation(), request.getModelName(), request.getUrl().orElse(""),
				request.getApiKey().orElse(""), request.getPromptParameters());
	}

	public List<String> toList() {
		var back = new LinkedList<String>();
		back.add(modelLocation.toString().toLowerCase());
		if (!modelName.isBlank()) back.add(modelName);
		if (!url.isBlank()) back.add(url);
		if (!apiKey.isBlank()) back.add(apiKey);
		back.addAll(promptParameters.toList());
		return back;
	}

	@Override
	public String toString() {
		return STR."\{modelLocation.toString()
								   .toLowerCase()} \{modelName} \{url} \{apiKey} \{promptParameters.toString()}";
	}
}
