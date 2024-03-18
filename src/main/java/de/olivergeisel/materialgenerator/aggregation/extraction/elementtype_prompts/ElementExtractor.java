package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.util.List;
import java.util.Map;

/**
 * An ElementExtractor is a class that extracts a KnowledgeElement from a PromptAnswer.
 *
 * @param <T> The type of the KnowledgeElement that is extracted.
 * @param <A> The type of the PromptAnswer that is used to extract the KnowledgeElement.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see PromptAnswer
 * @since 1.1.0
 */
public abstract class ElementExtractor<T extends KnowledgeElement, A extends PromptAnswer<T>> {

	/**
	 * Extracts a KnowledgeElement from a PromptAnswer.
	 *
	 * @param answer The PromptAnswer that is used to extract the KnowledgeElement.
	 * @return The extracted KnowledgeElement.
	 * @throws WrongExtractionMethodException if the PromptAnswer is not the right type for this method.
	 */
	public abstract T extract(A answer) throws WrongExtractionMethodException;

	/**
	 * Extracts a List of KnowledgeElements from a PromptAnswer.
	 *
	 * @param answers The PromptAnswer that is used to extract the KnowledgeElements.
	 * @param modelLocation The location of the model that was used to generate the PromptAnswer.
	 * @return A List of extracted KnowledgeElements.
	 * @throws WrongExtractionMethodException if the PromptAnswer is not the right type for this method.
	 */
	public abstract List<T> extractAll(A answers, GPT_Request.ModelLocation modelLocation)
			throws WrongExtractionMethodException;

	/**
	 * In a Remote request the choices are the promptAnswer for the prompt. So the model gives different promptAnswer to the
	 * same request.
	 * @param promptAnswer the promptAnswer that was given by the model.
	 * @return A List of all choices (answers) to the prompt.
	 * @throws RuntimeException if the json is not valid.
	 */
	protected List<Map> getChoices(A promptAnswer) throws RuntimeException {
		final var json = promptAnswer.getAnswer();
		var parser = new JSONParser(json);
		try {
			var jsonObject = parser.parseObject();
			return (List<Map>) jsonObject.get("choices");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extract all possible answers from a promptAnswer. At moment, it is only used for remote requests.
	 * It will only get the first choice and extract the text from it.
	 *
	 * @param answers       the promptAnswer where the parts are extracted from.
	 * @param modelLocation the location of the model that was used to generate the promptAnswer.
	 * @return A List of all parts of the promptAnswer.
	 */
	protected String[] getPossibleAnswers(A answers, GPT_Request.ModelLocation modelLocation) {
		String text;
		if (modelLocation == GPT_Request.ModelLocation.LOCAL) {
			text = answers.getAnswer();
		} else {
			var choices = getChoices(answers);
			var firstChoice = choices.getFirst();
			text = firstChoice.get("text").toString().replace("\\r", "");
		}
		return text.split("\\\\n");
	}
}
