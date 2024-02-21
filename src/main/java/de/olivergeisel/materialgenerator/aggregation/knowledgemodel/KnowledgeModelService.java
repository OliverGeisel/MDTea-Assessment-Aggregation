package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.AggregationProcess;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms.AddElementForm;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.BasicRelation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.*;
import de.olivergeisel.materialgenerator.aggregation.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
@Transactional
public class KnowledgeModelService implements KnowledgeModel<Relation> {

	private final static Logger LOGGER = LoggerFactory.getLogger(KnowledgeModelService.class);

	private final ElementRepository elementRepository;

	private final RelationRepository relationRepository;

	private final StructureRepository structureRepository;

	private final KnowledgeStructureService structureService;

	private final Driver neo4jDriver;


	public KnowledgeModelService(ElementRepository elementRepository, RelationRepository relationRepository,
			StructureRepository structureRepository, KnowledgeStructureService structureService, Driver neo4jDriver) {
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.structureRepository = structureRepository;
		this.structureService = structureService;
		this.neo4jDriver = neo4jDriver;
	}

	//region private methods

	/**
	 * Get Ids of all {@link KnowledgeElement}s that are connected with the given element in the model.
	 *
	 * @param elementId the id of the element
	 * @return a list of all ids of all elements that are connected with the given element.
	 * @throws NoSuchElementException if no element with the given id was found
	 */
	private Collection<String> findRelatedElementIDs(String elementId) throws NoSuchElementException {
		var element = findElementById(elementId).orElseThrow();
		return Arrays.stream(getRelatedElements(element)).map(KnowledgeElement::getId).toList();
	}
	//endregion


