package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.aggregation.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public interface KnowledgeModel<T> {

	/**
	 * Adds an element to the model.  Link the element to all existing elements that are described in relations.
	 * If the element is already in the model, nothing happens.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not (because it was already in the model)
	 */
	boolean addKnowledge(KnowledgeElement element);

	/**
	 * Adds a collection of elements to the model. Link all new elements with all elements that are already in the
	 * model
	 * and described in the relations.
	 *
	 * @param elements the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	boolean addKnowledge(Collection<KnowledgeElement> elements);

	/**
	 * Adds a source to the model.
	 *
	 * @param sources the source to add
	 * @return true if the source was added, false if not
	 */
	boolean addSource(KnowledgeSource sources);

	/**
	 * Adds a collection of KnowledgeObjects to the model.
	 *
	 * @param sources the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	boolean addSource(Collection<KnowledgeSource> sources);


	/**
	 * Adds a structure to the given structure element.
	 *
	 * @param structure the structure to add
	 * @param partToAdd the part to add to the structure
	 * @return true if the structure was added, false if not
	 */
	boolean addStructureTo(KnowledgeFragment structure, KnowledgeObject partToAdd);


	boolean addStructureToRoot(KnowledgeObject object);

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param element the element to check
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the element was null
	 */
	boolean containsElement(KnowledgeElement element) throws IllegalArgumentException;

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param elementId the id of the element
	 * @return true if the model contains the element, false if not
	 * @throws IllegalArgumentException if the elementId was null
	 */
	boolean containsElement(String elementId) throws IllegalArgumentException;

	/**
	 * Returns all elements that connected with the given element in the model.
	 *
	 * @param elementId the id of the element
	 * @return a set of all elements that are connected with the given element.
	 * @throws NoSuchElementException if no element with the given id was found
	 */
	Set<KnowledgeElement> findRelatedElements(String elementId) throws NoSuchElementException;


	/**
	 * Returns the element that connected with the given element in the model.
	 *
	 * @param id the id of the element
	 * @return the element that is connected with the given element
	 */
	Optional<KnowledgeElement> findElementById(String id);


	/**
	 * Links the given elements with the given relation.
	 *
	 * @param from     the element to link from
	 * @param to       the element to link to
	 * @param relation the relation to link with
	 * @return true if the elements were linked, false if not
	 */
	T link(KnowledgeElement from, KnowledgeElement to, RelationType relation);

	/**
	 * Removes the given element from the model.
	 *
	 * @param element the element to remove
	 * @return true if the element was removed, false if not
	 */
	boolean remove(KnowledgeElement element) throws IllegalArgumentException;


	KnowledgeNode getKnowledgeNode(String elementId);

	KnowledgeNode getKnowledgeNode(KnowledgeElement element) throws NoSuchElementException;

	Set<KnowledgeNode> getKnowledgeNodesIncludingSimilarFor(String structureId);
}
