package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		return "StructureGroup{" +
			   "name=" + getName() +
			   ", parts=" + parts +
			   ", relevance=" + relevance +
			   '}';
	}
}
