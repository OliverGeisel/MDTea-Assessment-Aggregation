package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;

import java.util.*;

/**
 * Special version of a {@link ElementParser}. It is used to parse an Item from a JSON-File to a concrete Object of
 * the wanted {@link Item}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Item
 * @see ElementParser
 * @see KnowledgeElement
 * @since 1.1.0
 */
public class ItemParser {

	public static final String ITEM = "ITEM";

	private ItemParser() {
	}

	/**
	 * Creates a new Item of the given type.
	 *
	 * @param type        The type of the element
	 * @param id          The id of the element
	 * @param structureId The structure of the element (Not used yet)
	 * @param content     The content of the element
	 * @param relations   The relations of the element
	 * @return The created element
	 */
	public static Item create(String type, String content, String id, String structureId,
			Collection<Relation> relations) throws UnsupportedElementType {
		if (type == null || id == null || content == null) {
			throw new IllegalArgumentException("The type, id and content of an Item must not be null.");
		}
		if (type.isBlank() || !type.equalsIgnoreCase(ITEM)) {
			throw new IllegalArgumentException("The type of an Item must be 'ITEM'.");
		}
		var parsedContent = parseContent(content);
		var itemType = parsedContent.get("T").getFirst();
		var typeEnum = ItemType.valueOf(itemType.toUpperCase(Locale.ROOT));
		Item back = switch (typeEnum) {
			case TRUE_FALSE -> new TrueFalseItem(parsedContent.get("Q").getFirst(),
					Boolean.parseBoolean(parsedContent.get("C").getFirst()), id);
			case SINGLE_CHOICE -> new SingleChoiceItem(parsedContent.get("Q").getFirst(),
					parsedContent.get("A"), id);
			case MULTIPLE_CHOICE -> new MultipleChoiceItem(parsedContent.get("Q").getFirst(),
					parsedContent.get("A"), Integer.parseInt(parsedContent.get("C").getFirst()),
					Boolean.parseBoolean(parsedContent.get("S").getFirst()), id);
			case FILL_OUT_BLANKS -> null;
			case ASSIGNMENT, UNDEFINED -> null;
		};
		if (back != null) {
			back.setStructureId(structureId);
			back.setRelations(new HashSet<>(relations));
		}
		return back;
	}

	private static Map<String, List<String>> parseContent(String content) throws IllegalArgumentException {
		var back = new HashMap<String, List<String>>();
		var parts = content.split(";");
		for (var part : parts) {
			var keyValue = part.split(":");
			if (keyValue.length != 2) {
				continue;
			}

			back.computeIfAbsent(keyValue[0], it -> new ArrayList<>()).add(keyValue[1]);
		}
		if (back.isEmpty() || !back.containsKey("T")) {
			throw new IllegalArgumentException("The content of an Item must contain a type (T) of the Item.");
		}
		return back;
	}
}
