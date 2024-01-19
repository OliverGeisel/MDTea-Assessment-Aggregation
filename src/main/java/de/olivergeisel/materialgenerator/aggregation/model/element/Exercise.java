package de.olivergeisel.materialgenerator.aggregation.model.element;

import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public class Exercise extends ComplexElement {
	public Exercise(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Exercise(String content, String id, String type) {
		super(content, id, type);
	}
}
