package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Task;

public class TaskPromptAnswer extends PromptAnswer<Task> {

	public TaskPromptAnswer(TaskPrompt prompt, String answer, DeliverType deliverType) {
		super(prompt, answer, deliverType);
	}

	public TaskPromptAnswer(TaskPrompt prompt, String answer) {
		super(prompt, answer, DeliverType.MULTIPLE);
	}

	public TaskPromptAnswer(TaskPrompt prompt) {
		super(prompt, DeliverType.MULTIPLE);
	}
}
