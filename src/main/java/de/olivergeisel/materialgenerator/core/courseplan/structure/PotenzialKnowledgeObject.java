package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

public class PotenzialKnowledgeObject extends KnowledgeObject {

	private final String name;

	public PotenzialKnowledgeObject(String name) {
		super(name);
		this.name = name;
	}
}
