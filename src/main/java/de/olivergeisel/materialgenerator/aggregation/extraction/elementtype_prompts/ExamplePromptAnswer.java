package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Example;

public class ExamplePromptAnswer extends PromptAnswer<Example> {

	public ExamplePromptAnswer(ElementPrompt<Example> prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public ExamplePromptAnswer(ElementPrompt<Example> prompt, DeliverType deliverType) {
		super(prompt, deliverType);
	}
}
