package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.AggregationProcess;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.StructureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for the knowledge model. Provides methods to access the knowledge model.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeObject
 * @see KnowledgeElement
 * @since 1.1.0
 */
@Service
public class KnowledgeModelService {

	private final static Logger LOGGER = LoggerFactory.getLogger(KnowledgeModelService.class);

	private final ElementRepository elementRepository;

	private final RelationRepository relationRepository;

	private final StructureRepository structureRepository;

	public KnowledgeModelService(ElementRepository elementRepository, RelationRepository relationRepository,
			StructureRepository structureRepository) {
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.structureRepository = structureRepository;
	}

	public Optional<KnowledgeObject> getKnowledgeObject(String id) {
		return structureRepository.findById(id);
	}

	public long elementCount() {
		return elementRepository.count();
	}

	public long relationCount() {
		return relationRepository.count();
	}

	public long termCount() {
		return elementRepository.countByType(KnowledgeType.TERM);
	}

	public long count(KnowledgeType type) {
		return elementRepository.countByType(type);
	}

	public long definitionCount() {
		return elementRepository.countByType(KnowledgeType.DEFINITION);
	}

	public long exampleCount() {
		return elementRepository.countByType(KnowledgeType.EXAMPLE);
	}

	public long structureCount() {
		return structureRepository.count();
	}

	public void integrate(AggregationProcess process) {
		LOGGER.info("Integrating process started at {} into knowledge model.", process.getStart());

		var terms = process.getTerms().getAcceptedElements();
		var definitions = process.getDefinitions().getAcceptedElements();
		var examples = process.getExamples().getAcceptedElements();
		var relations = process.getRelations();

		// remove the unused elements
		var notAcceptedTerms = process.getTerms().getSuggestedElements();
		notAcceptedTerms.forEach(it -> process.removeAllRelationsWith(it.getId()));
		var notAcceptedDefinitions = process.getDefinitions().getSuggestedElements();
		notAcceptedDefinitions.forEach(it -> process.removeAllRelationsWith(it.getId()));
		var notAcceptedExamples = process.getExamples().getSuggestedElements();
		notAcceptedExamples.forEach(it -> process.removeAllRelationsWith(it.getId()));

		//save all remaining elements
		elementRepository.saveAll(terms);
		linkWithKnowledgeObject(terms);
		elementRepository.saveAll(definitions);
		linkWithKnowledgeObject(definitions);
		elementRepository.saveAll(examples);
		linkWithKnowledgeObject(examples);
		relationRepository.saveAll(relations);


		LOGGER.info("Integrating process finished. Add {} terms, {} definitions, {} examples and {} relations.",
				process.getTerms().getAcceptedElements().size(), process.getDefinitions().getAcceptedElements().size(),
				process.getExamples().getAcceptedElements().size(), process.getRelations().size());
	}

	private <T extends KnowledgeElement> void linkWithKnowledgeObject(List<T> elements) {
		for (KnowledgeElement element : elements) {
			var structureOptional = structureRepository.findById(element.getStructureId());
			structureOptional.ifPresentOrElse(it -> {
						it.linkElement(element);
						structureRepository.save(it);
					},
					() -> {
						var structure = new KnowledgeLeaf(element.getStructureId());
						structureRepository.save(structure);
						structure.linkElement(element);
						var root = structureRepository.findRoot();
						root.addObject(structure);
						structureRepository.save(root); // todo check if this is necessary
					});
		}
	}

	//region setter/getter
	public List<String> getStructureIds() {
		return structureRepository.findAll().stream().map(KnowledgeObject::getName).toList();
	}

	public RootStructureElement getRoot() {
		return structureRepository.findRoot();
	}
//endregion
}
