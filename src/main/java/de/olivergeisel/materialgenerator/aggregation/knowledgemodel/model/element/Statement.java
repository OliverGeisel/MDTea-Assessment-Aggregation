package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public class Statement extends SimpleElement {

	protected Statement() {
		super();
	}

	public Statement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Statement(String content, String id, String type) {
		super(content, id, type);
	}
}
