package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.extraction.ElementNegotiator;
import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.extraction.ModelParameters;
import de.olivergeisel.materialgenerator.aggregation.extraction.Negotiator;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.BasicRelation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Session-Object for the aggregation process.
 * ONLY for the aggregation process and usage in Controllers.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 1.1.0
 */
public class AggregationProcess {

	private LocalDateTime                 start           = LocalDateTime.now();
	private List<MultipartFile>           sources;
	private GPT_Request.ModelLocation     modelLocation   = GPT_Request.ModelLocation.REMOTE;
	private String                        currentFragment;
	private String                        areaOfKnowledge = "";
	private String                        apiKey;
	private String                        url;
	private String                        modelName;
	private ModelParameters               modelParameters = new ModelParameters();
	private String                        targetLanguage;
	private String                        fragmentLanguage;
	private ElementNegotiator<Term>       terms           = new ElementNegotiator<>(null);
	private ElementNegotiator<Definition> definitions     = new ElementNegotiator<>(null);
	private ElementNegotiator<Example>    examples        = new ElementNegotiator<>(null);
	private ElementNegotiator<Code>       codes           = new ElementNegotiator<>(null);
	private ElementNegotiator<Item>       items           = new ElementNegotiator<>(null);
	private List<Relation>                relations       = new LinkedList<>();
	private boolean                       complete        = false;
	private Step                          step            = Step.INITIAL;

	public AggregationProcess() {
		sources = new LinkedList<>();
	}

	public AggregationProcess(List<MultipartFile> sources) {
		this.sources = sources;
	}

	public void reset() {
		terms = new ElementNegotiator<>(null);
		definitions = new ElementNegotiator<>(null);
		examples = new ElementNegotiator<>(null);
		relations.clear();
	}

	public void setComplete() {
		complete = true;
	}

	/**
	 * Sets the current fragment to the next one.
	 *
	 * @return true if the step was changed, false if the step is already at the end.
	 */
	boolean nextStep() {
		if (step == Step.END) {
			return false;
		}
		step = step.next();
		return true;
	}

	public boolean addSources(Collection<MultipartFile> files) {
		for (var file : files) {
			if (!addSource(file)) {
				return false;
			}
		}
		return true;
	}

	public boolean addSource(MultipartFile file) {
		return sources.add(file);
	}

	/**
	 * Add the given elements as accepted to the corresponding list.
	 *
	 * @param elements The elements to add.
	 * @param <T>      The type of the elements.
	 * @throws IllegalArgumentException if the type of the elements is not supportet.
	 */
	public <T extends KnowledgeElement> void add(List<T> elements) throws IllegalArgumentException {
		if (elements == null || elements.isEmpty()) {
			return;
		}
		var first = elements.getFirst();
		switch (first.getType()) {
			case TERM -> terms.addAll((Collection<Term>) elements);
			case DEFINITION -> definitions.addAll((Collection<Definition>) elements);
			case EXAMPLE -> examples.addAll((Collection<Example>) elements);
			case CODE -> codes.addAll((Collection<Code>) elements);
			case ITEM -> items.addAll((Collection<Item>) elements);
			default -> throw new IllegalArgumentException(STR."Unknown type: \{first.getType()}");
		}
	}

	/**
	 * Suggests the given elements to the corresponding list.
	 *
	 * @param elements The elements to suggest.
	 * @param <T>      The type of the elements.
	 * @throws IllegalArgumentException if the type of the elements is not supportet.
	 * @see ElementNegotiator#suggest(KnowledgeElement)
	 */
	public <T extends KnowledgeElement> void suggest(List<T> elements) throws IllegalArgumentException {
		if (elements == null || elements.isEmpty()) {
			return;
		}
		var first = elements.getFirst();
		switch (first.getType()) {
			case TERM -> ((Collection<Term>) elements).forEach(terms::suggest);
			case DEFINITION -> ((Collection<Definition>) elements).forEach(definitions::suggest);
			case EXAMPLE -> ((Collection<Example>) elements).forEach(examples::suggest);
			case CODE -> ((Collection<Code>) elements).forEach(codes::suggest);
			case ITEM -> ((Collection<Item>) elements).forEach(items::suggest);
			default -> throw new IllegalArgumentException(STR."Unknown type: \{first.getType()}");
		}
	}

