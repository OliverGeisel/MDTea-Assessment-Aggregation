package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node
public class KnowledgeFragment extends KnowledgeObject {

	@Relationship("CONTAINS")
	private List<KnowledgeObject> children = new ArrayList<>();

	protected KnowledgeFragment() {
		super();
	}

	private String name; // Todo fix this in super-class is already a getter fo the id

	public boolean containsSimilar(String structureId) {
		if (structureId == null) {
			return false;
		}
		if (getName().contains(structureId) || structureId.contains(getName())) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.containsSimilar(structureId)) {
				return true;
			} else {
				if (element.getName().contains(structureId) || structureId.contains(element.getName())) {
					return true;
				}
			}
		}
		return false;
	}

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

	public KnowledgeObject getSimilarObjectById(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getName())) {
			return this;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment) {
				try {
					return fragment.getSimilarObjectById(id);
				} catch (NoSuchElementException ignored) {
				}
			} else {
				if (element.getName().contains(id) || id.contains(element.getName())) {
					return element;
				}
			}
		}
		throw new NoSuchElementException(STR."No element with id \{id} found");
	}

	/**
	 * Adds the given object to the children of this object.
	 * If the object is null, this object or already a child of this object, nothing happens.
	 * @param object the object to add
	 * @return whether the object was added
	 */
	public boolean addObject(KnowledgeObject object) {
		if (object == null || this == object || children.contains(object)) {
			return false;
		}
		return children.add(object);
	}

	/**
	 * Checks if the given object is a child of this object. This is done recursively.
	 * So it also checks if the object exists anywhere in the children of the children.
	 * @param object the object to check
	 * @return whether the object is a child of this object
	 */
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

	/**
	 * Returns whether the given object is a direct child of this object.
	 *
	 * @param object the object to check
	 * @return whether the given object is a direct child of this object
	 */
	public boolean hasDirectChild(KnowledgeObject object) {
		return children.contains(object);
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
		if (id.equals(getName())) {
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
				if (element.getName().equals(id)) {
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
	 * Returns all linked elements of this object and its children. Depth 0 returns only the elements of this object, depth 1 returns the elements of this object and its children, etc.
	 * If depth is negative, only this object's elements are returned.
	 *
	 * @param depth the depth of the search (0 for this object only, 1 for this object and its children, etc.)
	 * @return all linked elements of this object and its children
	 */
	public Set<KnowledgeElement> getLinkedElements(int depth) {
		var ownElements = super.getLinkedElements();
		if (depth <= 0) {
			return super.getLinkedElements();
		}
		var back = new HashSet<>(ownElements);
		for (KnowledgeObject child : children) {
			if (child instanceof KnowledgeFragment fragment) {
				back.addAll(fragment.getLinkedElements(depth - 1));
			} else {
				back.addAll(child.getLinkedElements());
			}
		}
		return back;
	}

	public boolean removeObject(KnowledgeObject object) {
		if (!children.contains(object)) {
			return false;
		}
		return children.remove(object);
	}

	public void setChildren(List<KnowledgeObject> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	//region setter/getter
	@Override
	public Set<KnowledgeElement> getLinkedElements() {
		return getLinkedElements(0);
	}

	public List<KnowledgeObject> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeFragment fragment)) return false;
		if (super.equals(o)) return true;
		return children.equals(fragment.children) && name.equals(fragment.name);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + children.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return STR."KnowledgeFragment{name='\{name}', children=\{children}}";
	}
}
