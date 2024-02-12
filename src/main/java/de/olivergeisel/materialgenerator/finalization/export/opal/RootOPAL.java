package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.RUN;

public class RootOPAL {

	private static final String ROOT_TYPE        = "rootNode";
	private static final String TREE_PARENT      = "org.olat.course.tree.CourseEditorTreeNode";
	private static final String INNER_ROOT_CLASS = "org.olat.course.nodes.RootCourseNode";
	private static final String TREE_ROOT_CLASS  = "org.olat.course.tree.CourseEditorTreeNode";


	/**
	 * Create the root node in the structure (run or tree).
	 *
	 * @param course   The course to create the root for
	 * @param nodeType The type of the node (RUN or TREE)
	 * @param id       The id of the node
	 * @return The document with the root node
	 */
	public Document createRootOPAL(RawCourse course, FileCreationType nodeType, long id) {
		var document = newDocument();
		if (nodeType == RUN) {
			var node = innerRoot(ROOT_TYPE, nodeType, id, course);
			document.appendChild(node.getDocumentElement());
		} else {
			var root = document.createElement(ROOT_TYPE);
			root.setAttribute(CLASS, TREE_ROOT_CLASS);
			document.appendChild(root);
			var ident = document.createElement("ident");
			ident.setTextContent(Long.toString(id));
			root.appendChild(ident);
			var children = document.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			for (var part : course.getCourseOrder().getChapterOrder()) {
				var opalChild = new StructureOPAL();
				Document child = opalChild.createTreeStructureOPAL(part, TREE_PARENT);
				children.appendChild(child.getDocumentElement());
			}
			root.appendChild(children);
			root.appendChild(elementWithText(document, "accessible", true));
			root.appendChild(elementWithText(document, "selected", false));
			root.appendChild(elementWithText(document, "hrefPreferred", false));
			root.appendChild(elementWithText(document, "invisible", false)); // Todo maybe true for protection
			root.appendChild(innerRoot("cn", nodeType, id, course).getDocumentElement());
			var dirtry = elementWithText(document, "dirty", false);
			var deleted = elementWithText(document, "deleted", false);
			var newElement = elementWithText(document, "newnode", false);
			root.appendChild(dirtry);
			root.appendChild(deleted);
			root.appendChild(newElement);
		}
		return document;
	}


	private Document innerRoot(String tagType, FileCreationType fileType, long id, RawCourse course) {
		Document doc = newDocument();
		var node = doc.createElement(tagType);
		node.setAttribute(CLASS, INNER_ROOT_CLASS);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(id));
		node.appendChild(ident);
		if (fileType == RUN) {
			var children = doc.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			for (var part : course.getCourseOrder().getChapterOrder()) {
				var opalChild = new StructureOPAL();
				Document child = opalChild.createRunStructureOPAL(part, INNER_ROOT_CLASS);
				children.appendChild(child.getDocumentElement());
			}
		}
		appendRest(doc, node, course.getMeta().getName().orElse("New Course"));
		return doc;
	}

	private void appendRest(Document doc, Element root, String title) {
		root.appendChild(elementWithText(doc, "type", "root"));
		root.appendChild(elementWithText(doc, "shortTitle", title));
		root.appendChild(elementWithText(doc, "longTitle", title));
		root.appendChild(elementWithText(doc, "learningObjectives", ""));
		var moduleConfiguration = createDefaultModuleConfiguration(doc);
		root.appendChild(moduleConfiguration);
		// Todo add preconditions
		var additionalConditions = doc.createElement("additionalConditions");
		root.appendChild(additionalConditions);
		additionalConditions.appendChild(emptyElement(doc, "org.olat.course.condition.additionalconditions"
														   + ".PasswordCondition"));
		var scoreCalc = doc.createElement("scoreCalculator");
		scoreCalc.appendChild(elementWithText(doc, "expertMode", false));
		scoreCalc.appendChild(elementWithText(doc, "passedCutValue", 0));
		root.appendChild(scoreCalc);
		root.appendChild(elementWithText(doc, "evaluateAsSubcourse", false));
	}
}
