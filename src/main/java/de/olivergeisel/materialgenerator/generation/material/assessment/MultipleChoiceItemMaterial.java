package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.MultipleChoiceItem;
import de.olivergeisel.materialgenerator.generation.configuration.MultipleChoiceConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multiple choice item. A multiple choice item has a question and a list of correct and wrong answers.
 * <p>
 * The user has to choose the correct answers from a list of alternatives.
 * It is possible to define how many correct answers are possible and if the alternatives are shuffled.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ItemMaterial
 * @see MultipleChoiceConfiguration
 * @see MultipleChoiceItem
 * @since 1.1.0
 */
@Getter
@Entity
public class MultipleChoiceItemMaterial extends ItemMaterial {

	private static final MultipleChoiceConfiguration DEFAULT_CONFIGURATION = new MultipleChoiceConfiguration();

	private String       question;
	@ElementCollection
	private List<String> correctAnswers;
	@ElementCollection
	private List<String> alternativeAnswers;
	@Embedded
	private MultipleChoiceConfiguration itemConfiguration;

	protected MultipleChoiceItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.MULTIPLE_CHOICE);
		itemConfiguration = DEFAULT_CONFIGURATION;
	}

	/**
	 * Creates a new multiple choice item.
	 *
	 * @param question           the question or task of the item
	 * @param correctAnswer      the correct answer or answers must at least contain one answer
	 * @param alternativeAnswers the alternative answers (wrong answers). Can be null -> all answers are correct
	 * @param itemConfiguration  the item configuration for this item
	 * @throws IllegalArgumentException if the correct answer list is null or empty, or the number of correct answers is smaller than the number of correct choices in the configuration
	 */
	public MultipleChoiceItemMaterial(String question, List<String> correctAnswer, List<String> alternativeAnswers,
			MultipleChoiceConfiguration itemConfiguration) throws IllegalArgumentException {
		super(itemConfiguration, TemplateType.MULTIPLE_CHOICE);
		this.question = question;
		if (correctAnswer == null || correctAnswer.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one correct answer");
		}
		this.correctAnswers = correctAnswer;
		if (alternativeAnswers == null) {
			alternativeAnswers = new ArrayList<>();
		}
		this.alternativeAnswers = alternativeAnswers;
		this.itemConfiguration = itemConfiguration;
		if (itemConfiguration.getNumberOfCorrectChoices() > correctAnswer.size()) {
			throw new IllegalArgumentException("The number of correct answers is too small");
		}
		while (alternativeAnswers.size() + correctAnswer.size() > itemConfiguration.getNumberOfChoices()) {
			alternativeAnswers.removeLast();
		}
	}

	//region setter/getter
	public boolean isShuffle() {
		if (getItemConfiguration() instanceof MultipleChoiceConfiguration config) {
			return config.isShuffle();
		}
		return false;
	}
//endregion
}
