package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.generation.material.assessment.SingleChoiceItemMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.elementWithText;


/**
 * Write a single choice item to a file for the OPAL system or the IMS QTI 2.1 standard.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see SingleChoiceItemMaterial
 * @see OPALItemMaterialInfo
 * @see ItemWriter
 * @since 1.1.0
 */
public class SingleChoiceItemWriter extends ChoiceItemWriter<SingleChoiceItemMaterial> {


	@Override
	protected void createResponseDeclaration(Document document, Element root,
			OPALItemMaterialInfo<SingleChoiceItemMaterial> item) {
		var responseDeclaration = document.createElement("responseDeclaration");
		responseDeclaration.setAttribute("identifier", "RESPONSE_1");
		responseDeclaration.setAttribute("cardinality", "single");
		responseDeclaration.setAttribute("baseType", "identifier");
		var correctResponse = document.createElement("correctResponse");
		var value = elementWithText(document, "value", "ID_1");
		correctResponse.appendChild(value);
		responseDeclaration.appendChild(correctResponse);
		root.appendChild(responseDeclaration);
	}

	@Override
	protected void createItemBody(Document document, Element root,
			OPALItemMaterialInfo<SingleChoiceItemMaterial> item) {
		var itemBody = document.createElement("itemBody");
		itemBody.appendChild(elementWithText(document, "p", item.getOriginalMaterial().getQuestion()));
		var choiceInteraction = document.createElement("choiceInteraction");
		choiceInteraction.setAttribute("responseIdentifier", "RESPONSE_1");
		choiceInteraction.setAttribute("shuffle", Boolean.toString(item.getOriginalMaterial().isShuffle()));
		choiceInteraction.setAttribute("maxChoices", "1");

		var correct = document.createElement("simpleChoice");
		correct.setAttribute("identifier", "ID_1");
		correct.appendChild(elementWithText(document, "p", item.getOriginalMaterial().getCorrectAnswer()));
		choiceInteraction.appendChild(correct);
		int i = 2;
		for (var answer : item.getOriginalMaterial().getAlternativeAnswers()) {
			var choice = document.createElement("simpleChoice");
			choice.setAttribute("identifier", STR."ID_\{i++}");
			choice.appendChild(elementWithText(document, "p", answer));
			choiceInteraction.appendChild(choice);
		}
		itemBody.appendChild(choiceInteraction);
		root.appendChild(itemBody);
	}
}
