package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.generation.material.Material;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.RUN;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.TREE;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;


/**
 * A SinglePage in the OPAL xml. Used for {@link Material}s.
 * Nodetype: org.olat.course.nodes.STCourseNode
 * TreeType: cn
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 1.1.0
 */
public class SinglePageOPAL {

	private static final String RUN_TYPE = "org.olat.course.nodes.SPCourseNode";
	private static final String TREE_TYPE    = "cn";
	private static final String TREE_CLASS   = "org.olat.course.nodes.SPCourseNode";
	private static final String TREE_ELEMENT = "org.olat.course.tree.CourseEditorTreeNode";
	private static final String CLASS        = "class";


	/**
	 * Create the node in the run structure.
	 *
	 * @param material   the material to create the page for
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @param doc        the document to create the page in
	 * @return the element for the run structure
	 */
	public Element createRunStructureOPAL(OPALMaterialInfo material, String parentType, Document doc) {
		return createSinglePageOPAL(material, parentType, RUN, doc);
	}

	/**
	 * Create the node in the tree structure.
	 *
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @param material   the material to create the page for
	 * @return the document with the tree structure
	 */
	public Element createTreeStructureOPAL(OPALMaterialInfo material, String parentType, Document doc) {
		var root = doc.createElement(TREE_ELEMENT);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(material.getNodeId()));
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
		var cn = createSinglePageOPAL(material, parentType, BasicElementsOPAL.FileCreationType.TREE, doc);
		root.appendChild(cn);
		root.appendChild(elementWithText(doc, "dirty", false));
		root.appendChild(elementWithText(doc, "deleted", false));
		root.appendChild(elementWithText(doc, "newnode", false));
		return root;
	}

	/**
	 * Create a single page OPAL document.
	 *
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode" or "org.olat.course.nodes
	 *                   .RootCourseNode")
	 * @param material   the material to create the page for
	 * @return the document with the single page
	 */
	private Element createSinglePageOPAL(OPALMaterialInfo material, String parentType,
			BasicElementsOPAL.FileCreationType type, Document doc) {
		var root = doc.createElement(type == RUN ? RUN_TYPE : TREE_TYPE);
		if (type == TREE) {
			root.setAttribute(CLASS, TREE_CLASS);
		}
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(material.getNodeId()));
		root.appendChild(ident);
		if (type == RUN) {
			var parent = doc.createElement("parent");
			parent.setAttribute(CLASS, parentType);
			parent.setAttribute("reference", "../../..");
			root.appendChild(parent);
			var children = doc.createElement("children");
			children.setAttribute(CLASS, "linked-list");
			root.appendChild(children);
		}
		var typeElement = doc.createElement("type");
		typeElement.setTextContent("sp");
		root.appendChild(typeElement);
		var title = doc.createElement("shortTitle");
		title.setTextContent(material.getName());
		root.appendChild(title);
		root.appendChild(createModuleConfiguration(doc, material.completeFileName()));
		root.appendChild(createPreConditionVisibility(doc));
		root.appendChild(createPreConditionAccess(doc));
		root.appendChild(emptyElement(doc, "additionalConditions"));
		root.appendChild(createPreConditionR(doc));
		root.appendChild(createPreConditionRW(doc));
		return root;
	}


	private Element createModuleConfiguration(Document doc, String file) {
		var moduleConfig = doc.createElement("moduleConfiguration");
		var config = doc.createElement("config");
		moduleConfig.appendChild(config);
		config.appendChild(createEntry(doc, "startpage", false));
		config.appendChild(createEntry(doc, "file", STR."/\{file}"));
		config.appendChild(createEntry(doc, "configversion", 3));
		return moduleConfig;
	}
}
