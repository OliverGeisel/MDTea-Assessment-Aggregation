package de.olivergeisel.materialgenerator.finalization.export.opal;


import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.course.CourseOrder;
import de.olivergeisel.materialgenerator.core.course.Meta;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;

/**
 * Representation of a Course for the LMS OPAL.
 */
public class CourseOPAL extends Course {


	public CourseOPAL() {

	}

	public static CourseOPAL fromRawCourse(RawCourse rawCourse) {
		return null;
	}

//region setter/getter
	@Override
	public Meta getMeta() {
		return null;
	}

	@Override
	public CourseOrder getOrder() {
		return null;
	}
//endregion
}
