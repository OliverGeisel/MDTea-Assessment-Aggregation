package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

/**
 * A node element is a knowledge element that has children. It contains a collection of relations to other elements.
 * Not used at Moment.
 * Todo implement
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see SimpleElement
 * @since 0.2.0
 */
@Node
@Deprecated
public class NodeElement extends KnowledgeElement {

	protected NodeElement() {
		super();
	}
	public NodeElement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public NodeElement(String content, String id, String type) {
		super(content, id, type);
	}
}
