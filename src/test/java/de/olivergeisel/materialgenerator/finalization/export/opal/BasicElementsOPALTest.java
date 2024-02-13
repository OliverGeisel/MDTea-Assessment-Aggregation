package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
class BasicElementsOPALTest {


	private Document document;

	@BeforeEach
	void setUp() {
		document = BasicElementsOPAL.newDocument();
	}

	@Test
	void newDocumentDifferent() {
		var doc1 = BasicElementsOPAL.newDocument();
		var doc2 = BasicElementsOPAL.newDocument();
		assertNotEquals(doc1, doc2, "newDocument should create different documents");
	}

	@Test
	void elementWithTextContentIsNull() {
		var text = BasicElementsOPAL.elementWithText(document, "test", null);
		assertEquals("test", text.getTagName(), "Tagname is wrong");
		assertNull(text.getTextContent(), "Textcontent should be null");
	}

	@Test
	void elementWithTextTypeNull() {
		assertThrows(DOMException.class, () -> BasicElementsOPAL.elementWithText(document, null, "test"));
	}

	@Test
	void elementWithTextEmptyText() {
		var element = BasicElementsOPAL.elementWithText(document, "test", "");
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertTrue(element.getTextContent().isEmpty(), "Textcontent should be empty");
	}

	@Test
	void elementWithText() {
		var element = BasicElementsOPAL.elementWithText(document, "test", "test");
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertEquals("test", element.getTextContent(), "Textcontent is wrong");
	}

	@Test
	void testElementWithTextBooleanFalse() {
		var element = BasicElementsOPAL.elementWithText(document, "test", false);
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertEquals("false", element.getTextContent(), "Textcontent is wrong");
	}

	@Test
	void testElementWithTextBooleanTrue() {
		var element = BasicElementsOPAL.elementWithText(document, "test", true);
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertEquals("true", element.getTextContent(), "Textcontent is wrong");
	}

	@Test
	void testElementWithTextNumber() {
		var element = BasicElementsOPAL.elementWithText(document, "test", 1);
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertEquals("1", element.getTextContent(), "Textcontent is wrong");
	}

	@Test
	void emptyElementOkay() {
		var element = BasicElementsOPAL.emptyElement(document, "test");
		assertEquals("test", element.getTagName(), "Tagname is wrong");
		assertFalse(element.hasChildNodes(), "Element should have no children");
	}

	@Test
	void emptyElementNull() {
		assertThrows(DOMException.class, () -> BasicElementsOPAL.emptyElement(document, null));
	}

	@Test
	void emptyElementEmpty() {
		assertThrows(DOMException.class, () -> BasicElementsOPAL.emptyElement(document, ""));
	}

	@Test
	void testCreateEntryText() {
		var entry = BasicElementsOPAL.createEntry(document, "KEY", "VALUE");
		assertEquals("entry", entry.getTagName(), "Tagname is wrong");
		assertEquals(2, entry.getChildNodes().getLength(), "Entry should have 2 children");
		assertEquals("wordform", entry.getChildNodes().item(0).getNodeName(), "NodeName is wrong");
		assertEquals("wordform", entry.getChildNodes().item(1).getNodeName(), "NodeName is wrong");
		assertEquals("KEY", entry.getChildNodes().item(0).getTextContent(), "Textcontent is wrong");
		assertEquals("VALUE", entry.getChildNodes().item(1).getTextContent(), "Textcontent is wrong");
	}

	@Test
	void testCreateEntryBoolean() {
		var entry = BasicElementsOPAL.createEntry(document, "KEY", true);
		assertEquals("entry", entry.getTagName(), "Tagname is wrong");
		assertEquals(2, entry.getChildNodes().getLength(), "Entry should have 2 children");
		assertEquals("wordform", entry.getChildNodes().item(0).getNodeName(), "NodeName is wrong");
		assertEquals("boolean", entry.getChildNodes().item(1).getNodeName(), "NodeName is wrong");
		assertEquals("KEY", entry.getChildNodes().item(0).getTextContent(), "Textcontent is wrong");
		assertEquals("true", entry.getChildNodes().item(1).getTextContent(), "Textcontent is wrong");
	}

	@Test
	void testCreateEntryNumber() {
		var entry = BasicElementsOPAL.createEntry(document, "KEY", 1);
		assertEquals("entry", entry.getTagName(), "Tagname is wrong");
		assertEquals(2, entry.getChildNodes().getLength(), "Entry should have 2 children");
		assertEquals("wordform", entry.getChildNodes().item(0).getNodeName(), "NodeName is wrong");
		assertEquals("int", entry.getChildNodes().item(1).getNodeName(), "NodeName is wrong");
		assertEquals("KEY", entry.getChildNodes().item(0).getTextContent(), "Textcontent is wrong");
		assertEquals("1", entry.getChildNodes().item(1).getTextContent(), "Textcontent is wrong");
	}

	@Test
	void createDefaultModuleConfiguration() {
		var moduleConfig = BasicElementsOPAL.createDefaultModuleConfiguration(document);
		assertEquals("moduleConfiguration", moduleConfig.getTagName(), "Tagname is wrong");

		var config = (Element) moduleConfig.getChildNodes().item(0);
		assertEquals("config", config.getTagName(), "Tagname is wrong");
		assertEquals(8, config.getChildNodes().getLength(), "Config should have 8 children");

	}

	@Test
	void escapeTags() {
		assertEquals("test", BasicElementsOPAL.escapeTags("test"), "No tags to escape");
		assertEquals("test&lt;test&gt;", BasicElementsOPAL.escapeTags("test<test>"), "Tags escaped");
	}

}