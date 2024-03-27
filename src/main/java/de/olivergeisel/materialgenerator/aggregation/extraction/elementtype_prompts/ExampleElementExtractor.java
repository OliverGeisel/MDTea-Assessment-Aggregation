package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Example;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.BasicRelation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The ExampleElementExtractor is used to extract {@link Example} elements from {@link ExamplePromptAnswer}s.
 * <p>
 * This is something special. It can create two different types as a result. First it creates {@link Example} object.
 * This is is a text that is an example for a term. The example is linked to the term. The term is not created by this
 * process. It is assumed that the term is already existing.
 * Second it creates {@link Term} objects and {@link Relation} objects if there is somthing like "A is an example for
 * B". Then the extractor creates a relation between A and B and the other way around. If the term is not already
 * existing, the extractor creates a new term.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Example
 * @see ExamplePromptAnswer
 * @see Term
 * @see Relation
 * @since 1.1.0
 */
public class ExampleElementExtractor extends ElementExtractor<Example, ExamplePromptAnswer> {

	private Map<Example, String> termExampleMap = new HashMap<>();


	private List<Term>     terms;
	private List<Term>     newTerms  = new LinkedList<>();
	private List<Relation> relations = new LinkedList<>();

	public ExampleElementExtractor(List<Term> terms) {
		this.terms = terms;
	}

	/**
	 * Returns the term for a given example.
	 *
	 * @param example The example that is linked to the term you want.
	 * @return The term that is linked to the example.
	 */
	public String getTermFor(Example example) {
		for (var entry : termExampleMap.entrySet()) {
			if (entry.getKey().equals(example)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Example extract(ExamplePromptAnswer answer) throws WrongExtractionMethodException {
		if (answer == null)
			throw new IllegalArgumentException("Answer is null");
		if (answer.getDeliverType().equals(DeliverType.MULTIPLE)) {
			throw new WrongExtractionMethodException("Answer is of type MULTIPLE");
		}
		return null;
	}

	@Override
	public List<Example> extractAll(ExamplePromptAnswer answers, GPT_Request.ModelLocation modelLocation)
			throws WrongExtractionMethodException {
		if (answers == null)
			throw new IllegalArgumentException("Answer is null");
		if (answers.getDeliverType().equals(DeliverType.SINGLE)) {
			throw new WrongExtractionMethodException("Answer is of type SINGLE");
		}
		String text;
		if (modelLocation == GPT_Request.ModelLocation.LOCAL) {
			text = answers.getAnswer();
		} else {
			text = getChoices(answers).getFirst().get("text").toString();
		}
		List<Example> back = new LinkedList<>();
		final var rawPotentialExampleLines = text.split("\\\\n");
		for (var line : rawPotentialExampleLines) {
			if (line.strip().isBlank()) continue;
			final var potentialExample = line.split("\\|");
			if (potentialExample.length != 3) continue;
			String term = potentialExample[0].replaceAll(ElementPrompt.START_CHARS_STRING_REGEX, "").strip();
			var exampleContent = potentialExample[1].strip();
			var type = potentialExample[2].strip();
			if (type.equals("EXAMPLE")) {
				var id = STR."\{term}-EXAMPLE";
				int i = 0;
				var check = true;
				while (check) {
					var tempId = id;
					var result = termExampleMap.keySet().stream().anyMatch(e -> e.getId().equals(tempId));
					if (!result) break;
					id = STR."\{term}-\{++i}-EXAMPLE";
					if (i > 100) {
						check = false;
					}
				}
				termExampleMap.put(new Example(exampleContent, id, "example"), term);
				var example = new Example(exampleContent, id, "example");

				back.add(example);
			} else if (type.equals("TERM")) {
				var fromTerm = terms.stream().filter(e -> e.getContent().equals(term)).findFirst();
				AtomicReference<Term> from = new AtomicReference<>();
				fromTerm.ifPresentOrElse(from::set, () -> {
					var newTerm = new Term(term, STR."\{term}-TERM", "term");
					newTerms.add(newTerm);
					from.set(newTerm);
				});
				AtomicReference<Term> to = new AtomicReference<>();
				var toTerm = terms.stream().filter(e -> e.getContent().equals(exampleContent)).findFirst();
				toTerm.ifPresentOrElse(to::set, () -> {
					var newTerm = new Term(exampleContent, STR."\{exampleContent}-TERM", "term");
					newTerms.add(newTerm);
					to.set(newTerm);
				});
				relations.add(new BasicRelation(RelationType.EXAMPLE_FOR, from.get(), to.get()));
				relations.add(new BasicRelation(RelationType.HAS_EXAMPLE, to.get(), from.get()));
			}
		}
		return back;
	}

	//region setter/getter
	public List<Term> getNewTerms() {
		return newTerms;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public Map<Example, String> getTermExampleMap() {
		return termExampleMap;
	}
//endregion
}
