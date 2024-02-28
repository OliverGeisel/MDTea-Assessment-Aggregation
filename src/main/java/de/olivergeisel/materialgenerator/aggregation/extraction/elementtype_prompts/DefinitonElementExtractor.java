package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefinitonElementExtractor extends ElementExtractor<Definition, DefinitionPromptAnswer> {

	private Map<String, String> termDefinitionMap = new HashMap<>();


	public String getTermFor(String definitionId) {
		return termDefinitionMap.get(definitionId);
	}

	@Override
	public Definition extract(DefinitionPromptAnswer answer) throws WrongExtractionMethodException {
		if (answer.getDeliverType() == DeliverType.MULTIPLE) {
			throw new WrongExtractionMethodException(
					"Wrong extraction method for multiple answers. Use extractAll instead.");
		}
		return null;
	}

	@Override
	public List<Definition> extractAll(DefinitionPromptAnswer answers, GPT_Request.ModelLocation modelLocation) throws
			WrongExtractionMethodException {
		if (answers.getDeliverType() == DeliverType.SINGLE) {
			throw new WrongExtractionMethodException("Wrong extraction method for single answer. Use extract instead.");
		}

		final var format = answers.getPrompt().getWantedFormat();
		String text;
		List<Definition> back = new LinkedList<>();
		final var rawPotentialDefinitenLines = getPossibleAnswers(answers, modelLocation);
		for (var line : rawPotentialDefinitenLines) {
			if (line.strip().length() < 4) continue;
			final var potentialDefinition = line.split("\\|");
			if (potentialDefinition.length < 2) continue;
			String term = potentialDefinition[0].replace("+", "").strip();
			String definition = potentialDefinition[1].strip();
			back.add(new Definition(definition, STR."\{term}-DEFINITION"));
			termDefinitionMap.put(STR."\{term}-DEFINITION", term);
		}
		// Todo look after format
		return back;
	}
}
