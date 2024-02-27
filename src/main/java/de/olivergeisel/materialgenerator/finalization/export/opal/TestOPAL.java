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


	public static Element createPreConditionVisibilityTestRun(Document doc) {
		var root = doc.createElement("preConditionVisibilityTestRun");
		root.appendChild(elementWithText(doc, "conditionId", "visibilityrun"));
		innerPreconditionWrite(doc, root);
		return root;
	}

	public static Element createPreConditionVisibilityAssessment(Document doc) {
		var root = doc.createElement("preConditionVisibilityAssessment");
		root.appendChild(elementWithText(doc, "conditionId", "visibilityassessment"));
		innerPreconditionWrite(doc, root);
		return root;
	}

	public static Element createPreConditionVisibilityResults(Document doc) {
		var root = doc.createElement("preConditionVisibilityResults");
		root.appendChild(elementWithText(doc, "conditionId", "visibilityresult"));
		innerPreconditionWrite(doc, root);
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
		root.appendChild(createDefaultTestModuleConfiguration(STR."myolat_\{test.getNodeId()}", doc));
		root.appendChild(createPreConditionVisibility(doc));
		root.appendChild(createPreConditionAccess(doc));
		root.appendChild(createPreConditionVisibilityTestRun(doc));
		root.appendChild(createPreConditionVisibilityAssessment(doc));
		root.appendChild(createPreConditionVisibilityResults(doc));
		return root;
	}

	Element createDefaultTestModuleConfiguration(String key, Document doc) {
		var moduleConfig = doc.createElement("moduleConfiguration");
		var config = doc.createElement("config");
		moduleConfig.appendChild(config);
		config.appendChild(createEntry(doc, "showoverview", false));
		config.appendChild(createEntry(doc, "cutvalue", 1.0));
		config.appendChild(createEntry(doc, "enableCancel", false));
		config.appendChild(createEntry(doc, "itemShowScore", false));
		config.appendChild(createEntry(doc, "configversion", 5));
		config.appendChild(createEntry(doc, "mode", "Assessment"));
		config.appendChild(createEntry(doc, "suspendAllowed", false));
		config.appendChild(createEntry(doc, "testShowSolution", false));
		config.appendChild(createEntry(doc, "itemShowCorrect", false));
		config.appendChild(createEntry(doc, "enableScoreInfo", true));
		config.appendChild(createEntry(doc, "enableMenu", true));
		config.appendChild(createEntry(doc, "testShowFeedback", false));
		config.appendChild(createEntry(doc, "showResultsOnHomePage", true));
		config.appendChild(createEntry(doc, "exambrowser", false));
		config.appendChild(createEntry(doc, "attempts", 0));
		config.appendChild(createEntry(doc, "summary", "summaryCompact"));
		config.appendChild(createEntry(doc, "repoSoftkey", key));
		config.appendChild(createEntry(doc, "examconfirm", false));
		config.appendChild(createEntry(doc, "templateid", "onyxdefault"));
		config.appendChild(createEntry(doc, "showTaskTitles", true));
		config.appendChild(createEntry(doc, "showpdf", false));
		config.appendChild(createEntry(doc, "examsynchronizedstart", false));
		config.appendChild(createEntry(doc, "enableSuspend", false));
		config.appendChild(createEntry(doc, "qtitype", "qti2"));
		config.appendChild(createEntry(doc, "sequence", "itemPage"));
		config.appendChild(createEntry(doc, "itemShowMax", true));
		config.appendChild(createEntry(doc, "itemShowSolution", false));
		config.appendChild(createEntry(doc, "examcontrol", false));
		config.appendChild(createEntry(doc, "itemShowFeedback", false));
		config.appendChild(createEntry(doc, "renderMenu", false));
		return moduleConfig;
	}
}
