package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.Collection;


public class StructureTask extends StructureElementPart {

	public StructureTask(ContentTarget topic, Relevance relevance, String name, Collection<String> alternatives) {
		super(topic, relevance, name, alternatives);

	}

	protected StructureTask() {
	}

	@Override
	public Relevance updateRelevance() {
		// Nothing to do!
		return relevance;
	}

	@Override
	public String toString() {
		return STR."StructureTask{name=\{getName()}, topic=\{getTopic()}, relevance=\{relevance}}";
	}


}
