package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.generation.material.assessment.TrueFalseItemMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.elementWithText;


/**
 * This class is responsible for writing a {@link TrueFalseItemMaterial} to the Filesystem for OPAL or other QTI 2.1 compatible systems.
 * It's similar to the {@link SingleChoiceItemWriter}. All its differ is the number of possible Choices
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TrueFalseItemMaterial
 * @see OPALItemMaterialInfo
 * @see ItemWriter
 * @since 1.1.0
 */
public class TrueFalseItemWriter extends ChoiceItemWriter<TrueFalseItemMaterial> {


	@Override
	protected void createResponseDeclaration(Document document, Element root,
			OPALItemMaterialInfo<TrueFalseItemMaterial> item) {
		var responseDeclaration = document.createElement("responseDeclaration");
		responseDeclaration.setAttribute("identifier", "RESPONSE_1");
		responseDeclaration.setAttribute("cardinality", "single");
		responseDeclaration.setAttribute("baseType", "identifier");
		var correctResponse = document.createElement("correctResponse");
		var value = elementWithText(document, "value", item.getOriginalMaterial().isCorrect() ? "ID_1" : "ID_2");
		correctResponse.appendChild(value);
		responseDeclaration.appendChild(correctResponse);
		root.appendChild(responseDeclaration);
	}

	@Override
	protected void createItemBody(Document document, Element root, OPALItemMaterialInfo<TrueFalseItemMaterial> item) {
		var itemBody = document.createElement("itemBody");
		itemBody.appendChild(elementWithText(document, "p", item.getOriginalMaterial().getQuestion()));
		var choiceInteraction = document.createElement("choiceInteraction");
		choiceInteraction.setAttribute("responseIdentifier", "RESPONSE_1");
		choiceInteraction.setAttribute("shuffle", "false");
		choiceInteraction.setAttribute("maxChoices", "1");

		var trueAnswer = document.createElement("simpleChoice");
		trueAnswer.setAttribute("identifier", "ID_1");
		trueAnswer.appendChild(elementWithText(document, "p", "Ja"));
		choiceInteraction.appendChild(trueAnswer);
		var falseAnswer = document.createElement("simpleChoice");
		falseAnswer.setAttribute("identifier", "ID_2");
		falseAnswer.appendChild(elementWithText(document, "p", "Nein"));
		choiceInteraction.appendChild(falseAnswer);

		itemBody.appendChild(choiceInteraction);
		root.appendChild(itemBody);

	}
}