	//region statistic and existence

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param element the element to check
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the element was null
	 */
	@Override
	public boolean containsElement(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		return containsElement(element.getId());
	}

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param elementId the id of the element
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the elementId was null
	 */
	@Override
	public boolean containsElement(String elementId) throws IllegalArgumentException {
		if (elementId == null) {
			throw new IllegalArgumentException("ElementId was null!");
		}
		return elementRepository.existsById(elementId);
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

	@Override
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

	public boolean hasStructureObject(String id) {
		return structureRepository.existsById(id);
	}

	//endregion

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

	public void deleteElement(String id) {
		elementRepository.deleteById(id);
	}

	/**
	 * Removes the given element from the model.
	 *
	 * @param element the element to remove
	 * @return true if the element was removed, false if not
	 * @throws IllegalArgumentException if the element was null
	 */
	public boolean remove(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		elementRepository.delete(element);
		return true;
	}


	public void deleteRelation(String id) {
		relationRepository.deleteById(UUID.fromString(id));
	}

	public void deleteStructure(String id) {
		structureRepository.deleteById(id);
	}


	/**
	 * Link two elements with the given relation.
	 * <p>
	 * The elements are added to the model if they are not already in it.
	 *
	 * @param elementFrom the element from which the relation goes
	 * @param elementTo   the element to which the relation goes
	 * @param relation    the relation
	 * @return true if both elements were added and linked, false if not. false is also returned if the relation
	 * doesn't contain the elements.
	 * @throws IllegalArgumentException if one of the arguments was null.
	 */
	public boolean addAndLink(KnowledgeElement elementFrom, KnowledgeElement elementTo, Relation relation) {
		if (relation == null || elementFrom == null || elementTo == null) {
			throw new IllegalArgumentException("Relation or element was null!");
		}
		if (!relation.getFromId().equals(elementFrom.getId()) || !relation.getToId().equals(elementTo.getId())) {
			return false;
		}
		relation.setFrom(elementFrom);
		relation.setTo(elementTo);
		addKnowledge(elementFrom);
		addKnowledge(elementTo);
		var type = relation.getType();
		link(elementFrom, elementTo, type);
		return true;
	}


	/**
	 * Links the given elements with the given RelationType.
	 * The reversed relation will also be created.
	 *
	 * @param from the element to link from
	 * @param to   the element to link to
	 * @return the created relation
	 * @throws IllegalArgumentException if one of the arguments was null
	 * @throws IllegalStateException    if the elements were not in the model
	 */
	public Relation link(KnowledgeElement from, KnowledgeElement to, RelationType type) throws IllegalStateException {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Element was null!");
		}
		if (type == null) {
			throw new IllegalArgumentException("RelationType was null!");
		}
		if (!containsElement(from) || !containsElement(to)) {
			throw new IllegalStateException("Element was not in the model!");
		}

		var relation = new BasicRelation(type, from, to);
		var reverseRelation = new BasicRelation(type.getInverted(), to, from);
		relation = relationRepository.save(relation);
		reverseRelation = relationRepository.save(reverseRelation);
		return relation;
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
			case ITEM -> newElement = createItem(form);
			default -> throw new IllegalStateException(STR."Unexpected value: \{form.getType()}");
		}
		newElement.setStructureId(structureId);
		structureObject.ifPresent(it -> {
			it.linkElement(newElement);
			structureRepository.save(it);
		});
		elementRepository.save(newElement);
	}

	private Item createItem(AddElementForm form) {
		switch (form.getItemType()) {
			case TRUE_FALSE -> {
				var item = new TrueFalseItem(form.getContent(), form.isTrue(),
						createId(STR."\{form.getHeadline()}-TRUE_FALSE_ITEM"));
				item.setStructureId(form.getStructureId());
				return item;
			}
			case SINGLE_CHOICE -> {
				return null;
			}
			case MULTIPLE_CHOICE -> {
				return null;
			}
			default -> throw new IllegalStateException(STR."Unexpected value: \{form.getItemType()}");
		}
	}

	private String createId(String name) {
		var exists = elementRepository.existsById(name);
		return exists ? createId(name + UUID.randomUUID()) : name;
	}


	/**
	 * Adds an element to the model. Link the element to the structure of the model.
	 * If the element is already in the model, nothing happens.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not (because it was already in the model)
	 * @throws IllegalArgumentException if the element was null
	 */
	@Override
	public boolean addKnowledge(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		if (containsElement(element)) {
			return false;
		}
		elementRepository.save(element);
		return linkToStructure(element);
	}

	/**
	 * Adds a collection of elements to the model. Link all new elements with all elements that are already in the
	 * model
	 * and described in the relations.
	 *
	 * @param elements the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	@Override
	public boolean addKnowledge(Collection<KnowledgeElement> elements) {
		if (elements == null || elements.isEmpty()) {
			return false;
		}
		return elements.stream().anyMatch(this::addKnowledge);
	}

	/**
	 * Adds a relation to the model.
	 * If the relation is already in the model, nothing happens.
	 *
	 * @param relation the relation to add.
	 * @return true if the relation was added, false if not.
	 * @throws IllegalArgumentException if the relation was null.
	 * @throws NoSuchElementException   if the from or to element of the relation was not found
	 */
	public boolean addAndLink(Relation relation) throws IllegalArgumentException, NoSuchElementException {
		boolean hasFrom = false;
		boolean hasTo = false;
		if (relation == null) {
			throw new IllegalArgumentException("Relation was null!");
		}
		var fromId = relation.getFromId();
		var fromElement = findElementById(fromId).orElseThrow();
		var toId = relation.getToId();
		var toElement = findElementById(toId).orElseThrow();
		hasFrom = true;
		relation.setFrom(fromElement);
		hasTo = true;
		relation.setTo(toElement);
		link(fromElement, toElement, relation.getType());
		return relation != null;
	}

	//region structure

	/**
	 * Adds a structure to the root structure element.
	 *
	 * @param object the structure to add
	 * @return true if the structure was added, false if not
	 */
	public boolean addStructureToRoot(KnowledgeObject object) {
		var root = structureRepository.findRoot();
		root.addObject(object);
		structureRepository.save(object);
		structureRepository.save(root);
		return true;
	}

	/**
	 * Adds a structure to the given structure element.
	 *
	 * @param structure the structure to add
	 * @param partToAdd the part to add to the structure
	 * @return true if the structure was added, false if not
	 */
	public boolean addStructureTo(KnowledgeFragment structure, KnowledgeObject partToAdd) {
		if (structure == null || partToAdd == null) {
			return false;
		}
		if (structureRepository.existsById(structure.getId())) {
			return false;
		}
		structure.addObject(partToAdd);
		structureRepository.save(partToAdd);
		structureRepository.save(structure);
		return true;
	}

	private boolean hasStructureSimilar(String structureId) {
		return structureService.containsSimilar(structureId);
	}

	//endregion

	//region elements


	/**
	 * Link the given element to the structure of the model.
	 *
	 * @param element the element to link to the structure of the model
	 * @return true if the element was linked, false if not
	 * @throws IllegalArgumentException if the element was null
	 * @throws NoSuchElementException   if the structure was not found or is null
	 */
	private boolean linkToStructure(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("Element was null!");
		}
		var structureId = element.getStructureId();
		if (structureId == null || structureId.isBlank() || structureRepository.existsById(structureId)) {
			LOGGER.warn("StructureObject {} is not part of the structure", structureId);
			throw new NoSuchElementException(
					STR."StructureObject \{structureId} is not part of the structure or is null");
		}
		AtomicBoolean result = new AtomicBoolean(false);
		structureRepository.findById(structureId).ifPresent(it -> {
			result.set(it.linkElement(element));
			structureRepository.save(it);
		});
		return result.get();
	}

	/**
	 * All {@link KnowledgeElement}s that are connected with the given element. In and outgoing.
	 *
	 * @param element the element to get the related elements for
	 * @return an Array of {@link KnowledgeElement}s that are connected with the given element
	 */
	private KnowledgeElement[] getRelatedElements(KnowledgeElement element) {
		var returnList = new LinkedList<KnowledgeElement>();
		var outgoing = new LinkedList<KnowledgeElement>();
		relationRepository.findByFromId(element.getId()).forEach(it -> {
			var to = it.getTo();
			if (!outgoing.contains(to)) {
				outgoing.add(to);
			}
		});
		var incoming = relationRepository.findDistinctByToId(element.getId()).map(Relation::getFrom).toList();
		returnList.addAll(outgoing);
		returnList.addAll(incoming);
		return returnList.toArray(new KnowledgeElement[0]);
	}

	/**
	 * Returns all elements that connected with the given element in the model.
	 *
	 * @param elementId the id of the element
	 * @return a set of all elements that are connected with the given element.
	 * @throws NoSuchElementException if no element with the given id was found
	 */
	public Set<KnowledgeElement> findRelatedElements(String elementId) throws NoSuchElementException {
		return Arrays.stream(getRelatedElements(findElementById(elementId).orElseThrow())).collect(Collectors.toSet());
	}


	//endregion

	//region relations

	/**
	 * All {@link Relation}s that are connected with the given element. In and outgoing.
	 *
	 * @param element the element to get the related elements for
	 * @return an Array of {@link Relation}s that are connected with the given element
	 */
	private Relation[] getAllRelations(KnowledgeElement element) {
		var ownRelations = element.getRelations();
		var otherRelations = relationRepository.findByToId(element.getId()).toList();
		var returnList = new ArrayList<>(ownRelations);
		return returnList.toArray(new Relation[0]);
	}
	//endregion

	//region source (source of the knowledge)

	/**
	 * Adds a source to the model.
	 *
	 * @param sources the source to add
	 * @return true if the source was added, false if not
	 */
	@Override
	public boolean addSource(KnowledgeSource sources) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Adds a collection of KnowledgeObjects to the model.
	 *
	 * @param sources the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	@Override
	public boolean addSource(Collection<KnowledgeSource> sources) {
		if (sources == null || sources.isEmpty()) {
			return false;
		}
		return sources.stream().anyMatch(this::addSource);
	}
	//endregion

	//region knowledgeNode (reading partial model and give back)

	/**
	 * Returns all elements that are connected with the given structure object in the model. If the structure object
	 * is not found, it will not be searched for similar objects when includeSimilar is true.
	 *
	 * @param structureId    the id of the structure object
	 * @param includeSimilar if true, also search for elements that contain the given structureId in their own
	 *                       structureId
	 * @return a set of all elements that are connected with the given structure object
	 */
	private Set<KnowledgeNode> getKnowledgeNodesFor(String structureId, boolean includeSimilar,
			boolean similarWhenFound) {
		Set<KnowledgeNode> back = new HashSet<>();
		boolean hasStructureObject = false;
		try {
			hasStructureObject = hasStructureObject(structureId);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("No structure object with id: '{}' found.", structureId);
		}
		if (hasStructureObject) {
			var structureObject = findStructureById(structureId).orElseThrow();
			var elements = structureObject.getLinkedElements();
			for (var element : elements) {
				back.add(getKnowledgeNode(element));
			}
		}
		if (includeSimilar && (similarWhenFound || !hasStructureObject)) {
			LOGGER.info("Include similar objects for structure object '{}'.", structureId);
			try {
				back.addAll(getKnowledgeNodesForSimilar(structureId));
			} catch (NoSuchElementException e) {
				LOGGER.info("No similar object for structure object '{}' found.", structureId);
			}
		}
		return back;
	}

	/**
	 * Returns all elements that are connected with the given element in the model.
	 *
	 * @param elementId the element id of the element
	 * @return a set of all elements and relations that are connected with the given element
	 */
	public KnowledgeNode getKnowledgeNode(String elementId) throws NoSuchElementException {
		if (!containsElement(elementId)) {
			throw new NoSuchElementException(STR."No element with id \"\{elementId}\" found");
		}
		var element = findElementById(elementId).orElseThrow();
		return getKnowledgeNode(element);
	}

	/**
	 * Create a KnowledgeNode for a given KnowledgeElement
	 * <p>
	 *     The given element is the center of the KnowledgeNode. This is the mainElement.
	 *     Then the linked elements are added to the KnowledgeNode by getting the structureObject of the element. The
	 *     relations are also added. The related elements are all elements that are connected with the linked elements.
	 *
	 * @param element element you want
	 * @return a Collection of all elements and relations that are connected with the given element
	 * @throws NoSuchElementException if the element is not in the Model or the structure object is not in the model
	 */
	public KnowledgeNode getKnowledgeNode(KnowledgeElement element) throws NoSuchElementException {
		if (!containsElement(element)) {
			throw new NoSuchElementException(STR."No element: \"\{element}\" found");
		}
		var structureObject = structureRepository.findById(element.getStructureId()).orElseThrow();
		var linkedElements = structureObject.getLinkedElements(); // todo decide how deep
		var relatedElements = new LinkedList<>();
		var relations = new HashSet<>();
		for (var linked : linkedElements) {
			var tempList = getRelatedElements(linked);
			Arrays.stream(tempList).filter(it -> !linkedElements.contains(it)).forEach(relatedElements::add);
			var tempRelations = getAllRelations(linked);
			relations.addAll(Arrays.asList(tempRelations));
		}
		return new KnowledgeNode(structureObject, element, linkedElements.toArray(new KnowledgeElement[0]),
				relatedElements.toArray(new KnowledgeElement[0]), relations.toArray(new Relation[0]));

	}

	private Set<KnowledgeNode> getKnowledgeNodesForSimilar(String structureId) throws NoSuchElementException {
		Set<KnowledgeNode> back = new HashSet<>();
		if (!hasStructureSimilar(structureId)) {
			throw new NoSuchElementException(STR."No structure object with id \{structureId}or similar found");
		} else {
			var similarObject = structureService.getSimilarObjectById(structureId);
			var elements = similarObject.getLinkedElements();
			for (var element : elements) {
				back.add(getKnowledgeNode(element.getId()));
			}
		}
		return back;
	}


	/**
	 * Returns all elements that are connected with the given structure object in the model.
	 * If the structure object is not found, it will not be searched for similar objects.
	 *
	 * @param structureId the id of the structure object
	 * @return a set of all elements that are connected with the given structure object.
	 */
	public Set<KnowledgeNode> getKnowledgeNodesFor(String structureId) {
		return getKnowledgeNodesFor(structureId, false, false);
	}

	/**
	 * Returns all elements that are connected with the given structure object in the model.
	 * If the structure object is not found, it will be searched for similar objects.
	 *
	 * @param structureId the id of the structure object
	 * @return a set of all elements that are connected with the given structure object.
	 */
	public Set<KnowledgeNode> getKnowledgeNodesOrSimilarIfNotFoundFor(String structureId) {
		return getKnowledgeNodesFor(structureId, true, false);
	}

	/**
	 * Returns all elements that are connected with the given structure object in the model.
	 * It inclueds similar objects in the structure.
	 *
	 * @param structureId the id of the structure object
	 * @return a set of all elements that are connected with the given structure object.
	 */
	public Set<KnowledgeNode> getKnowledgeNodesIncludingSimilarFor(String structureId) {
		return getKnowledgeNodesFor(structureId, true, true);
	}
	//endregion


	//region setter/getter

	/**
	 * Returns all ids of all KnowledgeObjects in the model.
	 *
	 * @return a list of all ids of all KnowledgeObjects in the model
	 */
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
