package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.Collection;


/**
 * A part of a {@link StructureElement} that can be used to build a {@link StructureElement}.
 * It can be a leaf or a composite of the Composite Pattern. It should be contained in a {@link StructureChapter} or a {@link StructureGroup}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see StructureElement
 * @see StructureChapter
 * @see StructureGroup
 * @since 1.0.0
 */
public abstract class StructureElementPart extends StructureElement {

	protected StructureElementPart(ContentTarget topic, Relevance relevance, String name,
			Collection<String> alternatives) {
		super(topic, relevance, name, alternatives);
	}

	protected StructureElementPart() {
		super();
	}
}
