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
		List<Definition> back = new LinkedList<>();
		// Try JSON new format first
		var possibleAnswers = getPossibleAnswers(answers, modelLocation);
		if (possibleAnswers.length == 1) {
			try {
				var parsed = net.minidev.json.JSONValue.parseWithException(possibleAnswers[0]);
				if (parsed instanceof java.util.Map) {
					@SuppressWarnings("unchecked")
					var map = (java.util.Map<String, Object>) parsed;
					if (map.containsKey("definitions")) {
						var defs = (java.util.List<Object>) map.get("definitions");
						for (var o : defs) {
							if (o instanceof java.util.Map) {
								@SuppressWarnings("unchecked")
								var d = (java.util.Map<String, Object>) o;
								var term = d.getOrDefault("term", "").toString();
								var definition = d.getOrDefault("definition", "").toString();
								back.add(new Definition(definition, STR."\{term}-DEFINITION"));
								termDefinitionMap.put(STR."\{term}-DEFINITION", term);
							}
						}
						return back;
					}
				}
			} catch (Exception ignored) {
			}
		}

		// Fallback to old format
		final var rawPotentialDefinitenLines = possibleAnswers;
		for (var line : rawPotentialDefinitenLines) {
			if (line.strip().length() < 4) continue;
			final var potentialDefinition = line.split("\\|");
			if (potentialDefinition.length < 2) continue;
			String term = potentialDefinition[0].replaceAll(ElementPrompt.START_CHARS_STRING_REGEX, "").strip();
			String definition = potentialDefinition[1].strip();
			back.add(new Definition(definition, STR."\{term}-DEFINITION"));
			termDefinitionMap.put(STR."\{term}-DEFINITION", term);
		}
		return back;
	}
}
