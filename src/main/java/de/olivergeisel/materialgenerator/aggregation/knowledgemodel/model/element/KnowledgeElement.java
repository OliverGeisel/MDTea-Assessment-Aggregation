package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;


/**
 * A KnowledgeElement is a part of the KnowledgeGraph. It can be a Term, Definition, Example, Image, Text and so on.
 * It has a content and a type. The content is the actual content of the element and the type is the type of the element.
 * Every element can have {@link Relation}s to other elements. And every element is linked to a
 * {@link KnowledgeObject} by their id.
 *
 * @author Oliver Geisel
 * @version 0.1.0
 * @see KnowledgeType
 * @see Relation
 * @see KnowledgeObject
 * @since 0.1.0
 */
@Node
public abstract class KnowledgeElement {

	/**
	 * The id of the structure point this element belongs to.
	 */
	protected String        structureId;
	@Relationship
	private   Set<Relation> relations = new HashSet<>();
	/**
	 * The id of the element in the knowledge base.
	 */
	@Id
	private   String        id;
	private   KnowledgeType type;
	/**
	 * The content of the element. Meaning depends on the type of the element.
	 */
	private   String        content;

	protected KnowledgeElement() {

	}

	protected KnowledgeElement(String content, String id, String type, Collection<Relation> relations) {
		this.content = content;
		this.id = id;
		this.type = KnowledgeType.valueOf(type.toUpperCase());
		this.relations.addAll(relations);
	}

	protected KnowledgeElement(String content, String id, String type) {
		this.content = content;
		if (id == null || id.isBlank()) {
			id = STR."\{UUID.randomUUID()}-\{type.toUpperCase()}";
		}
		this.id = id;
		this.type = KnowledgeType.valueOf(type.toUpperCase());
	}

	public boolean addRelations(Set<Relation> relations) {
		boolean result = false;
		for (Relation relation : relations) {
			result |= addRelation(relation);
		}
		return result;
	}

	public boolean addRelation(Relation relation) {
		if (relations.stream().anyMatch(r -> r.isSameAs(relation))) {
			return false;
		}
		return this.relations.add(relation);
	}

	public boolean removeRelation(Relation relation) {
		return this.relations.remove(relation);
	}

	public boolean removeRelations(Set<Relation> relations) {
		return this.relations.removeAll(relations);
	}

	public boolean hasRelations() {
		return !relations.isEmpty();
	}

	public boolean hasRelation(Relation relation) {
		return relations.contains(relation);
	}

	public boolean hasRelationTo(KnowledgeElement element) {
		return relations.stream().anyMatch(r -> r.getTo().equals(element));
	}

	public boolean hasType(KnowledgeType type) {
		return this.type.equals(type);
	}

	/**
	 * Edit the id of the element.
	 * Is not named setId because this is a more powerful method to assure the id is unique and not illegal.
	 * This method assures the id is not null, not blank and ends with the type of the element.
	 * <p>
	 * <b>Warning:</b> This method should only be used if the element is not persisted yet or if the id is
	 * illegal.
	 *
	 * @param id the new id
	 */
	public void editId(String id) {
		if (this.id.equals(id)) return;
		if (id == null || id.isBlank()) {
			id = STR."\{UUID.randomUUID()}-\{type.name()}";
		}
		if (!id.endsWith(STR."-\{type.name()}")) {
			id = id.concat(STR."-\{type.name()}");
		}
		this.id = id;
	}
	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public Set<Relation> getRelations() {
		return relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations.clear();
		this.relations.addAll(relations);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	//region setter/getter

	public KnowledgeType getType() {
		return type;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeElement that)) return false;

		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return STR."KnowledgeElement{id='\{id}', type=\{type}, relations=\{relations}, structureId='\{structureId}', content='\{content}'}";
	}

}
