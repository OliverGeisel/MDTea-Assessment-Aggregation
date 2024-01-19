package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;

import java.util.List;

public abstract class ElementExtractor<T extends KnowledgeElement> {

	public abstract T extract(PromptAnswer<T> answer);

	public abstract List<T> extractAll(PromptAnswer<T> answers);

}
