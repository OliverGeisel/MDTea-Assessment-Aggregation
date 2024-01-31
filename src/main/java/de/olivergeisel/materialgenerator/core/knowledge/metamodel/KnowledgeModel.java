package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationGenerator;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A model of knowledge. It contains structure, all elements, relations and sources.
 */
public class KnowledgeModel {

	private static final Logger                                logger              = LoggerFactory.getLogger(
			KnowledgeModel.class);
	private final        Map<Relation, RelationIdPair>         unfinishedRelations = new HashMap<>();
	private final        Set<KnowledgeSource>                  sources             = new HashSet<>();
	private final        KnowledgeStructure                    structure;
	private final        Graph<KnowledgeElement, RelationEdge> graph;
	private final        String                                version;
	private final        String                                name;

	public KnowledgeModel() {
		this(new RootStructureElement());
	}

	public KnowledgeModel(RootStructureElement root) {
		this(root, "0.0.0", "");
	}

	public KnowledgeModel(RootStructureElement root, String version, String name) {
		if (root == null) {
			throw new IllegalArgumentException("Root was null!");
		}
		this.structure = new KnowledgeStructure(root);
		this.name = name;
		this.graph = new DirectedWeightedPseudograph<>(RelationEdge.class);
		this.version = version;
	}

	/**
	 * Adds a relation to the model.
	 * If the relation is already in the model, nothing happens.
	 *
	 * @param relation the relation to add.
	 * @return true if the relation was added, false if not.
	 * @throws IllegalArgumentException if the relation was null.
	 */
	public boolean addAndLink(Relation relation) throws IllegalArgumentException {
		boolean hasFrom = false;
		boolean hasTo = false;
		if (relation == null) {
			throw new IllegalArgumentException("Relation was null!");
		}
		var fromId = relation.getFromId();
		var fromElement = get(fromId);
		var toId = relation.getToId();
		var toElement = get(toId);
		if (fromElement != null) {
			hasFrom = true;
			relation.setFrom(fromElement);
		}
		if (toElement != null) {
			hasTo = true;
			relation.setTo(toElement);
		}
		if (!(hasFrom && hasTo)) {
			unfinishedRelations.put(relation, new RelationIdPair(fromId, toId));
		} else {
			return link(fromElement, toElement, relation);
		}
		return false;
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
	 * doesn't
	 * contain the elements.
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
	 * Adds an element to the model and links it with all relations that are already in the model.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not
	 */
	public boolean addAndLink(KnowledgeElement element) {
		addKnowledge(element);
		var relations = element.getRelations();
		relations.forEach(relation -> {
			var type = relation.getType();
			var toId = relation.getToId();
			if (contains(toId)) {
				var toElement = get(toId);
				link(element, toElement, relation.getType());
				try {
					relation.getTo();
				} catch (IllegalStateException e) {
					relation.setTo(toElement);
				}
				try {
					relation.getFrom();
				} catch (IllegalStateException e) {
					relation.setFrom(element);
				}
				// reverse relation
				if (toElement.getRelations().stream().noneMatch(r -> r.getToId().equals(element.getId())
																	 && r.hasType(type.getInverted()))) {
					var newReverseRelation = RelationGenerator.create(type.getInverted(), toElement, element);
					toElement.addRelation(newReverseRelation);
				}
			} else {
				unfinishedRelations.put(relation, new RelationIdPair(element.getId(), toId));
			}
		});
		return true;
	}

	/**
	 * Adds an element to the model.  Link the element to all existing elements that are described in relations.
	 * If the element is already in the model, nothing happens.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not (because it was already in the model)
	 */
	public boolean addKnowledge(KnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		if (contains(element)) {
			return false;
		}
		var back = graph.addVertex(element);
		if (!element.getRelations().isEmpty()) {
			addAndLink(element);
		}
		return back;
	}

	/**
	 * Adds a collection of elements to the model. Link all new elements with all elements that are already in the
	 * model
	 * and described in the relations.
	 *
	 * @param elements the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	public boolean addKnowledge(Collection<KnowledgeElement> elements) {
		if (elements.isEmpty()) {
			return false;
		}
		var adding = elements.stream().map(this::addKnowledge).max(Boolean::compareTo).orElseThrow();
		if (hasUnfinishedRelations()) {
			tryCompleteLinking();
		}
		updateStructure();
		return adding;
	}

	/**
	 * Adds a collection of KnowledgeObjects to the model.
	 *
	 * @param sources the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	public boolean addSource(Collection<KnowledgeSource> sources) {
		return this.sources.addAll(sources);
	}

	/**
	 * Adds a source to the model.
	 *
	 * @param sources the source to add
	 * @return true if the source was added, false if not
	 */
	public boolean addSource(KnowledgeSource sources) {
		return this.sources.add(sources);
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
		if (!getRoot().contains(structure)) {
			return false;
		}
		structure.addObject(partToAdd);
		return true;
	}

	public boolean addStructureToRoot(KnowledgeObject object) {
		this.structure.getRoot().addObject(object);
		updateStructure();
		return true;
	}

	public boolean hasStructureObject(String id) {
		return this.structure.contains(id);
	}

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param elementId the id of the element
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the elementId was null
	 */
	public boolean contains(String elementId) throws IllegalArgumentException {
		if (elementId == null) {
			throw new IllegalArgumentException("ElementId was null!");
		}
		return graph.vertexSet().stream().anyMatch(it -> it.getId().equals(elementId));
	}

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param element the element to check
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the element was null
	 */
	public boolean contains(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		return graph.containsVertex(element);
	}


	/**
	 * Returns all elements that connected with the given element in the model.
	 *
	 * @param elementId the id of the element
	 * @return a set of all elements that are connected with the given element.
	 * @throws NoSuchElementException if no element with the given id was found
	 */
	public Set<KnowledgeElement> findAll(String elementId) throws NoSuchElementException {
		var matchingIDs = findRelatedElementIDs(elementId);
		return graph.vertexSet().stream().filter(it -> matchingIDs.contains(it.getId())).collect(Collectors.toSet());
	}

	private Collection<String> findRelatedElementIDs(String elementId) {
		var element = get(elementId);
		return Arrays.stream(getRelatedElements(element)).map(KnowledgeElement::getId).toList();
	}

	/**
	 * Returns the element that connected with the given element in the model.
	 *
	 * @param id the id of the element
	 * @return the element that is connected with the given element
	 * @throws NoSuchElementException if no element with the given id was found
	 */
	public KnowledgeElement get(String id) throws NoSuchElementException {
		return graph.vertexSet().stream().filter(it -> it.getId().equals(id)).findFirst()
					.orElseThrow(() -> new NoSuchElementException("No element with id " + id + " found"));
	}

	private Relation[] getAllRelations(KnowledgeElement element) {
		var ownRelations = element.getRelations();
		var elementId = element.getId();
		var otherRelations = graph.incomingEdgesOf(element).stream().map(graph::getEdgeSource)
								  .map(KnowledgeElement::getRelations)
								  .flatMap(it -> it.stream().filter(relation -> relation.getToId().equals(elementId)))
								  .toList(); // todo still needed?
		var returnList = new ArrayList<>(ownRelations);
		return returnList.toArray(new Relation[0]);
	}

	private KnowledgeElement[] getRelatedElements(KnowledgeElement element) {
		var returnList = new LinkedList<KnowledgeElement>();
		var outgoing = graph.outgoingEdgesOf(element).stream().map(graph::getEdgeTarget).toList();
		var incoming = graph.incomingEdgesOf(element).stream().map(graph::getEdgeSource).toList();
		returnList.addAll(outgoing);
		returnList.addAll(incoming);
		return returnList.toArray(new KnowledgeElement[0]);
	}

	/**
	 * Check if there are any relations that could not be completed.
	 *
	 * @return true if there are unfinished relations, false if not.
	 */
	public boolean hasUnfinishedRelations() {
		return !unfinishedRelations.isEmpty();
	}

	/**
	 * Links the given elements with the given relation.
	 *
	 * @param from     the element to link from
	 * @param to       the element to link to
	 * @param relation the relation to link with
	 * @return true if the elements were linked, false if not
	 */
	public boolean link(KnowledgeElement from, KnowledgeElement to, Relation relation) {
		var edge = new RelationEdge<>(from, to, relation.getType());
		boolean back = graph.addEdge(from, to, edge);
		back = back && graph.addEdge(to, from, new RelationEdge<>(from, to, relation.getType().getInverted(), edge));
		return back;
	}

	/**
	 * Links the given elements with the given edge.
	 *
	 * @param from the element to link from
	 * @param to   the element to link to
	 * @param edge the edge to link with
	 * @return true if the elements were linked, false if not
	 */
	public boolean link(KnowledgeElement from, KnowledgeElement to, RelationEdge<KnowledgeElement> edge) {
		graph.addEdge(from, to, edge);
		graph.addEdge(to, from, new RelationEdge<>(edge.getRelation(), edge));
		return true;
	}

	/**
	 * Links the given elements with the given type and creates the reverse Edge as well.
	 *
	 * @param from the element to link from
	 * @param to   the element to link to
	 * @param type the type of the relation
	 * @return the created edge
	 * @throws IllegalStateException if one of the elements is not in the model
	 */
	public RelationEdge<KnowledgeElement> link(KnowledgeElement from, KnowledgeElement to, RelationType type)
			throws IllegalStateException {
		if (!contains(from) || !contains(to)) {
			throw new IllegalStateException("One of the elements is not in the model!");
		}
		var newEdge = new RelationEdge<>(from, to, type);
		if (!graph.addEdge(from, to, newEdge)) {
			logger.info("Edge was already linked from {} to {}.", from.getId(), to.getId());
		}
		var reverseEdge = new RelationEdge<>(to, from, type.getInverted(), newEdge);
		if (!graph.addEdge(to, from, reverseEdge)) {
			logger.info("Edge was already linked from {} to {}.", to.getId(), from.getId());
		}
		return newEdge;
	}

	/**
	 * Removes the given element from the model.
	 *
	 * @param element the element to remove
	 * @return true if the element was removed, false if not
	 */
	public boolean remove(KnowledgeElement element) {
		return graph.removeVertex(element);
	}

	/**
	 * Tries to complete all relations that were not completed when the elements were added.
	 */
	public void tryCompleteLinking() {
		var completed = new ArrayList<Relation>();
		for (var entry : unfinishedRelations.entrySet()) {
			var relation = entry.getKey();
			var fromId = relation.getFromId();
			var toId = relation.getToId();
			if (contains(fromId) && contains(toId)) {
				var from = get(fromId);
				var to = get(toId);
				link(from, to, relation.getType());
				// set reverse relation
				var reverseType = relation.getType().getInverted();
				if (to.getRelations().stream().noneMatch(it -> it.getToId().equals(fromId)
															   && it.hasType(reverseType))) {
					to.addRelation(RelationGenerator.create(reverseType, to, from));
				}
				checkRelations(from);
				checkRelations(to);
				completed.add(relation);
			}
		}
		completed.forEach(unfinishedRelations.keySet()::remove);
	}

	/**
	 * Try to complete incomplete relations for the given element.
	 *
	 * @param element the element to check
	 */
	private void checkRelations(KnowledgeElement element) {
		for (var relation : element.getRelations()) {
			if (relation.isIncomplete()) {
				try {
					relation.getFrom();
				} catch (IllegalStateException ignored) {
					relation.setFrom(element);
				}
				try {
					relation.getTo();
				} catch (IllegalStateException ignored) {
					var toId = relation.getToId();
					if (contains(toId)) {
						relation.setTo(get(toId));
					}
				}
			}
		}
	}

	private void updateStructure() {
		for (var elem : graph.vertexSet()) {
			var structureId = elem.getStructureId();
			if (structureId == null) {
				continue;
			}
			try {
				structure.getObjectById(structureId).linkElement(elem);
			} catch (NoSuchElementException e) {
				logger.warn("StructureObject {} is not part of the structure", structureId);
			}
		}
	}

	/**
	 * Returns all elements that are connected with the given structure object in the model.
	 *
	 * @param structureId    the id of the structure object
	 * @param includeSimilar if true, also search for elements that contain the given structureId in their own
	 *                       structureId
	 * @return a set of all elements that are connected with the given structure object
	 */
	private Set<KnowledgeNode> getKnowledgeNodesFor(String structureId, boolean includeSimilar,
			boolean similarWhenFound) {
		Set<KnowledgeNode> back = new HashSet<>();
		var hasStructureObject = hasStructureObject(structureId);
		if (hasStructureObject) {
			var structureObject = structure.getObjectById(structureId);
			var elements = structureObject.getLinkedElements();
			for (var element : elements) {
				back.add(getKnowledgeNode(element.getId()));
			}
		} else {
			logger.warn("No structure object with id: '{}' found.", structureId);
		}
		if (includeSimilar && (similarWhenFound || !hasStructureObject)) {
			logger.info("Include similar objects for structure object '{}'.", structureId);
			try {
				back.addAll(getKnowledgeNodesForSimilar(structureId));
			} catch (NoSuchElementException e) {
				logger.info("No similar object for structure object '{}' found.", structureId);
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
	 * It includes similar objects in the structure.
	 *
	 * @param structureId the id of the structure object
	 * @return a set of all elements that are connected with the given structure object.
	 */
	public Set<KnowledgeNode> getKnowledgeNodesIncludingSimilarFor(String structureId) {
		return getKnowledgeNodesFor(structureId, true, true);
	}

	private Set<KnowledgeNode> getKnowledgeNodesForSimilar(String structureId) throws NoSuchElementException {
		Set<KnowledgeNode> back = new HashSet<>();
		if (!hasStructureSimilar(structureId)) {
			throw new NoSuchElementException("No structure object with id " + structureId + "or similar found");
		} else {
			var similarObject = structure.getSimilarObjectById(structureId);
			var elements = similarObject.getLinkedElements();
			for (var element : elements) {
				back.add(getKnowledgeNode(element.getId()));
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
		if (!contains(elementId)) {
			throw new NoSuchElementException("No element with id " + elementId + " found");
		}
		var element = get(elementId);
		return getKnowledgeNode(element);
	}

	/**
	 * create a KnowledgeNode for a given KnowledgeElement
	 *
	 * @param element element you want
	 * @return a Collection of all elements and relations that are connected with the given element
	 * @throws NoSuchElementException if the element is not in the Model.
	 */
	public KnowledgeNode getKnowledgeNode(KnowledgeElement element) throws NoSuchElementException {
		if (!contains(element)) {
			throw new NoSuchElementException("No element: \"" + element + "\" found");
		}
		var structureObject = structure.getObjectById(element.getStructureId());
		var relatedElements = getRelatedElements(element);
		var relations = getAllRelations(element);
		return new KnowledgeNode(structureObject, element, relatedElements, relations);
	}

	private boolean hasStructureSimilar(String structureId) {
		return structure.containsSimilar(structureId);
	}

	//region setter/getter
	public KnowledgeFragment getRoot() {
		return structure.getRoot();
	}

	/**
	 * Returns a list of all ids of the elements in the model.
	 *
	 * @return a list of all ids of the elements in the model
	 */
	public List<String> getIDs() {
		return graph.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Set<KnowledgeElement> getElements() {
		return graph.vertexSet();
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeModel that)) return false;
		if (!version.equals(that.version)) return false;
		if (!graph.equals(that.graph)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = graph.hashCode();
		result = 31 * result + version.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	private record RelationIdPair(String fromId, String toId) {
	}
}

/**
 * A relation edge between two elements.
 * It contains the type of the relation.
 */
class RelationEdge<T extends KnowledgeElement> extends DefaultEdge {
	private final RelationType type;
	private       RelationEdge inverted;
	private       T            source;
	private       T            target;

	/**
	 * Creates a new RelationEdge with the given type and inverted edge.
	 *
	 * @param type     the type of the relation
	 * @param inverted the inverted edge
	 */
	public RelationEdge(RelationType type, RelationEdge inverted) {
		if (inverted == null) {
			throw new IllegalArgumentException("inverted must not be null");
		}
		if (inverted.type.getInverted() != type) {
			throw new IllegalArgumentException("inverted must not have an inverted edge");
		}
		inverted.setInverted(this);
		this.inverted = inverted;
		this.type = type;
	}

	RelationEdge() {
		this.type = RelationType.CUSTOM;
	}

	public RelationEdge(T source, T target, RelationType type) {
		this.source = source;
		this.target = target;
		this.type = type;
	}

	public RelationEdge(T source, T target, RelationType type, RelationEdge inverted) {
		this.source = source;
		this.target = target;
		this.type = type;
		this.inverted = inverted;
	}

	/**
	 * Creates a new RelationEdge with the given inverted edge.
	 *
	 * @param inverted the inverted edge
	 */
	public RelationEdge(RelationEdge inverted) throws IllegalArgumentException {
		if (inverted == null) {
			throw new IllegalArgumentException("inverted must not be null");
		}
		inverted.setInverted(this);
		this.inverted = inverted;
		this.type = inverted.getInverted().type;
	}

	/**
	 * Creates a new RelationEdge with the given type.
	 *
	 * @param type the type of the relation
	 */
	public RelationEdge(RelationType type) {
		this.type = type;
	}

	//region setter/getter
	public RelationEdge getInverted() {
		return inverted;
	}

	public void setInverted(RelationEdge inverted) {
		this.inverted = inverted;
	}

	public RelationType getRelation() {
		return type;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RelationEdge<?> that)) return false;

		if (type != that.type) return false;
		if (!Objects.equals(source, that.source)) return false;
		return Objects.equals(target, that.target);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (target != null ? target.hashCode() : 0);
		return result;
	}
}