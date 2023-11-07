package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;

public abstract class TermElement extends KnowledgeElement {
	protected TermElement(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	protected TermElement(String content, String id, String type) {
		super(content, id, type);
	}
}
