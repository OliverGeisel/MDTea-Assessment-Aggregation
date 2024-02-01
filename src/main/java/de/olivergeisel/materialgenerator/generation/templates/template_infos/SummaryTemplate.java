package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SummaryTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("definitions");
		allFields.add("term");
		allFields.add("examples");
		allFields.add("lists");
		allFields.add("texts");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String term;
	private String definitions;
	private String examples;
	private String lists;
	private String texts;

	public SummaryTemplate() {
		super(TemplateType.SUMMARY);
	}

	public SummaryTemplate(String term, String definitions, String examples, String lists, String texts) {
		super(TemplateType.SUMMARY);
		this.term = term;
		this.definitions = definitions;
		this.examples = examples;
		this.lists = lists;
		this.texts = texts;
	}

}
