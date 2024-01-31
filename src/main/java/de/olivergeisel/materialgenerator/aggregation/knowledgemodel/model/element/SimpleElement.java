package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public abstract class SimpleElement extends KnowledgeElement {

	protected SimpleElement() {
		super();
	}

	protected SimpleElement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	protected SimpleElement(String content, String id, String type) {
		super(content, id, type);
	}
}
