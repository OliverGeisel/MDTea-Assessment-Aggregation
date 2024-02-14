package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.Node;


/**
 * Represents a basic relation between two elements.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Relation
 * @see RelationType
 * @see KnowledgeElement
 * @since 0.2.0
 */
@Node
public class BasicRelation extends Relation {

	protected BasicRelation() {

	}

	/**
	 * Create a new Relation with the given parameters.
	 *
	 * @param type Type of relation. @see RelationType
	 * @param from Source of the relation
	 * @param to   Target of the relation
	 */
	public BasicRelation(RelationType type, String from, String to) {
		super(idFromName(type, from, to), from, to, type);
	}

	/**
	 * Create a new Relation with the given parameters.
	 *
	 * @param type Type of relation. @see RelationType
	 * @param from Source of the relation
	 * @param to   Target of the relation
	 */
	public BasicRelation(RelationType type, KnowledgeElement from, KnowledgeElement to) {
		super(idFromName(type, from.getId(), to.getId()), from, to, type);
	}

	/**
	 * Help Method to find the id for the Relation you want
	 *
	 * @param type Type of relation. @see RelationType
	 * @param from Source of the relation
	 * @param to   Target of the relation
	 * @return id of the relation
	 */
	static String idFromName(RelationType type, String from, String to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to must not be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		return STR."\{from}_\{type.name().toUpperCase()}_\{to}";
	}

	static String idFromName(String type, String from, String to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to must not be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		return STR."\{from}_\{type.toUpperCase()}_\{to}";
	}

}
