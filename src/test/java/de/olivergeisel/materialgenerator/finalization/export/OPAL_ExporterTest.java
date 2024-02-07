package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.parts.CourseMetadataFinalization;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OPAL_ExporterTest {

	private OPAL_Exporter opalExporter;

	@BeforeEach
	void setUp() {
		opalExporter = new OPAL_Exporter();
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
	void export() throws IOException {
		var templateSet = "templateSet";
		var rawCourse = mock(RawCourse.class);
		var courseMeta = new CourseMetadataFinalization("title", "2024", "easy", "University", "description", Map.of());
		when(rawCourse.getMeta()).thenReturn(courseMeta);
		when(rawCourse.getMetadata()).thenReturn(courseMeta);
		var directory = File.createTempFile("test", "");
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
			var courseFile = new File(directory, "__backup_context.json");
			assertTrue(courseFile.exists(), "Course file should exist");
			var rulesFile = new File(directory, "course_rules.xml");
			assertTrue(rulesFile.exists(), "Rules file should exist");
			var configFiles = new File(directory, "CourseConfig");
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