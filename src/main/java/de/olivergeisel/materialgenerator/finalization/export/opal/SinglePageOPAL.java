package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Random;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.RUN;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.FileCreationType.TREE;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.createEntry;
import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.elementWithText;


/**
 * A SinglePage in the OPAL xml.
 * Nodetype: org.olat.course.nodes.STCourseNode
 * TreeType: cn
 */
public class SinglePageOPAL {

	private static final String RUN_TYPE     = "org.olat.course.nodes.STCourseNode";
	private static final String TREE_TYPE    = "cn";
	private static final String TREE_CLASS   = "org.olat.course.nodes.SPCourseNode";
	private static final String TREE_ELEMENT = "org.olat.course.tree.CourseEditorTreeNode";
	private static final String CLASS        = "class";


	public Element createRunStructureOPAL(String parentType, String file, Document doc) {
		var id = new Random().nextLong(100_000_000);
		return createSinglePageOPAL(parentType, file, RUN, id, doc);
	}

	/**
	 * Create the node in the tree structure.
	 *
	 * @param parentType parent type of the page (e.g. "org.olat.course.nodes.STCourseNode")
	 * @param file       file to link (html to display in the page)
	 * @return the document with the tree structure
	 */
	public Element createTreeStructureOPAL(String parentType, String file, Document doc) {
		var root = doc.createElement(TREE_ELEMENT);
		var id = new Random().nextLong(100_000_000);
		var ident = doc.createElement("ident");
		ident.setTextContent(Long.toString(id));
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
		var cn = createSinglePageOPAL(parentType, file, BasicElementsOPAL.FileCreationType.TREE, id, doc);
		children.appendChild(cn);
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
	 * @param file       file to link (html to display in the page)
	 * @return the document with the single page
	 */
	protected Element createSinglePageOPAL(String parentType, String file, BasicElementsOPAL.FileCreationType type,
			long id, Document doc) {
		var root = doc.createElement(type == RUN ? RUN_TYPE : TREE_TYPE);
		if (type == TREE) {
			root.setAttribute(CLASS, TREE_CLASS);
		}
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
		}
		var typeElement = doc.createElement("type");
		typeElement.setTextContent("sp");
		root.appendChild(typeElement);
		var title = doc.createElement("shortTitle");
		root.appendChild(title);
		root.appendChild(createModuleConfiguration(doc, file));
		// TODO Skip preconditions at moment. can be added later maybe
		return root;
	}


	private Element createModuleConfiguration(Document doc, String file) {
		var moduleConfig = doc.createElement("moduleConfiguration");
		var config = doc.createElement("config");
		moduleConfig.appendChild(config);
		config.appendChild(createEntry(doc, "startpage", false));
		config.appendChild(createEntry(doc, "file", file));
		config.appendChild(createEntry(doc, "configversion", 3));
		return moduleConfig;
	}
}
