package de.olivergeisel.materialgenerator.aggregation.model.structure;

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
