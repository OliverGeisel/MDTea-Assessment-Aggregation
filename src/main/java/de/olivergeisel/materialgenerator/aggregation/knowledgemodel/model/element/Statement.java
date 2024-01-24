package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;

import java.util.Collection;

public class Statement extends SimpleElement {
	public Statement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Statement(String content, String id, String type) {
		super(content, id, type);
	}
}
