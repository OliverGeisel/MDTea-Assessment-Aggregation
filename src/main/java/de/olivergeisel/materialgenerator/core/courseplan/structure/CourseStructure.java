package de.olivergeisel.materialgenerator.core.courseplan.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The general structure of a course. It is a composite of {@link StructureChapter}.
 * Each chapter can contain {@link StructureElementPart} or {@link StructureGroup}.
 * Each group can contain many {@link StructureTask}s.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see StructureChapter
 * @see StructureElementPart
 * @see StructureGroup
 * @since 0.2.0
 */
public class CourseStructure {
	private final List<StructureChapter> order = new ArrayList<>();

	public boolean add(StructureChapter element) {
		return order.add(element);
	}

	//region setter/getter
	public boolean isValid() {
		return order.stream().allMatch(StructureChapter::isValid);
	}
	public List<StructureChapter> getOrder() {
		return Collections.unmodifiableList(order);
	}
//endregion

	@Override
	public String toString() {
		return STR."CourseStructure{chapters=\{order.size()}}";
	}

}
