package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class DefinitionContent {

	@ElementCollection
	private final List<UUID> relatedTerms = new LinkedList<>();
	String definition;

	public DefinitionContent(String definition) {
		this.definition = definition;
	}

	protected DefinitionContent() {

	}

	public boolean addTermId(UUID id) {
		return relatedTerms.add(id);
	}

	public boolean removeTermId(UUID id) {
		return relatedTerms.remove(id);
	}

	public void clearRelatedTerms() {
		relatedTerms.clear();
	}

	//region setter/getter
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public List<UUID> getRelatedTerms() {
		return relatedTerms;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefinitionContent that)) return false;

		if (!relatedTerms.equals(that.relatedTerms)) return false;
		return Objects.equals(definition, that.definition);
	}

	@Override
	public int hashCode() {
		int result = relatedTerms.hashCode();
		result = 31 * result + (definition != null ? definition.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "DefinitionContent{" + "relatedTerms=" + relatedTerms + ", definition='" + definition + '\'' + '}';
	}
}
