package de.olivergeisel.materialgenerator.finalization.export.opal;

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
	 * @return The document with the root node
	 */
	public Element createRootOPAL(CourseOrganizerOPAL course, FileCreationType nodeType, Document document) {
		Element back;
		if (nodeType == RUN) {
			back = innerRoot(ROOT_TYPE, nodeType, course, document);
		} else {
			var root = document.createElement(ROOT_TYPE);
			root.setAttribute(CLASS, TREE_ROOT_CLASS);
			var ident = document.createElement("ident");
			ident.setTextContent(Long.toString(course.getNodeId()));
			root.appendChild(ident);
			var children = document.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			var order = (CourseOrganizerOPAL.OpalOrderRaw) course.getOrder();
			for (var part : order.getChapterInfos()) {
				var opalChild = new StructureOPAL();
				var child = opalChild.createTreeStructureOPAL(part, TREE_PARENT, document);
				children.appendChild(child);
			}
			root.appendChild(children);
			root.appendChild(elementWithText(document, "accessible", true));
			root.appendChild(elementWithText(document, "selected", false));
			root.appendChild(elementWithText(document, "hrefPreferred", false));
			root.appendChild(elementWithText(document, "invisible", false)); // Todo maybe true for protection
			root.appendChild(innerRoot("cn", nodeType, course, document));
			root.appendChild(elementWithText(document, "dirty", false));
			root.appendChild(elementWithText(document, "deleted", false));
			root.appendChild(elementWithText(document, "newnode", false));
			back = root;
		}
		return back;
	}


	/**
	 * Internal call to create the root node in the document.
	 *
	 * @param tagType  The type of the tag (rootNode or cn)
	 * @param fileType The type of the file (RUN or TREE)
	 * @param course   The course to create the root for
	 * @param doc      The document to create the node in
	 * @return The new root node
	 */
	private Element innerRoot(String tagType, FileCreationType fileType, CourseOrganizerOPAL course, Document doc) {
		var node = doc.createElement(tagType);
		node.setAttribute(CLASS, INNER_ROOT_CLASS);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(course.getNodeId()));
		node.appendChild(ident);
		if (fileType == RUN) {
			var children = doc.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			node.appendChild(children);
			var order = (CourseOrganizerOPAL.OpalOrderRaw) course.getOrder();
			for (var part : order.getChapterInfos()) {
				var opalChild = new StructureOPAL();
				var child = opalChild.createRunStructureOPAL(part, INNER_ROOT_CLASS, doc);
				children.appendChild(child);
			}
		}
		appendRest(doc, node, course.getMeta().getName().orElse("New Course"));
		return node;
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
