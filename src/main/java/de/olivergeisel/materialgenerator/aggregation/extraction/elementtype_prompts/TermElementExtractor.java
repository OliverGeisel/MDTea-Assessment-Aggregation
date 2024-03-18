package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;

import java.util.LinkedList;
import java.util.List;

public class TermElementExtractor extends ElementExtractor<Term, TermPromptAnswer> {


	@Override
	public Term extract(TermPromptAnswer answer) {

		if (answer.getDeliverType() == DeliverType.MULTIPLE) {
			throw new WrongExtractionMethodException(
					"Wrong extraction method for multiple answers. Use extractAll instead.");
		}
		return null;
	}

	@Override
	public List<Term> extractAll(TermPromptAnswer answers, GPT_Request.ModelLocation modelLocation)
			throws WrongExtractionMethodException {
		if (answers.getDeliverType() == DeliverType.SINGLE) {
			throw new WrongExtractionMethodException("Wrong extraction method for single answer. Use extract instead.");
		}
		final var format = answers.getPrompt().getWantedFormat();
		String text;
		if (modelLocation == GPT_Request.ModelLocation.LOCAL) {
			text = answers.getAnswer();
		} else {
			text = getChoices(answers).getFirst().get("text").toString();
		}
		List<Term> back = new LinkedList<>();
		// Todo look after format
		final var rawPotentialTermLines = text.split("\\\\n");
		for (var line : rawPotentialTermLines) {
			if (line.strip().isBlank()) continue;
			final var potentialTerm = line.split("\\|");
			String term = potentialTerm[0].replaceAll(ElementPrompt.START_CHARS_STRING_REGEX, "").strip();
			back.add(new Term(term, STR."\{term}-TERM", "term"));
		}
		return back;

	}


}
