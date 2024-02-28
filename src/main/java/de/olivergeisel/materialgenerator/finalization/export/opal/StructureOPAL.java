package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	public Element createRunStructureOPAL(MaterialCollectionOPAL collection, String parentType, Document doc) {
		return createStructureOPAL(collection, parentType, RUN, doc);
	}

	public Element createTreeStructureOPAL(MaterialCollectionOPAL collection, String parentType, Document doc) {
		var root = doc.createElement(TREE_PARENT);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(collection.getNodeId()));
		root.appendChild(ident);
		var parent = doc.createElement("parent");
		parent.setAttribute(CLASS, parentType);
		parent.setAttribute("reference", "../../..");
		root.appendChild(parent);
		var children = doc.createElement("children");
		children.setAttribute(CLASS, "linked-list");
		for (var part : collection.getMaterialOrder()) {
			if (part instanceof MaterialCollectionOPAL subCollection) {
				var xmlGroup =
						new StructureOPAL().createTreeStructureOPAL(subCollection, TREE_PARENT, doc);
				children.appendChild(xmlGroup);
			} else if (part instanceof OPALMaterialInfo material) {
				var page = new SinglePageOPAL();
				Element xmlTask = page.createTreeStructureOPAL(material, TREE_PARENT, doc);
				children.appendChild(xmlTask);
			}
		}
		if (collection instanceof OPALGroupInfo group) {
			for (var complex : group.getComplexMaterialInfos()) {
				if (complex instanceof OPAlTestMaterialInfo testMaterial) {
					var testOPAL = new TestOPAL();
					var testXML = testOPAL.createTreeTestOPAL(testMaterial, TREE_PARENT, doc);
					children.appendChild(testXML);
				}
			}
		}
		root.appendChild(children);
		root.appendChild(elementWithText(doc, "accessible", true));
		root.appendChild(elementWithText(doc, "selected", false));
		root.appendChild(elementWithText(doc, "hrefPreferred", false));
		root.appendChild(elementWithText(doc, "invisible", false));
		var cn = createStructureOPAL(collection, parentType, BasicElementsOPAL.FileCreationType.TREE, doc);
		root.appendChild(cn);
		root.appendChild(elementWithText(doc, "dirty", false));
		root.appendChild(elementWithText(doc, "deleted", false));
		root.appendChild(elementWithText(doc, "newnode", false));
		return root;
	}


	/**
	 * Create a structure in the OPAL xml. Content depends on the type.
	 *
	 * @param collection A collection of materials to set in the structure
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @param type       type of the file (RUN or TREE)
	 * @param doc        The document to create the structure in
	 * @return the document with the structure
	 */
	private Element createStructureOPAL(MaterialCollectionOPAL collection, String parentType, FileCreationType type,
			Document doc) {
		var root = doc.createElement(type == RUN ? RUN_TYPE : TREE_TYPE);
		if (type == TREE) {
			root.setAttribute(CLASS, TREE_CLASS);
		}
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(collection.getNodeId()));
		root.appendChild(ident);
		if (type == RUN) {
			var parent = doc.createElement("parent");
			parent.setAttribute(CLASS, parentType);
			parent.setAttribute("reference", "../../..");
			root.appendChild(parent);

			var children = doc.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			root.appendChild(children);
			// todo disable loop if nothing should be visible in OPAL for public
			for (var part : collection.getMaterialOrder()) {
				if (part instanceof MaterialCollectionOPAL materialOrderCollection) {
					var xmlGroup =
							new StructureOPAL().createStructureOPAL(materialOrderCollection, RUN_TYPE, type,
									doc);
					children.appendChild(xmlGroup);
				} else if (part instanceof OPALMaterialInfo material) {
					var page = new SinglePageOPAL();
					var xmlTask = page.createRunStructureOPAL(material, RUN_TYPE, doc);
					// Todo change to a file that is linked with the task
					children.appendChild(xmlTask);
				}
			}
			if (collection instanceof OPALGroupInfo group) {
				for (var complex : group.getComplexMaterialInfos()) {
					if (complex instanceof OPAlTestMaterialInfo testMaterial) {
						var testOPAL = new TestOPAL();
						var testXML = testOPAL.createRunTestOPAL(testMaterial, RUN_TYPE, doc);
						children.appendChild(testXML);
					}
				}
			}
		}
		var typeElement = elementWithText(doc, "type", "st");
		root.appendChild(typeElement);
		var titele = elementWithText(doc, "shortTitle", collection.getName());
		root.appendChild(titele);
		if (type == RUN) {
			var longTitle = doc.createElement("longTitle");
			longTitle.setTextContent(collection.getName());
			root.appendChild(longTitle);
			var learningObjets = elementWithText(doc, "learningObjectives", "");
			root.appendChild(learningObjets);

			var displayOptions = doc.createElement("displayOption");
		root.appendChild(displayOptions);
		displayOptions.setTextContent("title+desc+content");
		}
		root.appendChild(createDefaultModuleConfiguration(doc));
		root.appendChild(createPreConditionVisibility(doc));
		root.appendChild(createPreConditionAccess(doc));
		root.appendChild(emptyElement(doc, "additionalConditions"));
		var scoreCalc = doc.createElement("scoreCalculator");
		root.appendChild(scoreCalc);
		scoreCalc.appendChild(elementWithText(doc, "expertMode", false));
		scoreCalc.appendChild(elementWithText(doc, "passedCutValue", 0));
		root.appendChild(elementWithText(doc, "evaluateAsSubcourse", false));
		return root;
	}


}
