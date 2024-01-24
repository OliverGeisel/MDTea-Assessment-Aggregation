package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;

public class TermPromptAnswer extends PromptAnswer<Term> {

	public TermPromptAnswer(TermPrompt prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public TermPromptAnswer(TermPrompt prompt, String answer) {
		super(prompt, answer, DeliverType.MULTIPLE);
	}

	public TermPromptAnswer(TermPrompt prompt) {
		super(prompt, DeliverType.MULTIPLE);
	}
}
