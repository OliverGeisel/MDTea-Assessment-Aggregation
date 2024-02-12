package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;
import org.w3c.dom.Document;

import java.util.Random;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.RUN;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.TREE;

public class StructureOPAL {

	private static final String RUN_TYPE    = "org.olat.course.nodes.STCourseNode";
	private static final String TREE_TYPE   = "cn";
	private static final String TREE_PARENT = "org.olat.course.tree.CourseEditorTreeNode";
	private static final String TREE_CLASS  = "org.olat.course.nodes.STCourseNode";
	private static final String CLASS       = "class";


	/**
	 * Create the element in the run structure.
	 *
	 * @param collection A collection of materials to set in the structure
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @return the document with the run structure
	 */
	public Document createRunStructureOPAL(MaterialOrderCollection collection, String parentType) {
		var id = new Random().nextLong(100_000_000);
		return createStructureOPAL(collection, parentType, RUN, id);
	}

	public Document createTreeStructureOPAL(MaterialOrderCollection collection, String parentType) {
		var doc = newDocument();
		var root = doc.createElement(TREE_PARENT);
		doc.appendChild(root);
		var id = new Random().nextInt(100_000_000);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(id));
		root.appendChild(ident);
		var parent = doc.createElement("parent");
		parent.setAttribute(CLASS, parentType);
		parent.setAttribute("reference", "../../..");
		root.appendChild(parent);
		var children = doc.createElement("children");
		children.setAttribute(CLASS, "linked-list");
		for (var part : collection) {
			var childId = new Random().nextLong(100_000_000);
			if (part instanceof MaterialOrderCollection materialOrderCollection) {
				var xmlGroup =
						new StructureOPAL().createStructureOPAL(materialOrderCollection, TREE_TYPE, TREE, childId);
				children.appendChild(xmlGroup);
			} else if (part instanceof Material task) {
				var page = new SinglePageOPAL();
				Document xmlTask;
				var typeToCreate = TREE;
				xmlTask = page.createTreeStructureOPAL(TREE_TYPE, task.toString()); //
				// Todo change to a file that is linked with the task
				children.appendChild(xmlTask.getDocumentElement());
			}
		}
		root.appendChild(children);
		root.appendChild(elementWithText(doc, "accessible", true));
		root.appendChild(elementWithText(doc, "selected", false));
		root.appendChild(elementWithText(doc, "hrefPreferred", false));
		root.appendChild(elementWithText(doc, "invisible", false));
		var cn = createStructureOPAL(collection, parentType, BasicElementsOPAL.FileCreationType.TREE, id);
		children.appendChild(cn.getDocumentElement());
		root.appendChild(elementWithText(doc, "dirty", false));
		root.appendChild(elementWithText(doc, "deleted", false));
		root.appendChild(elementWithText(doc, "newnode", false));
		return doc;
	}


	/**
	 * Create a structure in the OPAL xml. Content depends on the type.
	 *
	 * @param collection A collection of materials to set in the structure
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @param type       type of the file (RUN or TREE)
	 * @param id         id of this structure
	 * @return the document with the structure
	 */
	protected Document createStructureOPAL(MaterialOrderCollection collection, String parentType,
			FileCreationType type, long id) {
		Document doc = newDocument();
		var root = doc.createElement(type == RUN ? RUN_TYPE : TREE_TYPE);
		if (type == TREE) {
			root.setAttribute(CLASS, TREE_CLASS);
		}
		doc.appendChild(root);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(id));
		root.appendChild(ident);
		if (type == RUN) {
			var parent = doc.createElement("parent");
			parent.setAttribute(CLASS, parentType);
			parent.setAttribute("reference", "../../..");
			root.appendChild(parent);

			var children = doc.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			root.appendChild(children);
			for (var part : collection) {
				var childId = new Random().nextLong(100_000_000);
				if (part instanceof MaterialOrderCollection materialOrderCollection) {
					var xmlGroup =
							new StructureOPAL().createStructureOPAL(materialOrderCollection, RUN_TYPE, type, childId);
					children.appendChild(xmlGroup);
				} else if (part instanceof Material task) {
					var page = new SinglePageOPAL();
					Document xmlTask = page.createRunStructureOPAL(RUN_TYPE, task.toString());
					// Todo change to a file that is linked with the task
					children.appendChild(xmlTask.getDocumentElement());
				}
			}
		}
		var typeElement = elementWithText(doc, "type", "st");
		root.appendChild(typeElement);
		var titele = elementWithText(doc, "shortTitle", collection.getName());
		root.appendChild(titele);
		var longTitle = doc.createElement("longTitle");
		longTitle.setTextContent(collection.getName());
		root.appendChild(longTitle);
		var learningObjets = doc.createElement("learningObjects");
		root.appendChild(learningObjets);
		var displayOptions = doc.createElement("displayOptions");
		root.appendChild(displayOptions);
		displayOptions.setTextContent("title+desc+content");
		root.appendChild(createDefaultModuleConfiguration(doc));
		// Todo add other tags fo precondition
		var scoreCalc = doc.createElement("scoreCalculator");
		root.appendChild(scoreCalc);
		scoreCalc.appendChild(elementWithText(doc, "expertMode", false));
		scoreCalc.appendChild(elementWithText(doc, "passedCutValue", 0));
		root.appendChild(elementWithText(doc, "evaluateAsSubcourse", false));
		return doc;
	}


}
