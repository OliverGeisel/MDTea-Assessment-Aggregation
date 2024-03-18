package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.generation.material.assessment.FillOutBlanksItemMaterial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.elementWithText;

public class FillOutBlanksItemWriter
		extends ItemWriter<FillOutBlanksItemMaterial, OPALItemMaterialInfo<FillOutBlanksItemMaterial>> {

	private static final String BLANK = "<_____>";

	private static List<String> splitWithPattern(String input) {
		List<String> parts = new ArrayList<>();
		int lastIndex = 0;

		while (true) {
			int startIndex = input.indexOf(BLANK, lastIndex);
			if (startIndex == -1) {
				parts.add(input.substring(lastIndex));
				break;
			}
			parts.add(input.substring(lastIndex, startIndex));
			parts.add(input.substring(startIndex, startIndex + BLANK.length())); // add blank
			lastIndex = startIndex + BLANK.length();
		}
		return parts;
	}

	@Override
	protected void createResponseDeclaration(Document document, Element root,
			OPALItemMaterialInfo<FillOutBlanksItemMaterial> item) {
		int i = 1;
		for (var blank : item.getOriginalMaterial().getBlanks()) {
			var responseDeclaration = document.createElement("responseDeclaration");
			responseDeclaration.setAttribute("identifier", STR."RESPONSE_\{i++}");
			responseDeclaration.setAttribute("cardinality", "single");
			responseDeclaration.setAttribute("baseType", "string");
			var correctResponse = document.createElement("correctResponse");
			var value = elementWithText(document, "value", blank.getCorrectAnswer());
			correctResponse.appendChild(value);
			responseDeclaration.appendChild(correctResponse);
			var mapping = document.createElement("mapping");
			responseDeclaration.appendChild(mapping);
			mapping.setAttribute("defaultValue", "0");
			var caseSensitive = blank.isCaseSensitive() ? "true" : "false";
			var mapEntry = document.createElement("mapEntry");
			mapEntry.setAttribute("mapKey", blank.getCorrectAnswer());
			mapEntry.setAttribute("mappedValue", "1");
			mapEntry.setAttribute("caseSensitive", caseSensitive);
			mapping.appendChild(mapEntry);
			for (var alternative : blank.getAlternativeAnswers()) {
				mapEntry = document.createElement("mapEntry");
				mapEntry.setAttribute("mapKey", alternative);
				mapEntry.setAttribute("mappedValue", "1");
				mapEntry.setAttribute("caseSensitive", caseSensitive);
				mapping.appendChild(mapEntry);
			}
		}

	}

	@Override
	protected void createItemBody(Document document, Element root,
			OPALItemMaterialInfo<FillOutBlanksItemMaterial> item) {
		var itemBody = document.createElement("itemBody");
		var body = item.getOriginalMaterial().getBody();
		var parts = splitWithPattern(body);
		var content = document.createElement("p");
		itemBody.appendChild(content);
		int i = 1;
		for (var part : parts) {
			if (part.matches(BLANK)) { // blank replaced
				var blank = document.createElement("textEntryInteraction");
				blank.setAttribute("responseIdentifier", STR."RESPONSE_\{i++}");
				blank.setAttribute("class", "ctest_l ctest_r"); // todo add placeholder in blank
				content.appendChild(blank);
			} else {
				content.appendChild(document.createTextNode(part)); // normal text
			}
		}
	}
}
