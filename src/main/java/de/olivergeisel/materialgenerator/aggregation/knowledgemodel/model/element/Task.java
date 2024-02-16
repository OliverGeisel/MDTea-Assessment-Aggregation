package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;
import java.util.UUID;

@Node
public class Task extends KnowledgeElement {

	private String task;

	private List<String> answers;

	protected Task() {
		super();
	}

	public Task(String task, String content, String answers) {
		super(task, STR."\{UUID.randomUUID().toString()}-TASK", "TASK");
		this.task = task;
		this.answers = List.of(answers.split(","));
	}

//region setter/getter
	public String getTask() {
		return task;
	}
//endregion
}
