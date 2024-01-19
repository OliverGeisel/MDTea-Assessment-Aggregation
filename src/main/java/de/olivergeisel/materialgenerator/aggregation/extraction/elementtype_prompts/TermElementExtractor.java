package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.model.element.Term;

import java.util.List;

public class TermElementExtractor extends ElementExtractor<Term> {


	@Override
	public Term extract(PromptAnswer<Term> answer) {

		if (answer.getDeliverType() == DeliverType.MULTIPLE) {
			throw new WrongExtractionMethodException(
					"Wrong extraction method for multiple answers. Use extractAll instead.");
		}
		return null;
	}

	@Override
	public List<Term> extractAll(PromptAnswer<Term> answers) {
		if (answers.getDeliverType() == DeliverType.SINGLE) {
			throw new WrongExtractionMethodException("Wrong extraction method for single answer. Use extract instead.");
		}

		return null;
	}


}
