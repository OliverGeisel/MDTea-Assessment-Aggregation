package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

/**
 * A Relation is a connection between two {@link KnowledgeElement}s.
 * <p>
 * A Relation is always directed.
 * A Relation has a name, a source and a target.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see KnowledgeElement
 * @see RelationType
 * @since 0.2.0
 */
@Node
public abstract class Relation {

	@Id
	private UUID id = UUID.randomUUID();
	private RelationType     type;
	private String           name;
	private String           fromId;
	private String           toId;
	@Relationship("FROM")
	private KnowledgeElement from;
	@Relationship("TO")
	private KnowledgeElement to;

	protected Relation() {
		this.type = RelationType.CUSTOM;
		this.name = "";
		this.fromId = "";
		this.toId = "";
	}

	protected Relation(String name, String fromId, String toId, RelationType type) {
		if (fromId == null || toId == null || type == null || name == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		this.type = type;
		this.fromId = fromId;
		this.toId = toId;
		this.name = name;
	}

	protected Relation(String name, KnowledgeElement from, KnowledgeElement to, RelationType type) {
		if (from == null || to == null || type == null || name == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		this.type = type;
		this.from = from;
		this.to = to;
		this.fromId = from.getId();
		this.toId = to.getId();
		this.name = name;
	}

	/**
	 * Check if the relation is a reverse of the given relation
	 *
	 * @param other relation to compare with
	 * @return true if the relation is a reverse of the given relation
	 */
	public boolean isReverseFrom(Relation other) {
		if (other == null) {
			return false;
		}
		var isReverseType = this.type.getInverted().equals(other.type);
		if (!isReverseType) {
			return false;
		}
		return this.fromId.equals(other.toId) && this.toId.equals(other.fromId);
	}

	public boolean hasType(RelationType type) {
		return this.type.equals(type);
	}

	/**
	 * Check if this relation is the same as another relation.
	 * Ignores the id of the relation.
	 *
	 * @param relation the relation to compare
	 * @return true if the relations are the same
	 */
	public boolean isSameAs(Relation relation) {
		if (this == relation) return true;
		if (relation == null) return false;
		if (!type.equals(relation.type)) return false;
		if (!name.equals(relation.name)) return false;
		if (!toId.equals(relation.toId)) return false;
		return fromId.equals(relation.fromId);
	}

	//region setter/getter
	public String getFromId() {
		return fromId;
	}

	public String getToId() {
		return toId;
	}

	public KnowledgeElement getFrom() {
		return from;
	}

	/**
	 * Set the source of the relation
	 *
	 * @param from source of the relation (must not be null)
	 * @throws IllegalArgumentException if from is null or the id is null or does not match fromId
	 */
	public void setFrom(KnowledgeElement from) {
		if (from == null) {
			throw new IllegalArgumentException("from must not be null");
		}
		if (from.getId() == null || !from.getId().equals(fromId)) {
			throw new IllegalArgumentException("from.id must not be null and must match fromId");
		}
		this.from = from;
		fromId = from.getId();
	}

	/**
	 * Check if the relation is incomplete
	 *
	 * @return true if the relation is incomplete
	 */
	public boolean isIncomplete() {
		return from == null || to == null || fromId == null || toId == null;
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
		if (to.getId() == null || !to.getId().equals(toId)) {
			throw new IllegalArgumentException("to.id must not be null and must match toId");
		}
		this.to = to;
		toId = to.getId();
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
		if (!(o instanceof Relation relation)) return false;

		if (!name.equals(relation.name)) return false;
		if (!fromId.equals(relation.fromId)) return false;
		return toId.equals(relation.toId);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + fromId.hashCode();
		result = 31 * result + toId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return STR."Relation \{name}: \{fromId} → \{toId}";
	}
}
