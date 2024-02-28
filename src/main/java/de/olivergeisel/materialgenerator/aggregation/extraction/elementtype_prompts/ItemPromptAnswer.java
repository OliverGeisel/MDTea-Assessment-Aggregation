package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Item;

public class ItemPromptAnswer extends PromptAnswer<Item> {

	public ItemPromptAnswer(ItemPrompt prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public ItemPromptAnswer(ItemPrompt prompt, String answer) {
		super(prompt, answer, DeliverType.MULTIPLE);
	}

	public ItemPromptAnswer(ItemPrompt prompt) {
		super(prompt, DeliverType.MULTIPLE);
	}
}
