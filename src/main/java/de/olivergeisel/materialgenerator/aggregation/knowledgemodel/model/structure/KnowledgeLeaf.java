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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeLeaf)) return false;
		return super.equals(o);
	}

	@Override
	public String toString() {
		return STR."KnowledgeLeaf{linkedElements size=\{getLinkedElements().size()}, id='\{getName()}'}";
	}
}
