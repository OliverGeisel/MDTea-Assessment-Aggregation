package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.configuration.TaskConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;


/**
 * Basic Material class for {@link Material} that are used for {@link Task}s or is a Material extracted from
 * a part of a {@link KnowledgeModel}.
 */
@Entity
public abstract class TaskMaterial extends Material {

	private TaskType          taskType;
	@Embedded
	private TaskConfiguration taskConfiguration;
	private String            taskParameters;

	protected TaskMaterial() {
		super();
	}

	protected TaskMaterial(TaskConfiguration taskConfiguration, String taskParameters, TemplateInfo templateInfo) {
		super(MaterialType.TASK, templateInfo);
		this.taskConfiguration = taskConfiguration;
		taskType = taskConfiguration.getForTaskType();
		this.taskParameters = taskParameters;
	}



}
