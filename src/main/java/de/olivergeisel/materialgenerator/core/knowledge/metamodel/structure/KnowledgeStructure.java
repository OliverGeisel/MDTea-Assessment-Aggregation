package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import java.util.NoSuchElementException;

public class KnowledgeStructure {
	private final RootStructureElement root;

	public KnowledgeStructure() {
		root = new RootStructureElement();
	}

	public KnowledgeStructure(RootStructureElement root) {
		this.root = root;
	}

	public boolean addObjectToRoot(KnowledgeObject object) {
		if (object == null || root == object || root.contains(object)) {
			return false;
		}
		return root.addObject(object);
	}

	public boolean contains(KnowledgeObject object) {
		if (object == null) {
			return false;
		}
		if (object == root) {
			return true;
		}
		return root.contains(object);
	}

	public boolean contains(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		return root.contains(id.toUpperCase().trim().replace('_', ' ').replace('-', ' '));
	}

	/**
	 * Find the structure Object with the given id
	 *
	 * @param id id of the object to get
	 * @return the object with the given id
	 * @throws NoSuchElementException if no object with the given id exists
	 */
	public KnowledgeObject getObjectById(String id) throws NoSuchElementException {
		return root.getObjectById(id);
	}

	public boolean containsSimilar(String structureId) {
		return root.containsSimilar(structureId);
	}

	public KnowledgeObject getSimilarObjectById(String structureId) throws NoSuchElementException {
		return root.getSimilarObjectById(structureId);
	}


	//region setter/getter
	public RootStructureElement getRoot() {
		return root;
	}
//endregion

}
