package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

import java.util.Collection;

/**
 * Represents a task in the knowledge base.
 * A {@link Task} is the base class for all tasks in the knowledge base. It contains the task type. Every subclass
 * has as KnowledgeType {@link KnowledgeType#TASK}. But the taskType is different.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TaskType
 * @see KnowledgeType
 * @see KnowledgeElement
 * @since 1.1.0
 */
public abstract class Task extends KnowledgeElement {

	private final TaskType taskType;

	protected Task(String content, String id, String taskType) {
		super(content, id, KnowledgeType.TASK.name());
		this.taskType = TaskType.valueOf(taskType.toUpperCase());
	}

	protected Task(String content, String id, String taskType, Collection<Relation> relations) {
		super(content, id, KnowledgeType.TEXT.name(), relations);
		this.taskType = TaskType.valueOf(taskType.toUpperCase());
	}

	//region setter/getter
	public TaskType getTaskType() {
		return taskType;
	}
//endregion


}