	/**
	 * Finds a KnowledgeElement by its id.
	 *
	 * @param id The id of the KnowledgeElement.
	 * @return The KnowledgeElement with the given id or null if no element with the given id was found.
	 */
	public KnowledgeElement findById(String id) throws IllegalArgumentException, NoSuchElementException {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("The id must not be null or empty.");
		}
		var term = terms.findById(id);
		if (term != null) {
			return term;
		}
		var definition = definitions.findById(id);
		if (definition != null) {
			return definition;
		}
		var example = examples.findById(id);
		if (example != null) {
			return example;
		}
		var code = codes.findById(id);
		if (code != null) {
			return code;
		}
		var task = items.findById(id);
		if (task != null) {
			return task;
		}
		throw new NoSuchElementException(STR."No element with id \{id} found.");
	}

	/**
	 * Removes the element with the given id from the process.
	 * Also removes all relations with the element.
	 *
	 * @param id The id of the element to remove.
	 * @return true if the element was removed, false if the element was not found.
	 */
	public boolean removeById(String id) {
		if (id == null || id.isBlank()) {
			return false;
		}
		var find = findById(id);
		if (find == null) {
			return false;
		}
		var removed = terms.removeById(id);
		removed |= definitions.removeById(id);
		removed |= examples.removeById(id);
		removed |= codes.removeById(id);
		removed |= items.removeById(id);
		var elementRelations = find.getRelations();
		for (var rel : elementRelations) {
			relations.remove(rel);
			unlink(rel);
		}
		return removed;
	}

	public boolean approveById(String id) {
		if (id == null || id.isBlank()) {
			return false;
		}
		var accepted = terms.approveById(id);
		accepted |= definitions.approveById(id);
		accepted |= examples.approveById(id);
		accepted |= codes.approveById(id);
		accepted |= items.approveById(id);
		return accepted;
	}

	/**
	 * Rejects the element with the given id.
	 *
	 * @param id The id of the element to reject.
	 * @return true if the element was rejected, false if the element was not found.
	 * @see ElementNegotiator#rejectById(String)
	 */
	public boolean rejectById(String id) {
		if (id == null || id.isBlank()) {
			return false;
		}
		var rejected = terms.rejectById(id);
		rejected |= definitions.rejectById(id);
		rejected |= examples.rejectById(id);
		rejected |= codes.rejectById(id);
		rejected |= items.rejectById(id);
		return rejected;
	}

	public List<Relation> getRelationsByFromId(String id) {
		return relations.stream().filter(it -> it.getFrom().getId().equals(id)).toList();
	}

	public List<Relation> getRelationsByToId(String id) {
		return relations.stream().filter(it -> it.getTo().getId().equals(id)).toList();
	}


	/**
	 * Links two {@link KnowledgeElement}s with a {@link Relation} of the given type.
	 *
	 * @param from The element that is the source of the relation.
	 * @param to   The element that is the target of the relation.
	 * @param type The type of the relation.
	 * @return true if the relation was added, false if the relation already exists.
	 */
	public boolean link(KnowledgeElement from, KnowledgeElement to, RelationType type) {
		if (from == null || to == null) {
			return false;
		}
		if (!containsElement(from.getId()) || !containsElement(to.getId())) {
			return false;
		}
		var relation = new BasicRelation(type, from, to);
		if (!relations.contains(relation)) {
			relations.add(relation);
			from.addRelation(relation);
			return true;
		}
		return false;
	}

	public boolean containsElement(String id) {
		return terms.contains(id) || definitions.contains(id) || examples.contains(id) || codes.contains(id)
			   || tasks.contains(id);
	}

	public boolean link(String fromId, String toId, RelationType type) {
		var from = findById(fromId);
		var to = findById(toId);
		return link(from, to, type);
	}

	/**
	 * Links a term given by its id to a definition given by its id.
	 * The
	 *
	 * @param definitionId The id of the definition.
	 * @param mainTerm     The id of the main term.
	 * @param terms        The ids of the related terms to the definition.
	 * @return true if the terms were linked, false if the definition or the terms were not found.
	 */
	public boolean linkTermsToDefinition(String definitionId, Term mainTerm, List<Term> terms) {
		try {
			var definition = definitions.findById(definitionId);
			var relation = new BasicRelation(RelationType.DEFINED_BY, mainTerm, definition);
			if (!relations.contains(relation)) {
				var reverseRelation = new BasicRelation(RelationType.DEFINES, definition, mainTerm);
				relations.add(relation);
				relations.add(reverseRelation);
			}
			for (var term : terms) {
				var relation1 = new BasicRelation(RelationType.RELATED, mainTerm, term);
				if (!relations.contains(relation1)) {
					var relation2 = new BasicRelation(RelationType.RELATED, term, mainTerm);
					relations.add(relation1);
					relations.add(relation2);
				}
			}
			return true;
		} catch (NoSuchElementException ne) {
			return false;
		}
	}

	public boolean removeAllRelationsWith(String id) {
		return relations.removeIf(it -> it.getFrom().getId().equals(id) || it.getTo().getId().equals(id));
	}

	public boolean unlinkTermsFromDefinition(String id, List<Term> terms) {
		try {
			var definition = definitions.findById(id);
			for (var term : terms) {
				relations.removeIf(it -> it.getFrom().getId().equals(definition.getId())
										 && it.getTo().getId().equals(term.getId()));
				relations.removeIf(it -> it.getFrom().getId().equals(term.getId()) && it.getTo().getId().equals(
						definition.getId()));
			}
			return true;
		} catch (NoSuchElementException ne) {
			return false;
		}
	}

	/**
	 * Checks if the process has elements in the accepted lists.
	 *
	 * @return true if the process has elements in the accepted lists, false if not.
	 */
	public boolean hasElements() {
		return !terms.getAcceptedElements().isEmpty() || !definitions.getAcceptedElements().isEmpty() || !examples
				.getAcceptedElements().isEmpty() || !codes.getAcceptedElements().isEmpty() || !tasks
				.getAcceptedElements().isEmpty();
	}

	public boolean hasRelations() {
		return !relations.isEmpty();
	}

	public boolean hasElementsAndRelations() {
		return hasElements() && hasRelations();
	}

	//region setter/getter
	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public String getFragmentLanguage() {
		return fragmentLanguage;
	}

	public void setFragmentLanguage(String fragmentLanguage) {
		this.fragmentLanguage = fragmentLanguage;
	}

	/**
	 * Number of the Step the process is currently in. Starts with 0.
	 *
	 * @return The number of the step.
	 */
	public int getStepNumber() {
		return step.ordinal();
	}

	/**
	 * Name of the Step the process is currently in.
	 *
	 * @return The name of the step.
	 * @see Step
	 */
	public String getStep() {
		return step.name();
	}

	public List<MultipartFile> getSources() {
		return sources;
	}


	public String getCurrentFragment() {
		return currentFragment;
	}

	public ElementNegotiator<Item> getItems() {
		return items;
	}

	public ModelParameters getModelParameters() {
		return modelParameters;
	}

	public void setModelParameters(
			ModelParameters modelParameters) {
		this.modelParameters = modelParameters;
	}

	public String getAreaOfKnowledge() {
		return areaOfKnowledge;
	}

	public void setAreaOfKnowledge(String areaOfKnowledge) {
		this.areaOfKnowledge = areaOfKnowledge;
	}

	public GPT_Request.ModelLocation getModelLocation() {
		return modelLocation;
	}

	public void setModelLocation(
			GPT_Request.ModelLocation modelLocation) {
		this.modelLocation = modelLocation;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public ElementNegotiator<Term> getTerms() {
		return terms;
	}

	public ElementNegotiator<Definition> getDefinitions() {
		return definitions;
	}

	public ElementNegotiator<Example> getExamples() {
		return examples;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public boolean isComplete() {
		return complete;
	}

	public LocalDateTime getStart() {
		return start;
	}
//endregion

	/**
	 * Shows the current step of the aggregation process.
	 */
	private enum Step {
		INITIAL,
		TERMS,
		DEFINITIONS,
		EXAMPLES,
		CODE,
		QUESTIONS,
		END;


		public Step next() {
			return switch (this) {
				case INITIAL -> TERMS;
				case TERMS -> DEFINITIONS;
				case DEFINITIONS -> EXAMPLES;
				case EXAMPLES -> CODE;
				case CODE -> QUESTIONS;
				case QUESTIONS, END -> END;
			};
		}
	}
}
