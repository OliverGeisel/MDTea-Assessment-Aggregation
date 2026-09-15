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

		// Try to detect new JSON format first
		try {
			var parsed = net.minidev.json.JSONValue.parseWithException(text);
			if (parsed instanceof java.util.Map) {
				@SuppressWarnings("unchecked")
				var map = (java.util.Map<String, Object>) parsed;
				if (map.containsKey("terms")) {
					@SuppressWarnings("unchecked")
					var terms = (java.util.List<Object>) map.get("terms");
					for (var o : terms) {
						if (o instanceof java.util.Map) {
							@SuppressWarnings("unchecked")
							var termObj = (java.util.Map<String, Object>) o;
							var term = termObj.getOrDefault("term", "").toString();
							back.add(new Term(term, STR."\{term}-TERM", "term"));
						}
					}
					return back;
				}
			}
		} catch (Exception ignored) {
			// not JSON or not the new format -> fall back to old parser
		}

		// Old format: lines like "+ Term | Translation"
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
