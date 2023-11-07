package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

public class KnowledgeLeaf extends KnowledgeObject {

	public KnowledgeLeaf(String id) {
		super(id);
	}

	@Override
	public String toString() {
		return "KnowledgeLeaf{" +
			   "linkedElements size=" + getLinkedElements().size() +
			   ", id='" + getId() + '\'' +
			   '}';
	}
}
