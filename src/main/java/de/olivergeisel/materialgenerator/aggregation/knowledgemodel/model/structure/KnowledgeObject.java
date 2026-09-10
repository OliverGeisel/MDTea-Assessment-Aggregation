package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Node
public abstract class KnowledgeObject {

	@Relationship("LINKED_TO")
	private final Set<KnowledgeElement> linkedElements = new HashSet<>();
	@Version
	protected long   version;
	@Id
	private   String id;

	protected KnowledgeObject() {
	}

	protected KnowledgeObject(String id) {
		this.id = id;
	}

	/**
	 * Links a KnowledgeElement to this KnowledgeObject
	 *
	 * @param element the element to link
	 * @return true if the element was not already linked
	 */
	public boolean linkElement(KnowledgeElement element) {
		return linkedElements.add(element);
	}

	public void overrideId(String id) {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("id must not be null");
		}
		this.id = id;
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
		if (this == o) {
			return true;
		}
		if (!(o instanceof KnowledgeObject that)) {
			return false;
		}
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return STR."KnowledgeObject{id='\{id}', linkedElements size=\{linkedElements.size()}'}";
	}
}
