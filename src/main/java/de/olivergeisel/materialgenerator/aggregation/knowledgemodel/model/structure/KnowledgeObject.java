package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Node
public abstract class KnowledgeObject {

	@Relationship("LINKED_TO")
	private Set<KnowledgeElement> linkedElements;

	/**
	 * Links a KnowledgeElement to this KnowledgeObject
	 *
	 * @param element the element to link
	 * @return true if the element was not already linked
	 */
	public boolean linkElement(KnowledgeElement element) {
		return linkedElements.add(element);
	}

	@Id
	@GeneratedValue(GeneratedValue.UUIDGenerator.class)
	private String                id;

	protected KnowledgeObject() {
		this.id = "";
		this.linkedElements = new HashSet<>();
	}

	protected KnowledgeObject(String id) {
		this.id = id;
		linkedElements = new HashSet<>();
	}

	//region setter/getter
	public String getId() {
		return id;
	}
	public String getName() {
		return id;
	}

	public String getIdUnified() {
		return id.toUpperCase().trim().replace('_', ' ').replace('-', ' ');
	}

	public Set<KnowledgeElement> getLinkedElements() {
		return Collections.unmodifiableSet(linkedElements);
	}

	public void setLinkedElements(Set<KnowledgeElement> linkedElements) {
		this.linkedElements.clear();
		this.linkedElements.addAll(linkedElements);
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeObject that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}


	@Override
	public String toString() {
		return STR."KnowledgeObject{id='\{id}', linkedElements size=\{linkedElements.size()}'}";
	}
}
