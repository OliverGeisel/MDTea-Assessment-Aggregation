package de.olivergeisel.materialgenerator.core.course;

import de.olivergeisel.materialgenerator.generation.material.Material;

/**
 * A CourseTask is a {@link CourseChapterLeafPart} that contains a {@link Material}.
 * <p>
 * A CourseTask can not contain other CourseTasks.
 * A CourseTask can not contain itself.
 *
 * @see CourseChapterLeafPart
 * @see CourseElement
 * @see Material
 * @see CourseChapter
 * @see Course
 * @see CourseOrder
 * @see CompleteCourse
 */
public class CourseTask extends CourseChapterLeafPart {
	private Material material;

}
