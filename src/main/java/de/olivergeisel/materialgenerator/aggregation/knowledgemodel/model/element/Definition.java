package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

/**
 * A Definition is a simple element that has a content and a type.
 * The content is the definition itself and the type is the type of the definition.
 *
 * @author Oliver Geisel
 * @version 0.2.0
 * @see SimpleElement
 * @see KnowledgeElement
 * @see Relation
 * @since 0.2.0
 */
@Node
public class Definition extends SimpleElement {

	protected Definition() {
		super();
	}

	public Definition(String content, String id, Collection<Relation> relations) {
		super(content, id, "DEFINITION", relations);
	}

	protected Definition(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Definition(String content, String id) {
		super(content, id, "DEFINITION");
	}


}
