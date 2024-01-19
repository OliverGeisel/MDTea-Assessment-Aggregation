package de.olivergeisel.materialgenerator.aggregation.model.element;

import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public abstract class SimpleElement extends KnowledgeElement {
	protected SimpleElement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	protected SimpleElement(String content, String id, String type) {
		super(content, id, type);
	}
}
