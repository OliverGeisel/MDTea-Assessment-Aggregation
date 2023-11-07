package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProofTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("term");
		allFields.add("relatedTerms");
		FIELDS = Collections.unmodifiableSet(allFields);
	}
}
