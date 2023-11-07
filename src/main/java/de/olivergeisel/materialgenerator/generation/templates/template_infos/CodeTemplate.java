package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class CodeTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("code");
		allFields.add("language");
		allFields.add("headline");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String headline;
	private String language;
	private String code;

	public CodeTemplate(String language, String code) {
		super(TemplateType.CODE);
		this.language = language;
		this.code = code;
	}

	public CodeTemplate() {
		super(TemplateType.CODE);
	}

	//region setter/getter
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
//endregion
}
