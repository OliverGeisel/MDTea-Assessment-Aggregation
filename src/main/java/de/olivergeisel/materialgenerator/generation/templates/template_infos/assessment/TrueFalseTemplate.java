package de.olivergeisel.materialgenerator.generation.templates.template_infos.assessment;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.DefinitionContent;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.*;

@Entity
public class TrueFalseTemplate extends AssessmentTemplate{


	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(AssessmentTemplate.FIELDS);
		allFields.add("statement");
		allFields.add("isCorrect");
		allFields.add("reason");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private final String statement;
	private final boolean isCorrect;
	private final String reason;



	public TrueFalseTemplate(UUID mainTerm) {
		super(TaskType.TRUE_FALSE, mainTerm);
		this.statement = "";
		this.isCorrect = true;
		this.reason = "";
	}

	public TrueFalseTemplate() {
		super(TaskType.TRUE_FALSE, null);
		this.statement = "";
		this.isCorrect = true;
		this.reason = "";
	}


	//region setter/getter
	public String getStatement() {
		return statement;
	}

	public boolean isCorrect() {
		return isCorrect;
	}
//endregion
}
