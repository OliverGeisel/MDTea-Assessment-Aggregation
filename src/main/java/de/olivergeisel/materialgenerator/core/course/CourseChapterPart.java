package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

/**
 * A CourseChapterPart is a part of a CourseChapter.
 * <p>
 * A CourseChapterPart can be a {@link CourseChapterCollectionPart} or a {@link CourseChapterLeafPart}.
 * A CourseChapterPart can not contain itself.
 * A CourseChapterPart can not contain a CourseChapterCollectionPart that contains this CourseChapterPart.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 */
public abstract class CourseChapterPart extends CourseElement {}

/**
 * A CourseChapterCollectionPart is a collection of {@link CourseChapterPart}s.
 * <p>
 * A CourseChapterCollectionPart can contain other CourseChapterCollectionParts and {@link CourseChapterLeafPart}s.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 */
abstract class CourseChapterCollectionPart extends CourseChapterPart {
	protected List<CourseChapterPart> parts;

	public abstract boolean contains(CourseChapterPart part);
}

/**
 * A CourseChapterLeafPart is a leaf of a CourseChapter.
 * <p>
 * A CourseChapterLeafPart can not contain other CourseChapterParts.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see CourseChapterPart
 * @see CourseChapterCollectionPart
 * @see CourseGroup
 * @see CourseChapter
 * @since 0.2.0
 */
abstract class CourseChapterLeafPart extends CourseChapterPart {

}