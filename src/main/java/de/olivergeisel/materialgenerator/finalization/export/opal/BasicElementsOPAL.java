package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Helper class to create basic elements for OPAL export.
 *
 * @version 1.1.0
 * @since 1.1.0
 * @author Oliver Geisel
 */
public class BasicElementsOPAL {

	static final         String CLASS    = "class";
	private static final String ENTRY    = "entry";
	private static final String WORDFORM = "wordform";

	public static Document newDocument() {
		var docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new CreateOPALException(e);
		}
		return docBuilder.newDocument();
	}

	public static Element elementWithText(Document doc, String name, String text) {
		var element = doc.createElement(name);
		element.appendChild(doc.createTextNode(text));
		return element;
	}

	public static Element elementWithText(Document doc, String name, long value) {
		return elementWithText(doc, name, Long.toString(value));
	}

	public static Element elementWithText(Document doc, String nodeName, boolean booleanValue) {
		return elementWithText(doc, nodeName, booleanValue ? "true" : "false");
	}

	public static Element emptyElement(Document doc, String name) {
		return doc.createElement(name);
	}

	public static Element createEntry(Document doc, String name, boolean booleanValue) {
		var entry = doc.createElement(ENTRY);
		var wordform = doc.createElement(WORDFORM);
		var booleanNode = doc.createElement("boolean");
		wordform.appendChild(doc.createTextNode(name));
		booleanNode.appendChild(doc.createTextNode(booleanValue ? "true" : "false"));
		entry.appendChild(wordform);
		entry.appendChild(booleanNode);
		return entry;
	}

	public static Element createEntry(Document doc, String firstValue, String secondValue) {
		var entry = doc.createElement(ENTRY);
		var wordform = doc.createElement(WORDFORM);
		var secondWord = doc.createElement(WORDFORM);
		wordform.appendChild(doc.createTextNode(firstValue));
		secondWord.appendChild(doc.createTextNode(secondValue));
		entry.appendChild(wordform);
		entry.appendChild(secondWord);
		return entry;
	}

	public static Element createEntry(Document doc, String firstValue, int intVal) {
		var entry = doc.createElement(ENTRY);
		var wordform = doc.createElement(WORDFORM);
		var intNode = doc.createElement("int");
		wordform.appendChild(doc.createTextNode(firstValue));
		intNode.appendChild(doc.createTextNode(Integer.toString(intVal)));
		entry.appendChild(wordform);
		entry.appendChild(intNode);
		return entry;
	}

	public static Element createEntry(Document doc, String name, double value) {
		var entry = doc.createElement(ENTRY);
		var wordform = doc.createElement(WORDFORM);
		var doubleNode = doc.createElement("float");
		wordform.appendChild(doc.createTextNode(name));
		doubleNode.appendChild(doc.createTextNode(Double.toString(value)));
		entry.appendChild(wordform);
		entry.appendChild(doubleNode);
		return entry;
	}

	public static Element createNoAccessExplanation(Document doc) {
		return elementWithText(doc, "noAccessExplanation", "Dieses Kurselement ist nicht zugänglich.");
	}

	public static Element createPreConditionVisibility(Document doc) {
		var root = doc.createElement("preConditionVisibility");
		root.appendChild(elementWithText(doc, "conditionId", "visibility"));
		innerPreconditionWrite(doc, root);
		return root;
	}

	public static Element createPreConditionAccess(Document doc) {
		var root = doc.createElement("preConditionAccess");
		root.appendChild(elementWithText(doc, "conditionId", "accessability"));
		innerPreconditionWrite(doc, root);
		return root;
	}

	public static void innerPreconditionWrite(Document doc, Element root) {
		root.appendChild(elementWithText(doc, "expertMode", false));
		root.appendChild(emptyElement(doc, "easyModeBeginDates"));
		root.appendChild(emptyElement(doc, "easyModeEndDates"));
		root.appendChild(elementWithText(doc, "easyModeDisallowGuests", false));
		root.appendChild(elementWithText(doc, "easyModeCoachesAndAdmins", false));
		root.appendChild(elementWithText(doc, "easyModeAlwaysAllowCoachesAndAdmins", false));
		root.appendChild(emptyElement(doc, "licencesConditions"));
		root.appendChild(elementWithText(doc, "easyModeLicences", false));
	}

	public static Element createPreConditionR(Document doc) {
		var root = doc.createElement("preConditionR");
		root.appendChild(elementWithText(doc, "conditionId", "readers"));
		innerPreconditionWrite(doc, root);
		return root;
	}

	public static Element createPreConditionRW(Document doc) {
		var root = doc.createElement("preConditionRW");
		root.appendChild(elementWithText(doc, "conditionId", "readerswriters"));
		root.appendChild(elementWithText(doc, "expertMode", false));
		root.appendChild(elementWithText(doc, "condition", "( isCourseCouch() or isCourseAdministrator() )"));
		root.appendChild(emptyElement(doc, "easyModeBeginDates"));
		root.appendChild(emptyElement(doc, "easyModeEndDates"));
		root.appendChild(elementWithText(doc, "easyModeDisallowGuests", false));
		root.appendChild(elementWithText(doc, "easyModeCoachesAndAdmins", true));
		root.appendChild(elementWithText(doc, "easyModeAlwaysAllowCoachesAndAdmins", false));
		root.appendChild(emptyElement(doc, "licencesConditions"));
		root.appendChild(elementWithText(doc, "easyModeLicences", false));
		return root;
	}


	public static Element createDefaultModuleConfiguration(Document doc) {
		var moduleConfig = doc.createElement("moduleConfiguration");
		var config = doc.createElement("config");
		moduleConfig.appendChild(config);
		config.appendChild(createEntry(doc, "courseEnrolmentEnabled", false));
		config.appendChild(createEntry(doc, "columns", 1));
		config.appendChild(createEntry(doc, "courseEnrolmentMailSubject", ""));
		config.appendChild(createEntry(doc, "display", "peekview"));
		config.appendChild(createEntry(doc, "courseEnrolmentGroupId", 0));
		config.appendChild(createEntry(doc, "courseEnrolmentMailEnabled", false));
		config.appendChild(createEntry(doc, "courseEnrolmentMailBody", ""));
		config.appendChild(createEntry(doc, "configversion", 3));

		return moduleConfig;
	}

	public static Element createElementWithAttribute(Document doc, String name, String attributeName,
			String attributeValue) {
		var element = doc.createElement(name);
		element.setAttribute(attributeName, attributeValue);
		return element;
	}

	public static String escapeTags(String text) {
		return text.replace("<", "&lt;").replace(">", "&gt;");
	}

	public enum FileCreationType {
		RUN, // if its for "runstructure.xml"
		TREE, // if its for "editorTreeModel.xml"
	}
}
