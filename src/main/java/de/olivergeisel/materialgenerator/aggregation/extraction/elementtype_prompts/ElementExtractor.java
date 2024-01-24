package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;

import java.util.List;

public abstract class ElementExtractor<T extends KnowledgeElement, A extends PromptAnswer<T>> {

	public abstract T extract(A answer);

	public abstract List<T> extractAll(A answers, GPT_Request.ModelLocation modelLocation);

}
