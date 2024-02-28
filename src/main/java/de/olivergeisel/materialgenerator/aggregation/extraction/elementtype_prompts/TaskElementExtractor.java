package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Task;

import java.util.LinkedList;
import java.util.List;


/**
 * The TaskElementExtractor is used to extract {@link Task} elements from {@link TaskPromptAnswer}s.
 * It is used to extract Task elements from {@link PromptAnswer}s that are of the type TaskPromptAnswer.
 * <p>
 * This extractor should be able to extract <b>all</b> different Types of {@link Task} elements.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Task
 * @see TaskPromptAnswer
 * @see PromptAnswer
 * @since 1.1.0
 */
public class TaskElementExtractor extends ElementExtractor<Task, TaskPromptAnswer> {


	/**
	 * Extracts a KnowledgeElement from a PromptAnswer.
	 *
	 * @param answer The PromptAnswer that is used to extract the KnowledgeElement.
	 * @return The extracted KnowledgeElement.
	 * @throws WrongExtractionMethodException if the PromptAnswer is not the right type for this method.
	 */
	@Override
	public Task extract(TaskPromptAnswer answer) throws WrongExtractionMethodException {
		if (answer.getDeliverType() != DeliverType.SINGLE) {
			throw new WrongExtractionMethodException("The DeliverType of the PromptAnswer is not SINGLE");
		}
		return null;
	}

	/**
	 * Extracts a List of KnowledgeElements from a PromptAnswer.
	 *
	 * @param answers       The PromptAnswer that is used to extract the KnowledgeElements.
	 * @param modelLocation The location of the model that was used to generate the PromptAnswer.
	 * @return A List of extracted KnowledgeElements.
	 * @throws WrongExtractionMethodException if the PromptAnswer is not the right type for this method.
	 */
	@Override
	public List<Task> extractAll(TaskPromptAnswer answers, GPT_Request.ModelLocation modelLocation)
			throws WrongExtractionMethodException {
		if (answers.getDeliverType() != DeliverType.MULTIPLE) {
			throw new WrongExtractionMethodException("The DeliverType of the PromptAnswer is not MULTIPLE");
		}

		final var format = answers.getPrompt().getWantedFormat();
		List<Task> back = new LinkedList<>();

		final var rawPotentialTaskLines = getPossibleAnswers(answers, modelLocation);
		for (var line : rawPotentialTaskLines) {
			final var potentialTask = line.split("\\|");
			if (line.strip().length() < 4 || potentialTask.length < 3) continue;
			String type = potentialTask[0].replaceAll(ElementPrompt.START_CHARS_STRING_REGEX, "").strip();
			String task = potentialTask[1].strip();
			String options = potentialTask[2].strip();
			back.add(selectTaskType(type, task, options));
		}

		return back;
	}


	private Task selectTaskType(String type, String task, String options) {
		return switch (type) {
			case "SINGLE_CHOICE" -> new Task("", task, options);
			case "MULTIPLE_CHOICE" -> new Task("", task, options);
			case "TRUE_FALSE" -> new Task("", task, options);
			case "FILL_BLANK" -> new Task("", task, options);
			default -> null;
		};
	}
}
