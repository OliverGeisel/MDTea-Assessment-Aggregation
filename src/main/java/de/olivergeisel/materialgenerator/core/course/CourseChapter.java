package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

/**
 * A CourseChapter is a collection of {@link CourseChapterPart}s.
 * <p>
 * A CourseChapter can contain other CourseChapters.
 * A CourseChapter can not contain itself.
 *
 * @see CourseChapterPart
 * @see CourseElement
 */
public class CourseChapter extends CourseElement {

	private List<CourseChapterPart> parts;

	public boolean add(CourseChapterPart part) {
		return parts.add(part);
	}
}
