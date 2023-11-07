package de.olivergeisel.materialgenerator.core.course;

/**
 * A Course is a collection of {@link CourseChapter}s.
 * <p>
 * A Course contains a {@link Meta} and a {@link CourseOrder}.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see CourseChapter
 * @see CourseElement
 * @since 0.2.0
 */
public abstract class Course {

//region setter/getter
	public abstract Meta getMeta();

	public abstract CourseOrder getOrder();
//endregion


}
