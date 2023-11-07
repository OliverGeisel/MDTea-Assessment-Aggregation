package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
		relevance = parts.stream().map(StructureElement::getRelevance).max(Enum::compareTo).orElseThrow();
		return relevance;
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
//endregion

	@Override
	public String toString() {
		return "StructureChapter{" + "name=" + getName() + ", parts=" + parts.size() + ", weight=" + weight
			   + ", relevance=" + relevance + '}';
	}


}
