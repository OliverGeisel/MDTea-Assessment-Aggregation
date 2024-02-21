package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.course.Meta;
import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.CourseMetadataFinalization;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourseOrder;
import lombok.AccessLevel;
import lombok.Getter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.*;

import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.createTemplateEngine;

/**
 * Holding all information for a course for the LMS OPAL. Over this method you export the Course
 */
public class CourseOrganizerOPAL extends Course {

	@Getter(AccessLevel.PACKAGE)
	private final TemplateEngine templateEngine;
	@Getter
	private final long           nodeId =
			new Random().nextLong(10_000_000_000_000_000L) + 1_000_000_000_000_000L;
	private final RawCourse      originalCourse;
	private final OpalOrderRaw   order;
	@Getter(AccessLevel.PACKAGE)
	private       Context        context = new Context(Locale.GERMANY);
	@Getter
	private boolean materialCreated = false;
	private long    runId           = nodeId + 1;

	public CourseOrganizerOPAL() {
		originalCourse = null;
		templateEngine = createTemplateEngine("color");
		order = new OpalOrderRaw();
	}

	public CourseOrganizerOPAL(RawCourse course) {
		originalCourse = course;
		var templateSet = course.getTemplateName();
		templateEngine = createTemplateEngine(templateSet);
		order = new OpalOrderRaw(course.getOrder(), this);
	}

	/**
	 * Create all Materials in the given directory
	 *
	 * @param targetDirectory the directory to create the materials in
	 * @throws IllegalStateException if the materials are already created
	 */
	public void creatMaterials(File targetDirectory) {
		if (materialCreated) {
			throw new IllegalStateException("Materials already created");
		}
		if (!targetDirectory.exists()) {
			targetDirectory.mkdirs();
		}
		order.createMaterials(targetDirectory);
		materialCreated = true;
	}

	//region setter/getter

	/**
	 * Returns the meta information of the course or a new empty one if the course is not set.
	 *
	 * @return meta information of the course.
	 */
	@Override
	public Meta getMeta() {
		if (originalCourse == null) {
			return new CourseMetadataFinalization("", "", "", "", "",Map.of());
		}
		return originalCourse.getMeta();
	}

	@Override
	public RawCourseOrder getOrder() {
		return order;
	}


	public RawCourse getOriginalCourse() {
		return originalCourse;
	}

	long getNextId() {
		return ++runId;
	}
//endregion

	static class OpalOrderRaw extends RawCourseOrder {

		private final List<OPALChapterInfo> chapters = new ArrayList<>();

		public OpalOrderRaw() {
		}

		public OpalOrderRaw(RawCourseOrder order, CourseOrganizerOPAL courseOrganizer) {
			for (var chapter : order.getChapterOrder()) {
				var newChapter = new OPALChapterInfo(chapter, courseOrganizer);
				chapters.add(newChapter);
			}
		}

		public void createMaterials(File targetDirectory) {
			for (var chapter : chapters) {
				chapter.createMaterials(targetDirectory);
			}
		}

		//region setter/getter
		@Override
		public List<ChapterOrder> getChapterOrder() {
			return Collections.unmodifiableList(chapters);
		}

		public List<OPALChapterInfo> getChapterInfos() {
			return new ArrayList<>(chapters);
		}
		//endregion
	}
}
