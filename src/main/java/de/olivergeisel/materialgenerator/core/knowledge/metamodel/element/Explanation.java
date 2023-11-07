package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;

public class Explanation extends KnowledgeElement {
	private String term;
	private String content;

	public Explanation(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Explanation(String content, String id, String type) {
		super(content, id, type);
	}
}
