package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A part of the {@link CourseStructure}. Use the Composite Pattern to build a {@link CourseStructure}.
 * This class is a composite role of the Composite Pattern.
 * It should be contained in a {@link StructureChapter} or another {@link StructureGroup}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see CourseStructure
 * @see StructureChapter
 * @see StructureElementPart
 * @since 1.0.0
 */
public class StructureGroup extends StructureElementPart {

	private final List<StructureElementPart> parts;

	public StructureGroup(ContentTarget topic, Relevance relevance, String name, Collection<String> alternatives) {
		super(topic, relevance, name, alternatives);
		parts = new ArrayList<>();
	}

	protected StructureGroup() {
		parts = new ArrayList<>();
	}

	@Override
	public Relevance updateRelevance() {
		relevance = parts.stream().map(StructureElement::getRelevance).max(Enum::compareTo).orElseThrow();
		return relevance;
	}

	@Override
	public StructureElement findByAlias(String alias) {
		var foundThis = super.findByAlias(alias);
		if (foundThis != null) return foundThis;
		for (StructureElement element : parts) {
			var found1 = element.findByAlias(alias);
			if (found1 != null) return found1;
		}
		return null;
	}

	public boolean add(StructureElementPart element) throws IllegalArgumentException {
		if (element == this || contains(element)) {
			return false;
		}
		return parts.add(element);
	}

	public boolean contains(StructureElementPart element) {
		for (StructureElement part : parts) {
			if (part instanceof StructureGroup group && group.contains(element))
				return true;
			else {
				if (part.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	public int size() {
		int back = 0;
		for (StructureElement element : parts) {
			if (element instanceof StructureGroup group) {
				back += group.size();
			} else {
				back++;
			}
		}
		return back;
	}

	//region setter/getter
	public List<StructureElementPart> getParts() {
		return parts;
	}
//endregion

	@Override
	public String toString() {
		return STR."StructureGroup{name=\{getName()}, parts=\{parts}, relevance=\{relevance}}";
	}
}
