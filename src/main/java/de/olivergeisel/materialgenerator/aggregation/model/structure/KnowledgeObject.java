package de.olivergeisel.materialgenerator.aggregation.model.structure;


import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Node
public abstract class KnowledgeObject {
	@Relationship
	private final Set<KnowledgeElement> linkedElements;
	@Id
	private final String                id;

	protected KnowledgeObject(String id) {
		this.id = id;
		linkedElements = new HashSet<>();
	}

	public boolean linkElement(KnowledgeElement element) {
		return linkedElements.add(element);
	}

	//region setter/getter
	public String getId() {
		return id;
	}

	public String getIdUnified() {
		return id.toUpperCase().trim().replace('_', ' ').replace('-', ' ');
	}

	public Set<KnowledgeElement> getLinkedElements() {
		return Collections.unmodifiableSet(linkedElements);
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
		return "KnowledgeObject{" +
			   "id='" + id + '\'' +
			   ", linkedElements size=" + linkedElements.size() +
			   '}';
	}
}
