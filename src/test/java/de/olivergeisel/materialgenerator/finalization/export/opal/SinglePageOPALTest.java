package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
class SinglePageOPALTest {

	private SinglePageOPAL singlePageOPAL;

	private Document document;

	@BeforeEach
	void setUp() {
		singlePageOPAL = new SinglePageOPAL();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void createRunStructureOPAL_Normal() {
		var parent = "org.olat.course.nodes.STCourseNode";
		var realMaterial =
				new Material(MaterialType.WIKI, TemplateType.DEFINITION, "Test-Term", "TEST_ID", "STRUCTURE_ID");
		var material = new OPALMaterialInfo("Test", 1, realMaterial, null);

		var result = singlePageOPAL.createRunStructureOPAL(material, parent, document);

		assertEquals("org.olat.course.nodes.STCourseNode", result.getTagName(), "Tagname is wrong");
		assertTrue(result.getAttribute("class").isEmpty());
		// ident
		var ident = result.getChildNodes().item(0);
		assertEquals("ident", ident.getNodeName(), "NodeName is wrong");
		assertEquals("1", ident.getTextContent(), "Id is wrong");
		// parent
		var parentElement = (Element) result.getChildNodes().item(1);
		assertEquals("parent", parentElement.getNodeName(), "NodeName is wrong");
		assertFalse(parentElement.hasChildNodes(), "A parent-element has no children");
		assertEquals("org.olat.course.nodes.STCourseNode", parentElement.getAttribute("class"), "Class is wrong");
		assertEquals("../../..", parentElement.getAttribute("reference"), "Reference is wrong");
		// children
		var children = (Element) result.getChildNodes().item(2);
		assertEquals("children", children.getNodeName(), "NodeName is wrong");
		assertFalse(children.hasChildNodes(), "A children-element has no children");
		// type
		var type = result.getChildNodes().item(3);
		assertEquals("type", type.getNodeName(), "NodeName is wrong");
		assertEquals("sp", type.getTextContent(), "Text is wrong");
		// shortTitle
		var shortTitle = result.getChildNodes().item(4);
		assertEquals("shortTitle", shortTitle.getNodeName(), "NodeName is wrong");
		assertEquals("Test", shortTitle.getTextContent(), "Text is wrong");
		//
		var moduleConfiguration = result.getChildNodes().item(5);
		assertEquals("moduleConfiguration", moduleConfiguration.getNodeName(), "NodeName is wrong");
		assertTrue(moduleConfiguration.hasChildNodes(), "A moduleConfiguration-element has always children");
	}

	@Test
	void createTreeStructureOPAL_Normal() {
		var parent = "org.olat.course.tree.CourseEditorTreeNode";
		var realMaterial =
				new Material(MaterialType.WIKI, TemplateType.DEFINITION, "Test-Term", "TEST_ID", "STRUCTURE_ID");
		var material = new OPALMaterialInfo("Test", 1, realMaterial, null);
		var result = singlePageOPAL.createTreeStructureOPAL(material, parent, document);

		assertEquals("org.olat.course.tree.CourseEditorTreeNode", result.getTagName(), "Tagname is wrong");
		assertTrue(result.getAttribute("class").isEmpty());
		// ident
		var ident = result.getChildNodes().item(0);
		assertEquals("ident", ident.getNodeName(), "NodeName is wrong");
		assertEquals("1", ident.getTextContent(), "Id is wrong");
		// parent
		var parentElement = (Element) result.getChildNodes().item(1);
		assertEquals("parent", parentElement.getNodeName(), "NodeName is wrong");
		assertFalse(parentElement.hasChildNodes(), "A parent-element has no children");
		assertEquals("org.olat.course.tree.CourseEditorTreeNode", parentElement.getAttribute("class"),
				"Class is wrong");
		assertEquals("../../..", parentElement.getAttribute("reference"), "Reference is wrong");
		// children
		var children = (Element) result.getChildNodes().item(2);
		assertEquals("children", children.getNodeName(), "NodeName is wrong");
		assertFalse(children.hasChildNodes(), "A children-element has no children");
		// accessible
		var accessible = result.getChildNodes().item(3);
		assertEquals("accessible", accessible.getNodeName(), "NodeName is wrong");
		assertEquals("true", accessible.getTextContent(), "Text is wrong");
		// selected
		var selected = result.getChildNodes().item(4);
		assertEquals("selected", selected.getNodeName(), "NodeName is wrong");
		assertEquals("false", selected.getTextContent(), "Text is wrong");
		// hrefPreferred
		var hrefPreferred = result.getChildNodes().item(5);
		assertEquals("hrefPreferred", hrefPreferred.getNodeName(), "NodeName is wrong");
		assertEquals("false", hrefPreferred.getTextContent(), "Text is wrong");
		// invisible
		var invisible = result.getChildNodes().item(6);
		assertEquals("invisible", invisible.getNodeName(), "NodeName is wrong");
		assertEquals("false", invisible.getTextContent(), "Text is wrong");
		// cn
		var cn = (Element) result.getChildNodes().item(7);
		assertEquals("cn", cn.getNodeName(), "NodeName is wrong");
		assertTrue(cn.hasChildNodes(), "A cn-element must have children");

		var cnIdent = cn.getChildNodes().item(0);
		assertEquals("ident", cnIdent.getNodeName(), "NodeName is wrong");
		assertEquals("1", cnIdent.getTextContent(), "Text is wrong");
		var cnType = cn.getChildNodes().item(1);
		assertEquals("type", cnType.getNodeName(), "NodeName is wrong");
		assertEquals("sp", cnType.getTextContent(), "Text is wrong");
		var cnShortTitle = cn.getChildNodes().item(2);
		assertEquals("shortTitle", cnShortTitle.getNodeName(), "NodeName is wrong");
		assertEquals("Test", cnShortTitle.getTextContent(), "Text is wrong");
		var cnModuleConfiguration = cn.getChildNodes().item(3);
		assertEquals("moduleConfiguration", cnModuleConfiguration.getNodeName(), "NodeName is wrong");
		assertTrue(cnModuleConfiguration.hasChildNodes(), "A moduleConfiguration-element has always children");

		// dirty
		var dirty = result.getChildNodes().item(8);
		assertEquals("dirty", dirty.getNodeName(), "NodeName is wrong");
		assertEquals("false", dirty.getTextContent(), "Text is wrong");
		// deleted
		var deleted = result.getChildNodes().item(9);
		assertEquals("deleted", deleted.getNodeName(), "NodeName is wrong");
		assertEquals("false", deleted.getTextContent(), "Text is wrong");
		// newnode
		var newnode = result.getChildNodes().item(10);
		assertEquals("newnode", newnode.getNodeName(), "NodeName is wrong");
		assertEquals("false", newnode.getTextContent(), "Text is wrong");

	}
}