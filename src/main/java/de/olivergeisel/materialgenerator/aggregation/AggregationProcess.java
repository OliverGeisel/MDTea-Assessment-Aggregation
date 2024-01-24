package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.collect.SourceFragmentCollection;
import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Definition;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Example;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Session-Object for the aggregation process.
 * ONLY for the aggregation process and usage in Controllers.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 1.1.0
 */
public class AggregationProcess {

	private final LocalDateTime             start         = LocalDateTime.now();
	private       List<MultipartFile>       sources;
	private       GPT_Request.ModelLocation modelLocation = GPT_Request.ModelLocation.REMOTE;
	private       SourceFragmentCollection  sourceFragmentCollection;
	private       String                    currentFragment;
	private       String                    areaOfKnowledge;
	private       String                    apiKey;
	private       String                    url;
	private       String                    modelName;
	private       List<Term>                terms         = new LinkedList<>();
	private       List<Definition>          definitions   = new LinkedList<>();
	private       List<Example>             examples      = new LinkedList<>();
	private       List<Relation>            relations     = new LinkedList<>();
	private       boolean                   complete      = false;
	private       Step                      step          = Step.INITIAL;

	public AggregationProcess() {
		sources = new LinkedList<>();
	}

	public AggregationProcess(List<MultipartFile> sources) {
		this.sources = sources;
	}

	public void reset() {
		terms.clear();
		definitions.clear();
		examples.clear();
		relations.clear();
	}

	public boolean setComplete() {
		return complete = true;
	}

	/**
	 * Sets the current fragment to the next one.
	 *
	 * @return true if the step was changed, false if the step is already at the end.
	 */
	boolean nextStep() {
		step = step.next();
		return step == Step.END;
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
	 * Add the given elements to the corresponding list.
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
			default -> throw new IllegalArgumentException("Unknown type: " + first.getType());
		}
	}

	//region setter/getter
	public int getStepNumber() {
		return step.ordinal();
	}

	public String getStep() {
		return step.name();
	}

	public List<MultipartFile> getSources() {
		return sources;
	}

	public SourceFragmentCollection getSourceFragmentCollection() {
		return sourceFragmentCollection;
	}

	public String getCurrentFragment() {
		return currentFragment;
	}

	public void setCurrentFragment(String currentFragment) {
		this.currentFragment = currentFragment;
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

	public List<Term> getTerms() {
		return terms;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public List<Example> getExamples() {
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
		PROOFS,
		QUESTIONS,
		END;


		public Step next() {
			return switch (this) {
				case INITIAL -> TERMS;
				case TERMS -> DEFINITIONS;
				case DEFINITIONS -> EXAMPLES;
				case EXAMPLES -> PROOFS;
				case PROOFS -> QUESTIONS;
				case QUESTIONS, END -> END;
			};
		}
	}
}
