package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.AggregationProcess;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms.AddElementForm;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.BasicRelation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.*;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for the knowledge model. Provides methods to access the knowledge model.
 * And edit it.
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

	private final Driver neo4jDriver;


	public KnowledgeModelService(ElementRepository elementRepository, RelationRepository relationRepository,
			StructureRepository structureRepository, Driver neo4jDriver) {
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.structureRepository = structureRepository;
		this.neo4jDriver = neo4jDriver;
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

	public List<KnowledgeElement> getElementsForStructure(String id) {
		var structure = structureRepository.findById(id);
		return structure.map(knowledgeObject -> knowledgeObject.getLinkedElements().stream().toList())
						.orElseGet(List::of);
	}

	public Optional<KnowledgeElement> findElementById(String id) {
		if (id == null || id.isBlank()) {
			return Optional.empty();
		}
		return elementRepository.findById(id);
	}

	public Optional<Relation> findRelationById(String id) {
		if (id == null || id.isBlank()) {
			return Optional.empty();
		}
		return relationRepository.findById(UUID.fromString(id));
	}

	public Optional<KnowledgeObject> findStructureById(String id) {
		if (id == null || id.isBlank()) {
			return Optional.empty();
		}
		return structureRepository.findById(id);
	}

	public void deleteElement(String id) {
		elementRepository.deleteById(id);
	}

	public void deleteRelation(String id) {
		relationRepository.deleteById(UUID.fromString(id));
	}

	public void deleteStructure(String id) {
		structureRepository.deleteById(id);
	}


	/**
	 * change a {@link KnowledgeLeaf} to a {@link KnowledgeFragment}. The leaf will be removed from the database and the
	 * fragment will be added. The linked elements will be moved to the new fragment.
	 * Will do nothing if the leaf does not exist.
	 *
	 * @param id the id of the leaf
	 */
	public void leafToNode(String id) {
		// Todo check if works
		var leaf = structureRepository.findById(id);
		if (leaf.isEmpty()) {
			return;
		}
		String parentId;
		try (var session = neo4jDriver.session()) {
			var result = session.run(
					STR."Match (a:KnowledgeObject {id: '\{id}'})<-[:CONTAINS]-(f:KnowledgeObject) return f");
			parentId = result.next().get("_fields").get(0).get("properties").get("name").asString();
		}
		var parent = structureRepository.findById(parentId);
		leaf.ifPresent(it -> {
			var links = it.getLinkedElements();
			var newNode = new KnowledgeFragment(it.getName());
			for (var link : links) {
				newNode.linkElement(link);
				link.setStructureId(newNode.getName());
				elementRepository.save(link);
			}
			parent.ifPresent(it2 -> {
				var parentFragment = (KnowledgeFragment) it2;
				parentFragment.addObject(newNode);
				parentFragment.removeObject(it);
				structureRepository.save(parentFragment);
			});
			structureRepository.save(newNode);
			structureRepository.delete(it);
		});
	}

	public void linkElements(String fromId, RelationType relationType, String toId) {
		var fromElement = elementRepository.findById(fromId);
		var toElement = elementRepository.findById(toId);
		if (fromElement.isEmpty() || toElement.isEmpty()) {
			return;
		}
		var relation = new BasicRelation(relationType, fromElement.get(), toElement.get());
		relationRepository.save(relation);
	}

	/**
	 * Adds a new element to the knowledge model.
	 * Links the new element to the structure point with the given id, if it exists.
	 *
	 * @param form the form with the data for the new element
	 */
	public void addElement(AddElementForm form) {
		var structureId = form.getStructureId();
		var structureObject = structureRepository.findById(structureId);
		if (structureObject.isEmpty()) {
			return;
		}
		KnowledgeElement newElement;
		switch (form.getType()) {
			case TERM -> newElement = new Term(form.getContent(), createId(STR."\{form.getContent()}-TERM"),
					form.getType().name());
			case DEFINITION -> newElement = new Definition(form.getContent(),
					createId(STR."\{form.getContent()}-DEFINITION"));
			case EXAMPLE -> newElement = new Example(form.getContent(),
					createId(STR."\{form.getContent()}-EXAMPLE"), form.getType().name());
			case CODE -> newElement = new Code(form.getLanguage(), form.getHeadline(), form.getContent(),
					createId(STR."\{form.getContent()}-CODE"));
			case IMAGE -> newElement = new Image(form.getContent(), form.getDescription(), form.getHeadline(),
					(STR."\{form.getContent()}-IMAGE"));
			case TEXT -> newElement =
					new Text(form.getHeadline(), form.getContent(), createId(STR."\{form.getContent()}-TEXT"));
			case FACT -> newElement = new Fact(form.getContent(), createId(STR."\{form.getContent()}-FACT"));
			default -> throw new IllegalStateException(STR."Unexpected value: \{form.getType()}");
		}
		newElement.setStructureId(structureId);
		structureObject.ifPresent(it -> {
			it.linkElement(newElement);
			structureRepository.save(it);
		});
		elementRepository.save(newElement);
	}

	private String createId(String name) {
		var exists = elementRepository.existsById(name);
		return exists ? createId(name + UUID.randomUUID().toString()) : name;
	}


	//region setter/getter
	public List<String> getStructureIds() {
		return structureRepository.findAll().stream().map(KnowledgeObject::getName).toList();
	}

	public RootStructureElement getRoot() {
		return structureRepository.findRoot();
	}

	public List<KnowledgeElement> getAllElements() {
		return elementRepository.findAll();
	}

	public List<Relation> getAllRelations() {
		return relationRepository.findAll();
	}

	public List<KnowledgeObject> getAllStructureElements() {
		return structureRepository.findAll();
	}
//endregion
}
