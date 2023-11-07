package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;

public class Definition extends SimpleElement {

	public Definition(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}

	public Definition(String content, String id, String type) {
		super(content, id, type);
	}
}
