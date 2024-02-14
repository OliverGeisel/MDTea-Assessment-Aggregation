package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.PromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;

import java.util.LinkedList;
import java.util.List;

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
