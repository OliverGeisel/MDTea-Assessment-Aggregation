package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

/**
 * A CourseOrder is a collection of {@link CourseChapter}s.
 */
public class CourseOrder {

	private List<CourseChapter> chapters;

	public boolean add(CourseChapter chapter) {
		return chapters.add(chapter);
	}


}
