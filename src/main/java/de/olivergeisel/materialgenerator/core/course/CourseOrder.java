package de.olivergeisel.materialgenerator.core.course;

import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.List;

/**
 * A RawCourseOrder is a collection of {@link CourseChapter}s.
 *
 * @see CourseChapter
 * @see Course
 * @see Material
 *
 * @since 0.2.0
 * @version 1.1.0
 * @author Oliver Geisel
 */
public class CourseOrder {

	private List<CourseChapter> chapters;

	public boolean add(CourseChapter chapter) {
		return chapters.add(chapter);
	}
}
