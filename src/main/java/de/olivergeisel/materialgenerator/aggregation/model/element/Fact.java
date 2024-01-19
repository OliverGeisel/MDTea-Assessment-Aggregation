package de.olivergeisel.materialgenerator.aggregation.model.element;

import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public class Fact extends SimpleElement {
	public Fact(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Fact(String content, String id, String type) {
		super(content, id, type);
	}
}
