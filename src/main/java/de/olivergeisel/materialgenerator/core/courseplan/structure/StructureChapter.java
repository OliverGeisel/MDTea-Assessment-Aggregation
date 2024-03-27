package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A part of the {@link CourseStructure}. Use the Composite Pattern to build a {@link CourseStructure}.
 * This class is a component role of the Composite Pattern. It can only be contained in a {@link CourseStructure}.
 * Only {@link StructureElementPart} should be contained in a {@link StructureChapter}
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see CourseStructure
 * @see StructureElementPart
 * @see StructureGroup
 * @since 1.0.0
 */
public class StructureChapter extends StructureElement {

	private final List<StructureElementPart> parts = new ArrayList<>();
	private       double                     weight;

	/**
	 * Constructor for StructureChapter with a given weight and a list of alternatives and a given relevance
	 *
	 * @param target       ContentTarget of the chapter
	 * @param relevance    Relevance of the chapter
	 * @param name         Name of the chapter
	 * @param weight       Weight of the chapter
	 * @param alternatives List of alternatives for the chapter
	 */
	public StructureChapter(ContentTarget target, Relevance relevance, String name, double weight,
			Collection<String> alternatives) {
		super(target, relevance, name, alternatives);
		this.weight = weight;
	}

	/**
	 * Constructor for StructureChapter with a given weight and a list of alternatives
	 *
	 * @param target       ContentTarget of the chapter
	 * @param name         Name of the chapter
	 * @param weight       Weight of the chapter
	 * @param alternatives List of alternatives for the chapter
	 */
	public StructureChapter(ContentTarget target, String name, double weight, Collection<String> alternatives) {
		super(target, Relevance.TO_SET, name, alternatives);
		this.weight = weight;
	}

	protected StructureChapter() {
		super();
	}

	@Override
	public Relevance updateRelevance() {
		relevance = parts.stream().map(StructureElement::getRelevance).max(Enum::compareTo).orElse(Relevance.TO_SET);
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
		if (contains(element)) {
			return false;
		}
		return parts.add(element);
	}

	public boolean contains(StructureElementPart element) {
		for (StructureElement element1 : parts) {
			if (element1 instanceof StructureGroup group && group.contains(element)) return true;
			else {
				if (element1.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the number of Tasks in the chapter
	 *
	 * @return number of itemConfigurations in the chapter
	 */
	public int numberOfTasks() {
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

	/**
	 * Returns the number of groups in the chapter
	 *
	 * @return number of parts in the chapter
	 */
	public int size() {
		return parts.size();
	}

	//region setter/getter
	public List<StructureElementPart> getParts() {
		return parts;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
//endregion

	@Override
	public String toString() {
		return STR."StructureChapter{name=\{getName()}, parts=\{parts.size()}, weight=\{weight}, relevance=\{relevance}}";
	}


}
