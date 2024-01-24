package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;

public class DefinitionPromptAnswer extends PromptAnswer<Definition> {

	public DefinitionPromptAnswer(DefinitionPrompt prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public DefinitionPromptAnswer(DefinitionPrompt prompt, String answer) {
		super(prompt, answer, DeliverType.MULTIPLE);
	}

	public DefinitionPromptAnswer(DefinitionPrompt prompt) {
		super(prompt, DeliverType.MULTIPLE);
	}
}
