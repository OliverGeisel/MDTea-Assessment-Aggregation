package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.model.element.Term;

public class TermPromptAnswer extends PromptAnswer<Term> {

	public TermPromptAnswer(String prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public TermPromptAnswer(String prompt, String answer) {
		super(prompt, answer, DeliverType.MULTIPLE);
	}
}
