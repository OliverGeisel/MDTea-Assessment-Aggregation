package de.olivergeisel.materialgenerator.core.courseplan.structure;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;

public class PotenzialKnowledgeObject extends KnowledgeObject {

	private final String name;

	public PotenzialKnowledgeObject(String name) {
		super(name);
		this.name = name;
	}
}
