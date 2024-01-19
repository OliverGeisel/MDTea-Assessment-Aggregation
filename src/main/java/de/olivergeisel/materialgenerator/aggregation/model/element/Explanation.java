package de.olivergeisel.materialgenerator.aggregation.model.element;

import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

/**
 * A simple element that represents an explanation. It is a {@link KnowledgeElement} with a content and a type.
 * Not implemented yet.
 * Todo implement
 *
 * @version 1.1
 * @see KnowledgeElement
 * @see SimpleElement
 * @since 1.0
 */
@Node
public class Explanation extends KnowledgeElement {


	public Explanation(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Explanation(String content, String id, String type) {
		super(content, id, type);
	}
}
