package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;


import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

public class BasicRelation extends Relation {

	public BasicRelation(RelationType type, String from, String to) {
		super(idFromName(type, from, to), from, to, type);
	}

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
		return from + "_" + type.name().toUpperCase() + "_" + to;
	}

	static String idFromName(String type, String from, String to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to must not be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		return from + "_" + type.toUpperCase() + "_" + to;
	}

}
