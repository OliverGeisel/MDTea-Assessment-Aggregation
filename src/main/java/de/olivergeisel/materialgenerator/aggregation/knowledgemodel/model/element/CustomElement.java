package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public class CustomElement extends KnowledgeElement {
	private String name;

	protected CustomElement() {
		super();
	}

	protected CustomElement(String content, String id, String type, Collection<Relation> relations, String name) {
		super(content, id, type, relations);
		this.name = name;
	}

	protected CustomElement(String content, String id, String type, String name) {
		super(content, id, type);
		this.name = name;
	}

	//region setter/getter
	public String getName() {
		return name;
	}
//endregion
}
