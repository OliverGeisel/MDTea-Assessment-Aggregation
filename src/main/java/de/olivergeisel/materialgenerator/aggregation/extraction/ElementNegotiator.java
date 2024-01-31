package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.collect.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A Negotiator is a class that extracts a list of {@link KnowledgeElement}s from a given source/{@link KnowledgeFragment}.
 * But the Elements <b>must not be</b> correct. The user must check the elements for correctness.
 * So the main purpose of a Negotiator is to extract the elements from a source/fragment and hold it until the user
 * is okay with it. Is all okay, the elements will be added to a {@link KnowledgeModel}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see KnowledgeModel
 * @since 1.1.0
 */
public class ElementNegotiator<T extends KnowledgeElement> implements Negotiator<KnowledgeElement> {

	private KnowledgeFragment fragment; // related fragment
	private List<T>           suggestedElements = new LinkedList<>();
	//  elements added by the extractor and suggested but not approved yet.
	private List<T>           acceptedElements  = new LinkedList<>(); //  elements added by user and approved

	public ElementNegotiator(KnowledgeFragment fragment) {this.fragment = fragment;}

	/**
	 * Move an element from the list of suggested elements to the list of accepted elements.
	 *
	 * @param element the element to approve
	 * @return true if the element was moved, false if the element is not in the list of suggested elements.
	 */
	public boolean approve(T element) {
		if (suggestedElements.contains(element)) {
			suggestedElements.remove(element);
			acceptedElements.add(element);
			return true;
		}
		return false;
	}

	public boolean hasElements() {
		return !isEmpty();
	}

	public boolean hasSuggestedElements() {
		return !suggestedElements.isEmpty();
	}

	public T findById(String id) {
		var suggested = suggestedElements.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
		if (suggested != null) {
			return suggested;
		}
		return acceptedElements.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
	}

	//region setter/getter

	/**
	 * Move an element from the list of suggested elements to the list of accepted elements.
	 *
	 * @param id the id of the element to approve
	 * @return true if the element was moved, false if the element is not in the list of suggested elements.
	 */
	public boolean approveById(String id) {
		return swapById(id, suggestedElements, acceptedElements);
	}

	private boolean swapById(String id, List<T> from, List<T> to) {
		if (id == null || id.isBlank()) {
			return false;
		}
		return from.stream().filter(it -> it.getId().equals(id)).findFirst().map(it -> {
			from.remove(it);
			to.add(it);
			return true;
		}).orElse(false);
	}

	/**
	 * Move an element from the list of accepted elements to the list of suggested elements.
	 *
	 * @param element the element to reject
	 * @return true if the element was moved, false if the element is not in the list of accepted elements.
	 */
	public boolean reject(T element) {
		if (acceptedElements.contains(element)) {
			acceptedElements.remove(element);
			suggestedElements.add(element);
			return true;
		}
		return false;
	}


	/**
	 * Move an element from the list of accepted elements to the list of suggested elements.
	 *
	 * @param id the id of the element to reject
	 * @return true if the element was moved, false if the element is not in the list of accepted elements.
	 */
	public boolean rejectById(String id) {
		return swapById(id, acceptedElements, suggestedElements);
	}

	/**
	 * Adds an element to the list of accepted elements.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if the element is already in the list of suggested or accepted elements.
	 */
	public <E extends T> boolean add(E element) {
		if (!suggestedElements.contains(element) && !acceptedElements.contains(element)) {
			acceptedElements.add(element);
			return true;
		}
		return false;
	}

	/**
	 * Adds a list of elements to the list of accepted elements.
	 *
	 * @param elements the elements to add
	 * @return true if at least one element was added, false if all elements are already in the list of suggested or accepted elements.
	 */
	public <E extends T> boolean addAll(Collection<E> elements) {
		boolean changed = false;
		for (var element : elements) {
			changed |= add(element);
		}
		return changed;
	}

	/**
	 * Adds an element to the list of suggested elements.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if the element is already in the list of suggested or accepted elements.
	 */
	public boolean suggest(T element) {
		if (!suggestedElements.contains(element) && !acceptedElements.contains(element)) {
			suggestedElements.add(element);
			return true;
		}
		return false;
	}

	/**
	 * Removes an element from the list of suggested or accepted elements.
	 *
	 * @param element the element to remove
	 * @return true if the element was removed, false if the element is not in the list of suggested or accepted elements.
	 */
	public boolean remove(T element) {
		if (suggestedElements.contains(element)) {
			suggestedElements.remove(element);
			return true;
		}
		if (acceptedElements.contains(element)) {
			acceptedElements.remove(element);
			return true;
		}
		return false;
	}

	public boolean removeById(String id) {
		if (id == null || id.isBlank()) {
			return false;
		}
		return suggestedElements.removeIf(it -> it.getId().equals(id))
			   || acceptedElements.removeIf(it -> it.getId().equals(id));
	}

	@Override
	public List<KnowledgeElement> extract() {
		return null;
	}
	public boolean isEmpty() {
		return suggestedElements.isEmpty() && acceptedElements.isEmpty();
	}
	public KnowledgeFragment getFragment() {
		return fragment;
	}

	public List<T> getSuggestedElements() {
		return suggestedElements;
	}

	public List<T> getAcceptedElements() {
		return acceptedElements;
	}
//endregion
}
