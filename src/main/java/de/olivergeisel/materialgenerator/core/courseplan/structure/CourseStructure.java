package de.olivergeisel.materialgenerator.core.courseplan.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseStructure {
	private final List<StructureChapter> order = new ArrayList<>();

	public boolean add(StructureChapter element) {
		return order.add(element);
	}

	//region setter/getter
	public List<StructureChapter> getOrder() {
		return Collections.unmodifiableList(order);
	}
//endregion

	@Override
	public String toString() {
		return STR."CourseStructure{chapters=\{order.size()}}";
	}

}
