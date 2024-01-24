package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Code;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class CodeMaterialTest {

	private CodeMaterial code;
	@Mock
	private Code         codeElement;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(codeElement.getStructureId()).thenReturn("StrukturId");
		when(codeElement.getId()).thenReturn("id");
		code = new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] args){}}", "Test.java",
				codeElement);
	}

	@Test
	void createEmptyOk() {
		var newCode = new CodeMaterial();
	}

	@Test
	void createWithAllOk() {
		var newCode = new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] args){}}", "Test"
																												+ ".java",
				codeElement);
	}

	@Test
	void createWithNullLanguage() {
		assertThrows(IllegalArgumentException.class,
				() -> new CodeMaterial(null, "public class Test{\npublic static void main(String[] args){}}",
						"Test.java",
						codeElement));
	}

	@Test
	void createWithNullCode() {
		assertThrows(IllegalArgumentException.class, () -> new CodeMaterial("JAVA", null, "Test.java",
				codeElement));
	}

	@Test
	void createWithNullTitle() {
		var newCode = new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] args){}}", null,
				codeElement);
	}

	@Test
	void createWithNullElement() {
		assertThrows(NullPointerException.class,
				() -> new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] "
											   + "args){}}", "Test.java",
						null));
	}

	@Test
	void checkReplaceNewLine() {
		var newCode =
				new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] args){}}", "Test.java",
						codeElement);
		assertEquals("public class Test{<br>public static void main(String[] args){}}", newCode.getCode());
	}

	@Test
	void checkNotReplaceTab() {
		var newCode = new CodeMaterial("JAVA", """
				public class Test{
					public static void main(String[] args){}
				}""",
				"Test.java",
				codeElement);
		assertEquals("public class Test{<br>\tpublic static void main(String[] args){}<br>}", newCode.getCode());
	}

	@Test
	void checkReplaceTab() {
		var newCode = new CodeMaterial("JAVA", """
				public class Test{
				\tpublic static void main(String[] args){}
				}""",
				"Test.java",
				codeElement);
		assertEquals("public class Test{<br>\tpublic static void main(String[] args){}<br>}", newCode.getCode());
	}

	@Test
	void shortNameOk() {
		var newCode =
				new CodeMaterial("JAVA", "public class Test{\npublic static void main(String[] args){}}", "Test.java",
						codeElement);
		assertEquals("Code: Test.java", newCode.shortName());
	}

	@Test
	void getTitle() {
		assertEquals("Test.java", code.getTitle());
	}

	@Test
	void setTitle() {
		code.setTitle("Test2.java");
		assertEquals("Test2.java", code.getTitle());
	}

	@Test
	void getLanguage() {
		assertEquals("JAVA", code.getLanguage());
	}

	@Test
	void setLanguage() {
		code.setLanguage("C++");
		assertEquals("C++", code.getLanguage());
	}

	@Test
	void getCode() {
		assertEquals("public class Test{<br>public static void main(String[] args){}}", code.getCode());
	}

	@Test
	void setCode() {
		code.setCode("public class Test2{\npublic static void main(String[] args){}}");
		assertEquals("public class Test2{\npublic static void main(String[] args){}}", code.getCode());
	}

	@Test
	void testToString() {
		assertEquals("CodeMaterial{title='Test.java', code='public cla...', language='JAVA'}", code.toString());
	}
}