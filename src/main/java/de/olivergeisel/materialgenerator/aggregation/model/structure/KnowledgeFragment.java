package de.olivergeisel.materialgenerator.aggregation.model.structure;


import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;

import java.util.*;

public class KnowledgeFragment extends KnowledgeObject {

	private final List<KnowledgeObject> children = new ArrayList<>();
	private       String                name;

	public KnowledgeFragment(String name) {
		this(name, null);
	}

	public KnowledgeFragment(String name, KnowledgeObject part) {
		super(name);
		this.name = name;
		if (part == null) {
			return;
		}
		children.add(part);
	}

	public boolean containsSimilar(String structureId) {
		if (structureId == null) {
			return false;
		}
		if (getId().contains(structureId) || structureId.contains(getId())) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.containsSimilar(structureId)) {
				return true;
			} else {
				if (element.getId().contains(structureId) || structureId.contains(element.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean addObject(KnowledgeObject object) {
		if (object == null || this == object || children.contains(object)) {
			return false;
		}
		return children.add(object);
	}

	public boolean contains(KnowledgeObject object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.contains(object)) {
				return true;
			} else {
				if (element.equals(object)) {
					return true;
				}
			}
		}
		return false;
	}

	public KnowledgeObject getSimilarObjectById(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getId())) {
			return this;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment) {
				try {
					return fragment.getSimilarObjectById(id);
				} catch (NoSuchElementException ignored) {
				}
			} else {
				if (element.getId().contains(id) || id.contains(element.getId())) {
					return element;
				}
			}
		}
		throw new NoSuchElementException("No element with id " + id + " found");
	}

	/**
	 * @param id id of the KnowledgeObject that should be included.
	 * @return {@literal true} if id is the this.getIdUnified or one element has the id.
	 * @throws NoSuchElementException if id is null
	 */
	public boolean contains(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getIdUnified())) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.contains(id)) {
				return true;
			} else {
				if (id.equals(element.getIdUnified())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the KnowledgeObject with the given id. If the id is not found, a NoSuchElementException is thrown.
	 *
	 * @param id the id of the object
	 * @return the object with the given id
	 * @throws IllegalArgumentException if id is null
	 * @throws NoSuchElementException   if no object with the given id is found
	 */
	public KnowledgeObject getObjectById(String id) throws IllegalArgumentException, NoSuchElementException {
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}
		if (id.equals(getId())) {
			return this;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment) {
				try {
					return fragment.getObjectById(id);
				} catch (NoSuchElementException ignored) {
					// no element found, continue
				}
			} else {
				if (element.getId().equals(id)) {
					return element;
				}
			}
		}
		throw new NoSuchElementException("No element with id " + id + " found");
	}

	public boolean removeObject(KnowledgeObject object) {
		if (!children.contains(object)) {
			return false;
		}
		return children.remove(object);
	}

	//region setter/getter
	@Override
	public Set<KnowledgeElement> getLinkedElements() {
		var ownElements = super.getLinkedElements();
		var back = new HashSet<>(ownElements);
		for (KnowledgeObject child : children) {
			back.addAll(child.getLinkedElements());
		}
		return back;
	}

	public List<KnowledgeObject> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion
}
