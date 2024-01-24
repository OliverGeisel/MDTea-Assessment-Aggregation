package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;

import java.util.Collection;
import java.util.List;

public class ElementGenerator {

	public static final String TERM        = "TERM";
	public static final String TEXT        = "TEXT";
	public static final String IMAGE       = "IMAGE";
	public static final String DEFINITION  = "DEFINITION";
	public static final String FACT        = "FACT";
	public static final String PROOF       = "PROOF";
	public static final String EXERCISE    = "EXERCISE";
	public static final String EXAMPLE     = "EXAMPLE";
	public static final String EXPLANATION = "EXPLANATION";
	public static final String NODE        = "NODE";
	public static final String CODE        = "CODE";
	public static final String STATEMENT   = "STATEMENT";

	private ElementGenerator() {
	}

	/**
	 * Creates a new KnowledgeElement of the given type.
	 *
	 * @param type        The type of the element
	 * @param id          The id of the element
	 * @param structureId The structure of the element (Not used yet)
	 * @param content     The content of the element
	 * @param relations   The relations of the element
	 * @return The created element
	 */
	public static KnowledgeElement create(String type, String id, String structureId, String content,
			Collection<Relation> relations) {
		var kType = type.toUpperCase();
		var element = switch (kType) {
			case FACT -> new Fact(content, id, type, relations);
			case DEFINITION -> new Definition(content, id, type, relations);
			case TERM -> new Term(content, id, type, relations);
			case PROOF -> new Proof(content, id, type, relations);
			case EXERCISE -> new Exercise(content, id, type, relations);
			case EXPLANATION -> new Explanation(content, id, type, relations);
			case EXAMPLE -> new Example(content, id, type, relations);
			case STATEMENT -> new Statement(content, id, type, relations);
			case NODE -> new NodeElement(content, id, type, relations);
			case TEXT -> new Text(content, id, type, relations);
			case IMAGE -> new Image(content, id, type, relations);
			case CODE -> new Code(content, id, type, relations);
			default -> new CustomElement(content, id, "CUSTOM", relations, type);
		};
		element.setStructureId(structureId);
		return element;
	}

	/**
	 * Creates a new KnowledgeElement of the given type.
	 *
	 * @param type      The type of the element
	 * @param id        The id of the element
	 * @param structure The structure of the element (Not used yet)
	 * @param content   The content of the element
	 * @return The created element
	 */
	public static KnowledgeElement create(String type, String id, String structure, String content) {
		return create(type, id, structure, content, List.of());
	}
}
