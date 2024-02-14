package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

/**
 * Represents a fact in the knowledge graph. Facts are true statements about the world.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @since 0.2.0
 */
@Node
public class Fact extends SimpleElement {

	protected Fact() {
		super();
	}

	public Fact(String content, String id) {
		super(content, id, "FACT");
	}

	protected Fact(String content, String id, String type) {
		super(content, id, type);
	}

	public Fact(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}
}
