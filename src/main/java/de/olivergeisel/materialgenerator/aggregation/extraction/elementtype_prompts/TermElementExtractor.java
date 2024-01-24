package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			throws RuntimeException {
		if (answers.getDeliverType() == DeliverType.SINGLE) {
			throw new WrongExtractionMethodException("Wrong extraction method for single answer. Use extract instead.");
		}
		final var format = answers.getPrompt().getWantedFormat();
		String text;
		if (modelLocation == GPT_Request.ModelLocation.LOCAL) {
			text = answers.getAnswer();
		} else {
			final var json = answers.getAnswer();
			var parser = new JSONParser(json);
			try {
				var jsonObject = parser.parseObject();
				var choices = (List<Map>) jsonObject.get("choices");
				text = choices.get(0).get("text").toString();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		List<Term> back = new LinkedList<>();
		// Todo look after format
		final var rawPotentialTermLines = text.split("\\\\n");
		for (var line : rawPotentialTermLines) {
			if (line.isBlank()) continue;
			final var potentialTerm = line.split("\\|");
			String term = potentialTerm[0].replace("+", "").strip();
			back.add(new Term(term, STR."\{term}-TERM", "term"));
		}
		return back;

	}
}
