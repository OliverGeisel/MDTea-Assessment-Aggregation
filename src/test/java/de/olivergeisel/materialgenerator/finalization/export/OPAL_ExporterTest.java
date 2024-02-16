package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.export.opal.OPAL_Exporter;
import de.olivergeisel.materialgenerator.finalization.parts.CourseMetadataFinalization;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OPAL_ExporterTest {

	private OPAL_Exporter opalExporter;

	@BeforeEach
	void setUp() {
		var imageService = mock(ImageService.class);
		opalExporter = new OPAL_Exporter(imageService);
	}

	@Test
	void exportCatchNull() {
		var templateSet = "templateSet";
		var rawCourse = mock(RawCourse.class);
		var directory = mock(java.io.File.class);
		assertThrows(IllegalArgumentException.class, () -> opalExporter.export(null, null, null));
		assertThrows(IllegalArgumentException.class, () -> opalExporter.export(null, templateSet, directory));
		assertThrows(IllegalArgumentException.class, () -> opalExporter.export(rawCourse, null, directory));
		assertThrows(IllegalArgumentException.class, () -> opalExporter.export(rawCourse, templateSet, null));
	}

	@Test
	void exportBasic() throws IOException {
		var order = mock(RawCourseOrder.class);
		when(order.getChapterOrder()).thenReturn(List.of());

		var templateSet = "templateSet";
		var rawCourse = mock(RawCourse.class);
		var courseMeta = new CourseMetadataFinalization("title", "2024", "easy", "University", "description", Map.of());
		when(rawCourse.getMeta()).thenReturn(courseMeta);
		when(rawCourse.getMetadata()).thenReturn(courseMeta);
		when(rawCourse.getTemplateName()).thenReturn(templateSet);
		when(rawCourse.getOrder()).thenReturn(order);
		var directoryPath = Files.createTempDirectory("test");
		var directory = directoryPath.toFile();
		try {
			opalExporter.export(rawCourse, templateSet, directory);

			var layoutDirectory = new File(directory, "layout");
			assertTrue(layoutDirectory.exists(), "Layout directory should exist");
			assertTrue(layoutDirectory.isDirectory(), "Layout directory should be a directory");

			var courseDirectory = new File(directory, "coursefolder");
			assertTrue(courseDirectory.exists(), "Course directory should exist");
			assertTrue(courseDirectory.isDirectory(), "Course directory should be a directory");

			var exportDirectory = new File(directory, "export");
			assertTrue(exportDirectory.exists(), "Export directory should exist");
			assertTrue(exportDirectory.isDirectory(), "Export directory should be a directory");
			var repoFile = new File(exportDirectory, "repo.xml");
			assertTrue(repoFile.exists(), "Repo file should exist");
			var learningGroupFile = new File(exportDirectory, "learninggroupexport.xml");
			assertTrue(learningGroupFile.exists(), "Learning group file should exist");
			var rigthGroupFile = new File(exportDirectory, "rightgroupexport.xml");
			assertTrue(rigthGroupFile.exists(), "Right group file should exist");
			// root files
			var backupFile = new File(directory, "__backup_context__.json");
			assertTrue(backupFile.exists(), "Course file should exist");
			var rulesFile = new File(directory, "course_rules.xml");
			assertTrue(rulesFile.exists(), "Rules file should exist");
			var configFiles = new File(directory, "CourseConfig.xml");
			assertTrue(configFiles.exists(), "Config files should exist");
			var treeFile = new File(directory, "editortreemodel.xml");
			assertTrue(treeFile.exists(), "Tree file should exist");
			var runFile = new File(directory, "runstructure.xml");
			assertTrue(runFile.exists(), "Run file should exist");
		} catch (IOException e) {
			fail("Should not throw an exception");
		} finally {
			directory.delete();
		}
	}
}