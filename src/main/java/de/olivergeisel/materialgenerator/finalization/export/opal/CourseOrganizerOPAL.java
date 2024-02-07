package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.export.ZipService;
import org.springframework.stereotype.Service;

/**
 * Holding all information for a course for the LMS OPAL. Over this method you export the Course
 */
@Service
public class CourseOrganizerOPAL {

	private final ZipService zipService;

	public CourseOrganizerOPAL(ZipService zipService) {this.zipService = zipService;}


}
