package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.generation.material.assessment.MultipleChoiceItemMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.elementWithText;

public class MultipleChoiceItemWriter extends ChoiceItemWriter<MultipleChoiceItemMaterial> {

	@Override
	protected void createResponseDeclaration(Document document, Element root,
			OPALItemMaterialInfo<MultipleChoiceItemMaterial> item) {
		var responseDeclaration = document.createElement("responseDeclaration");
		responseDeclaration.setAttribute("identifier", "RESPONSE_1");
		responseDeclaration.setAttribute("cardinality", "multiple");
		responseDeclaration.setAttribute("baseType", "identifier");
		var correctResponse = document.createElement("correctResponse");
		var i = 1;
		for (var answer : item.getOriginalMaterial().getCorrectAnswers()) {
			// Todo implement the case where the order matters not (id 1,2,3)
			var value = elementWithText(document, "value", STR."ID_\{i++}");
			correctResponse.appendChild(value);
			responseDeclaration.appendChild(correctResponse);
		}
		root.appendChild(responseDeclaration);
	}

	@Override
	protected void createItemBody(Document document, Element root,
			OPALItemMaterialInfo<MultipleChoiceItemMaterial> item) {
		var itemBody = document.createElement("itemBody");
		itemBody.appendChild(elementWithText(document, "p", item.getOriginalMaterial().getQuestion()));
		var choiceInteraction = document.createElement("choiceInteraction");
		itemBody.appendChild(choiceInteraction);
		choiceInteraction.setAttribute("responseIdentifier", "RESPONSE_1");
		choiceInteraction.setAttribute("shuffle", Boolean.toString(item.getOriginalMaterial().isShuffle()));
		choiceInteraction.setAttribute("maxChoices", "0");
		var i = 1;
		for (var answer : item.getOriginalMaterial().getCorrectAnswers()) {
			var choice = document.createElement("simpleChoice");
			choice.setAttribute("identifier", STR."ID_\{i++}");
			choice.appendChild(elementWithText(document, "p", answer));
			choiceInteraction.appendChild(choice);
		}
		for (var answer : item.getOriginalMaterial().getAlternativeAnswers()) {
			var choice = document.createElement("simpleChoice");
			choice.setAttribute("identifier", STR."ID_\{i++}");
			choice.appendChild(elementWithText(document, "p", answer));
			choiceInteraction.appendChild(choice);
		}
		root.appendChild(itemBody);
	}
}
