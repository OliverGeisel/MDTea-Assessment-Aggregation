package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
class OPALMaterialInfoTest {

	private OPALMaterialInfo    opalMaterialInfo;
	@Mock
	private Material            material;
	@Mock
	private CourseOrganizerOPAL organizer;

	@BeforeEach
	void setUp() {
		opalMaterialInfo = new OPALMaterialInfo("fileName", 12345678L, material, organizer);
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void fileName() {
		when(material.getName()).thenReturn("NAME OF MATERIAL");
		assertEquals("NAME OF MATERIAL", opalMaterialInfo.fileName());
	}

	@Test
	void materialType() {
		when(material.getTemplateType()).thenReturn(TemplateType.DEFINITION);
		assertEquals("DEFINITION", opalMaterialInfo.materialType());
	}

	@Test
	void createFile() {
		var engine = mock(TemplateEngine.class);
		var context = mock(Context.class);
		when(organizer.getContext()).thenReturn(context);
		when(engine.process((String) any(), any())).thenReturn("htmlString");
		when(organizer.getTemplateEngine()).thenReturn(engine);
		when(material.getTemplateType()).thenReturn(TemplateType.DEFINITION);
		when(material.getName()).thenReturn("NAME OF MATERIAL");

		Path dir = null;
		File dirFile;
		try {
			dir = Files.createTempDirectory("test");
			dirFile = dir.toFile();
			opalMaterialInfo.createFile(dirFile);
			verify(context).setVariable("material", material);
			verify(context).setVariable("title", "fileName");
			assertTrue(dirFile.listFiles().length > 0);
			assertEquals("htmlString", Files.readString(dir.resolve("NAME OF MATERIAL.html")), "File must exist and "
																							   + "contain content");
		} catch (IOException e) {
			fail("Could not create temp directory");
		} finally {
			if (dir != null) {
				dir.toFile().delete();
			}
		}
	}

	@Test
	void find() {
		assertNull(opalMaterialInfo.find(null));
	}

	@Test
	void isValid() {
		assertFalse(opalMaterialInfo.isValid());
	}

	@Test
	void getMaterialName() {
		assertEquals("fileName", opalMaterialInfo.getMaterialName());
	}

	@Test
	void getNodeId() {
		assertEquals(12345678L, opalMaterialInfo.getNodeId());
	}

	@Test
	void getOriginalMaterial() {
		assertEquals(material, opalMaterialInfo.getOriginalMaterial());
	}
}