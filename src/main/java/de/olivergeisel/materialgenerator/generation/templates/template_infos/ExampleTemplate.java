package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class ExampleTemplate extends BasicTemplate {
	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("example");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String example;

	public ExampleTemplate(UUID mainTermId, String example) {
		super(TemplateType.EXAMPLE, mainTermId);
		this.example = example;
	}

	public ExampleTemplate(UUID mainTermId) {
		super(TemplateType.EXAMPLE, mainTermId);
	}

	public ExampleTemplate() {
		super(TemplateType.EXAMPLE);
	}

	//region setter/getter
	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
//endregion

}
