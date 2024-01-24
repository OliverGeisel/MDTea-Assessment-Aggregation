package de.olivergeisel.materialgenerator.generation.templates.template_infos.assessment;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.BasicTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public abstract class AssessmentTemplate extends BasicTemplate {
	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("taskType");
		allFields.add("taskParameters");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private final TaskType taskType;

	protected AssessmentTemplate(TaskType taskType, UUID mainTermId) {
		super(TemplateType.TASK, mainTermId);
		this.taskType = taskType;
	}


	protected AssessmentTemplate() {
		this.taskType = TaskType.UNDEFINED;
	}
}
