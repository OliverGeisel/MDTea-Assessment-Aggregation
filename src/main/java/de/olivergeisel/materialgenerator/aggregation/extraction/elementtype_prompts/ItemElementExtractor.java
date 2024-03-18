package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * The ItemElementExtractor is used to extract {@link Item} elements from {@link ItemPromptAnswer}s.
 * It is used to extract Task elements from {@link PromptAnswer}s that are of the type ItemPromptAnswer.
 * <p>
 * This extractor should be able to extract <b>all</b> different Types of {@link Item} elements.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Item
 * @see ItemPromptAnswer
 * @see PromptAnswer
 * @since 1.1.0
 */
public class ItemElementExtractor extends ElementExtractor<Item, ItemPromptAnswer> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ItemElementExtractor.class);

	/**
	 * Extracts a KnowledgeElement from a PromptAnswer.
	 *
	 * @param answer The PromptAnswer that is used to extract the KnowledgeElement.
	 * @return The extracted KnowledgeElement.
	 * @throws WrongExtractionMethodException if the PromptAnswer is not the right type for this method.
	 */
	@Override
	public Item extract(ItemPromptAnswer answer) throws WrongExtractionMethodException {
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
	public List<Item> extractAll(ItemPromptAnswer answers, GPT_Request.ModelLocation modelLocation)
			throws WrongExtractionMethodException {
		if (answers.getDeliverType() != DeliverType.MULTIPLE) {
			throw new WrongExtractionMethodException("The DeliverType of the PromptAnswer is not MULTIPLE");
		}

		final var format = answers.getPrompt().getWantedFormat();
		List<Item> back = new LinkedList<>();

		final var rawPotentialTaskLines = getPossibleAnswers(answers, modelLocation);
		for (var line : rawPotentialTaskLines) {
			final var potentialTask = line.split("\\|");
			if (line.strip().length() < 4 || potentialTask.length < 3) continue;
			String type = potentialTask[0].replaceAll(ElementPrompt.START_CHARS_STRING_REGEX, "").strip();
			type = type.replace("\\", ""); // Remove a strange character that is sometimes added
			String task = potentialTask[1].strip();
			String options = potentialTask[2].strip();
			try {
				var item = selectItemType(type, task, options);
				if (item != null) {
					back.add(item);
				}
			} catch (IllegalArgumentException e) {
				LOGGER.debug("The Item could not be created. {}", e.getMessage());
			}

		}
		return back;
	}


	private Item selectItemType(String type, String task, String options) {
		return switch (type) {
			// Todo implement correct
			case "SINGLE_CHOICE" -> {
				var separatedOptions = Arrays.asList(options.split(";"));
				yield new SingleChoiceItem(task, separatedOptions, "");
			}
			case "MULTIPLE_CHOICE" -> {
				var separatedOptions = options.split(";");
				var correctOptions = new LinkedList<String>();
				var wrongOptions = new LinkedList<String>();
				for (String separatedOption : separatedOptions) {
					if (separatedOption.contains("(c)")) {
						correctOptions.add(separatedOption.replace("(c)", "").strip());
					} else {
						wrongOptions.add(separatedOption.strip());
					}
				}
				var complete = new LinkedList<>(correctOptions);
				complete.addAll(wrongOptions);
				yield new MultipleChoiceItem(task, complete, correctOptions.size(), "");
			}
			case "TRUE_FALSE" -> new TrueFalseItem(task, Boolean.parseBoolean(options), "");
			case "FILL_OUT_BLANKS" -> {
				var separatedOptions = options.split(";");
				Item back = null;
				try {
					back = new FillOutBlanksItem(task, Arrays.asList(separatedOptions), "");
				} catch (IllegalArgumentException e) {
					LOGGER.info(STR."The FillOutBlanksItem could not be created. \{e.getMessage()}");
				}
				yield back;
			}
			default -> null;
		};
	}
}
