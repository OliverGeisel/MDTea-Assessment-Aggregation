package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.finalization.export.opal.DefaultXMLWriter;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;

/**
 * This class is responsible for writing the item as a xml-file to the file system.
 *
 * @param <T> The type of the {@link ItemMaterial}.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ItemMaterial
 * @since 1.1.0
 */
public abstract class ItemWriter<I extends ItemMaterial, T extends OPALItemMaterialInfo<I>> {

	public void writeItem(T item, File targetDirectory) {
		var document = newDocument();
		var root = document.createElement("assessmentItem");
		root.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_v2p1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", """
				http://www.imsglobal.org/xsd/imsqti_v2p1
				http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd
				http://www.w3.org/1998/Math/MathML
				http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd""");
		root.setAttribute("identifier", Long.toString(item.getNodeId()));
		root.setAttribute("title", item.getOriginalMaterial().getName());
		root.setAttribute("adaptive", "false");
		root.setAttribute("timeDependent", "false");
		document.appendChild(root);
		// response declaration
		createResponseDeclaration(document, root, item);
		// outcome
		var outcomeDeclaration = document.createElement("outcomeDeclaration");
		outcomeDeclaration.setAttribute("identifier", "SCORE");
		outcomeDeclaration.setAttribute("cardinality", "single");
		outcomeDeclaration.setAttribute("baseType", "float");
		var defaultValue = document.createElement("defaultValue");
		defaultValue.appendChild(elementWithText(document, "value", "0"));
		outcomeDeclaration.appendChild(defaultValue);
		root.appendChild(outcomeDeclaration);

		var outcomeDeclaration2 = document.createElement("outcomeDeclaration");
		outcomeDeclaration2.setAttribute("identifier", "MAXSCORE");
		outcomeDeclaration2.setAttribute("cardinality", "single");
		outcomeDeclaration2.setAttribute("baseType", "float");
		var defaultValue2 = document.createElement("defaultValue");
		defaultValue2.appendChild(elementWithText(document, "value", "1"));
		outcomeDeclaration2.appendChild(defaultValue2);
		root.appendChild(outcomeDeclaration2);

		var outcomeDeclaration4 = document.createElement("outcomeDeclaration");
		outcomeDeclaration4.setAttribute("identifier", "FEEDBACKBASIC");
		outcomeDeclaration4.setAttribute("cardinality", "single");
		outcomeDeclaration4.setAttribute("baseType", "identifier");
		var defaultValue4 = document.createElement("defaultValue");
		defaultValue4.appendChild(elementWithText(document, "value", "empty"));
		outcomeDeclaration4.appendChild(defaultValue4);
		root.appendChild(outcomeDeclaration4);

		var outcomeDeclaration3 = document.createElement("outcomeDeclaration");
		outcomeDeclaration3.setAttribute("identifier", "MINSCORE");
		outcomeDeclaration3.setAttribute("cardinality", "single");
		outcomeDeclaration3.setAttribute("baseType", "float");
		outcomeDeclaration3.setAttribute("view", "testConstructor");
		var defaultValue3 = document.createElement("defaultValue");
		defaultValue3.appendChild(elementWithText(document, "value", "0"));
		outcomeDeclaration3.appendChild(defaultValue3);
		root.appendChild(outcomeDeclaration3);
		// body
		createItemBody(document, root, item);
		// response processing
		root.appendChild(defaultResponseProcessing(document));
		// todo add modal feedback

		// write
		var filePath = new File(targetDirectory, STR."\{item.getNodeId()}.xml").getAbsolutePath();
		DefaultXMLWriter.write(document, filePath);
	}

	protected abstract void createResponseDeclaration(Document document, Element root, OPALItemMaterialInfo<I> item);

	protected abstract void createItemBody(Document document, Element root, OPALItemMaterialInfo<I> item);


	protected Element defaultResponseProcessing(Document document) {
		var responseProcessing = document.createElement("responseProcessing");
		var responseCondition = document.createElement("responseCondition");
		responseProcessing.appendChild(responseCondition);
		var responseIf = document.createElement("responseIf");
		responseCondition.appendChild(responseIf);
		var isNull = document.createElement("isNull");
		responseIf.appendChild(isNull);
		var variable = document.createElement("variable");
		variable.setAttribute("identifier", "RESPONSE_1");
		responseIf.appendChild(variable);
		var elseIf = document.createElement("responseElseIf");
		responseCondition.appendChild(elseIf);
		var match = document.createElement("match");
		elseIf.appendChild(match);
		var variable2 = document.createElement("variable");
		variable2.setAttribute("identifier", "RESPONSE_1");
		match.appendChild(variable2);
		var correct = document.createElement("correct");
		correct.setAttribute("identifier", "RESPONSE_1");
		match.appendChild(correct);
		var setOutcomeValue = document.createElement("setOutcomeValue");
		setOutcomeValue.setAttribute("identifier", "SCORE");
		elseIf.appendChild(setOutcomeValue);
		var sum = document.createElement("sum");
		setOutcomeValue.appendChild(sum);
		var variable3 = document.createElement("variable");
		variable3.setAttribute("identifier", "SCORE");
		sum.appendChild(variable3);
		var variable4 = document.createElement("variable");
		variable4.setAttribute("identifier", "MAXSCORE");
		sum.appendChild(variable4);

		var responseCondition2 = document.createElement("responseCondition");
		responseProcessing.appendChild(responseCondition2);
		var responseIf2 = document.createElement("responseIf");
		responseCondition2.appendChild(responseIf2);
		var gt = document.createElement("gt");
		responseIf2.appendChild(gt);
		gt.appendChild(createElementWithAttribute(document, "variable", "identifier", "SCORE"));
		gt.appendChild(createElementWithAttribute(document, "variable", "identifier", "MAXSCORE"));
		var setOutcomeValue2 = document.createElement("setOutcomeValue");
		setOutcomeValue2.setAttribute("identifier", "SCORE");
		responseIf2.appendChild(setOutcomeValue2);
		setOutcomeValue2.appendChild(createElementWithAttribute(document, "variable", "identifier", "MAXSCORE"));

		var responseCondition3 = document.createElement("responseCondition");
		responseProcessing.appendChild(responseCondition3);
		var responseIf3 = document.createElement("responseIf");
		responseCondition3.appendChild(responseIf3);
		var lt = document.createElement("lt");
		responseIf3.appendChild(lt);
		lt.appendChild(createElementWithAttribute(document, "variable", "identifier", "SCORE"));
		lt.appendChild(createElementWithAttribute(document, "variable", "identifier", "MINSCORE"));
		var setOutcomeValue3 = document.createElement("setOutcomeValue");
		setOutcomeValue3.setAttribute("identifier", "SCORE");
		responseIf3.appendChild(setOutcomeValue3);
		setOutcomeValue3.appendChild(createElementWithAttribute(document, "variable", "identifier", "MINSCORE"));

		return responseProcessing;
	}
}
