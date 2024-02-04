package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("Unit")
class DownloadManagerUnitTest {

	private DownloadManager manager;

	@Mock
	private ZipService    zipService;
	@Mock
	private OPAL_Exporter opalExporter;
	@Mock
	private HTML_Exporter htmlExporter;
	@Mock
	private RawCourse     course;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		try {
			when(zipService.createTempDirectory()).thenReturn(new File("tempDir"));
			when(zipService.createZipArchive(any(), any())).thenReturn(new File("zipFile"));
		} catch (Exception e) {
			fail("Exception in init was thrown");
		}
		manager = new DownloadManager(zipService, htmlExporter, opalExporter);
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void createCourseInZipWithNullExporter() {
		assertThrows(IllegalArgumentException.class,
				() -> manager.createCourseInZip("TestCourse", "TestTemplate", null, null, null));
	}

	@Test
	void createCourseInZipWithNullName() {
		assertThrows(IllegalArgumentException.class,
				() -> manager.createCourseInZip(null, "TestTemplate", null, null, htmlExporter));
	}

	@Test
	void createCourseInZipWithNullTemplateSetName() {
		assertThrows(IllegalArgumentException.class,
				() -> manager.createCourseInZip("TestCourse", null, null, null, htmlExporter));
	}

	@Test
	void createCourseInZipWithNullCourse() {
		assertThrows(IllegalArgumentException.class,
				() -> manager.createCourseInZip("TestCourse", "TestTemplate", null, null, htmlExporter));
	}

	@Test
	void createCourseInZipWithOPAL() {
		var response = new MockHttpServletResponse();
		manager.createCourseInZip("TestCourse", "TestTemplate", course, response, opalExporter);
		try {
			verify(opalExporter).export(any(), any(), any());
		} catch (Exception e) {
			fail("Exception thrown");
		}

	}

	@Test
	void createCourseInZipWithHTML() {
		var response = new MockHttpServletResponse();
		manager.createCourseInZip("TestCourse", "TestTemplate", course, response, htmlExporter);
		try {
			verify(htmlExporter).export(any(), any(), any());
		} catch (Exception e) {
			fail("Exception thrown");
		}
	}

	@Test
	@Disabled("Old deprecated method")
	void createSingle() {
	}

	@Test
	void createAndDownloadWithUnsupportedExporterKind() {
		var response = new MockHttpServletResponse();
		var course = mock(RawCourse.class);
		assertThrows(IllegalArgumentException.class, () -> manager.createAndDownload(DownloadManager.ExportKind.PDF,
				"TestCourse", course, "TestTemplate", response));
	}

	@Test
	void createAndDownload() {
		var response = new MockHttpServletResponse();
		manager.createAndDownload(DownloadManager.ExportKind.HTML, "TestCourse", course, "TestTemplate", response);
		assertEquals("application/zip", response.getContentType());
		assertEquals("attachment; filename=TestCourse.zip", response.getHeader("Content-Disposition"));
	}
}