package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.*;

@Entity
public class DefinitionTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("values.definition");
		allFields.add("values.relatedTerms");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	@Embedded
	@AttributeOverride(name = "definition", column = @Column(name = "definition_definition"))
	@AttributeOverride(name = "relatedTerms", column = @Column(name = "definition_related_term"))
	private DefinitionContent values;

	public DefinitionTemplate(UUID mainTerm) {
		super(TemplateType.DEFINITION, mainTerm);
	}

	public DefinitionTemplate() {
		super(TemplateType.DEFINITION);
	}

	//region setter/getter
	public String getDefinition() {
		return values.getDefinition();
	}

	public void setDefinition(String definition) {
		this.values.setDefinition(definition);
	}

	public void setRelatedTerms(List<String> terms) {
		values.clearRelatedTerms();
		terms.forEach(t -> values.addTermId(UUID.fromString(t)));
	}
//endregion

}
