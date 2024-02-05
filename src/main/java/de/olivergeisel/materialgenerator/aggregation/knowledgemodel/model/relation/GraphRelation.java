package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * A GraphRelation is a connection between two {@link KnowledgeElement}s.
 * <p>
 * A GraphRelation is always directed.
 * A GraphRelation has a name, a source and a target.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see KnowledgeElement
 * @see RelationType
 * @since 0.2.0
 */
@RelationshipProperties
public abstract class GraphRelation {

	@Id
	@GeneratedValue
	private Long id;

	private RelationType type;
	private String       name;

	@TargetNode()
	private KnowledgeElement to;

	protected GraphRelation() {
		this.type = RelationType.CUSTOM;
		this.name = "unknown";
	}

	protected GraphRelation(String name, String fromId, String toId, RelationType type) {
		if (fromId == null || toId == null || type == null || name == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		this.type = type;
		this.name = name;
	}

	protected GraphRelation(String name, KnowledgeElement from, KnowledgeElement to, RelationType type) {
		if (from == null || to == null || type == null || name == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		this.type = type;
		this.to = to;
		this.name = name;
	}

	public boolean hasType(RelationType type) {
		return this.type.equals(type);
	}

	//region setter/getter


	public String getToId() {
		return to.getId();
	}


	/**
	 * Check if the relation is incomplete
	 *
	 * @return true if the relation is incomplete
	 */
	public boolean isIncomplete() {
		return to == null;
	}

	public boolean isComplete() {
		return !isIncomplete();
	}

	/**
	 * Get the name of the relation
	 *
	 * @return name of the relation
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the target of the relation
	 *
	 * @return target of the relation
	 * @throws IllegalStateException if target is not set
	 */
	public KnowledgeElement getTo() throws IllegalStateException {
		if (to == null) {
			throw new IllegalStateException("to is not set");
		}
		return to;
	}

	/**
	 * Set the target of the relation
	 *
	 * @param to target of the relation (must not be null)
	 */
	public void setTo(KnowledgeElement to) {
		if (to == null) {
			throw new IllegalArgumentException("to must not be null");
		}
		if (to.getId() == null || !to.getId().equals(getToId())) {
			throw new IllegalArgumentException("to.id must not be null and must match toId");
		}
		this.to = to;
	}

	public RelationType getType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GraphRelation relation)) return false;

		if (!name.equals(relation.name)) return false;
		return to.equals(relation.to);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + to.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return STR."Relation \{name}: \{getToId()} → \{getToId()}";
	}
}
