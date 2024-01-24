package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class KnowledgeLeaf extends KnowledgeObject {

	protected KnowledgeLeaf() {
		super();
	}

	public KnowledgeLeaf(String id) {
		super(id);
	}

	@Override
	public String toString() {
		return "KnowledgeLeaf{" +
			   "linkedElements size=" + getLinkedElements().size() +
			   ", id='" + getName() + '\'' +
			   '}';
	}
}
