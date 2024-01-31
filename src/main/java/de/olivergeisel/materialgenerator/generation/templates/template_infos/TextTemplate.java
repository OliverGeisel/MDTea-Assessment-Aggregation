package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class TextTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("textField");
		allFields.add("headline");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String textField;
	private String headline;

	public TextTemplate() {
		super(TemplateType.TEXT);
	}

	public TextTemplate(UUID mainTermId) {
		super(TemplateType.TEXT, mainTermId);
	}

	public TextTemplate(UUID mainTermId, String textField, String headline) {
		super(TemplateType.TEXT, mainTermId);
		this.textField = textField;
		this.headline = headline;
	}

	//region setter/getter
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}
//endregion
}
