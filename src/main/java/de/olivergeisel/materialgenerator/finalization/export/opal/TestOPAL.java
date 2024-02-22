package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.RUN;

public class TestOPAL {

	private static final String RUN_TYPE    = "org.olat.course.nodes.IQTESTCourseNode";
	private static final String TREE_TYPE   = "cn";
	private static final String TREE_PARENT = "org.olat.course.tree.CourseEditorTreeNode";
	private static final String TREE_CLASS  = "org.olat.course.nodes.IQTESTCourseNode";

	private static final String CLASS = "class";


	public Element createRunTestOPAL(OPAlTestMaterialInfo test, String parentType, Document doc) {
		return createTestOPAL(test, parentType, RUN, doc);
	}

	public Element createTreeTestOPAL(OPAlTestMaterialInfo test, String parentType, Document doc) {
		var root = doc.createElement(TREE_PARENT);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(test.getNodeId()));
		root.appendChild(ident);
		var parent = doc.createElement("parent");
		parent.setAttribute(CLASS, parentType);
		parent.setAttribute("reference", "../../..");
		root.appendChild(parent);
		var children = doc.createElement("children");
		children.setAttribute(CLASS, "linked-list");
		root.appendChild(children);
		root.appendChild(elementWithText(doc, "accessible", true));
		root.appendChild(elementWithText(doc, "selected", false));
		root.appendChild(elementWithText(doc, "hrefPreferred", false));
		root.appendChild(elementWithText(doc, "invisible", false));
		var cn = createTestOPAL(test, parentType, FileCreationType.TREE, doc);
		root.appendChild(cn);
		root.appendChild(elementWithText(doc, "dirty", false));
		root.appendChild(elementWithText(doc, "deleted", false));
		root.appendChild(elementWithText(doc, "newnode", false));
		return root;
	}


	private Element createTestOPAL(OPAlTestMaterialInfo test, String parentType, FileCreationType type, Document doc) {
		var root = doc.createElement(type == RUN ? RUN_TYPE : TREE_TYPE);
		if (type == FileCreationType.TREE) {
			root.setAttribute(CLASS, TREE_CLASS);
		}
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(test.getNodeId()));
		root.appendChild(ident);
		if (type == RUN) {
			var parent = doc.createElement("parent");
			parent.setAttribute(CLASS, parentType);
			parent.setAttribute("reference", "../../..");
			root.appendChild(parent);

			root.appendChild(createElementWithAttribute(doc, "children", "class", "linked-list"));
		}
		root.appendChild(elementWithText(doc, "type", "iqtest"));
		root.appendChild(elementWithText(doc, "shortTitle", test.getName()));
		root.appendChild(elementWithText(doc, "longTitle", test.getName())); // Todo is Subtitle
		root.appendChild(elementWithText(doc, "learningObjectives", escapeTags(test.getDescription())));
		root.appendChild(elementWithText(doc, "displayOption", "title+description+content"));
		root.appendChild(createDefaultModuleConfiguration(doc));
		return root;
	}
}
