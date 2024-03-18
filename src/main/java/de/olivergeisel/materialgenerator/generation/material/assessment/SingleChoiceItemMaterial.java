package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.SingleChoiceConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.List;

/**
 * A single choice item is an item where the user has to choose one correct answer from a list of alternatives.
 * <p>
 * The user has to choose the correct answer from a list of alternatives.
 * </p>
 * <p>
 * The configuration of the item defines if the alternatives are shuffled or not and how many alternatives are shown.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ItemMaterial
 * @see SingleChoiceConfiguration
 * @since 1.1.0
 */
@Entity
public class SingleChoiceItemMaterial extends ItemMaterial {

	private static final SingleChoiceConfiguration DEFAULT_CONFIGURATION = new SingleChoiceConfiguration();

	private String       question;
	private String       correctAnswer;
	@ElementCollection
	private List<String> alternativeAnswers;
	@Embedded
	private SingleChoiceConfiguration itemConfiguration;

	protected SingleChoiceItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.SINGLE_CHOICE);
		itemConfiguration = DEFAULT_CONFIGURATION;
	}

	public SingleChoiceItemMaterial(String question, String correctAnswer, List<String> alternativeAnswers,
			SingleChoiceConfiguration itemConfiguration) {
		super(itemConfiguration, TemplateType.SINGLE_CHOICE);
		this.question = question;
		this.correctAnswer = correctAnswer;
		this.alternativeAnswers = alternativeAnswers;
		this.itemConfiguration = itemConfiguration;
		while (alternativeAnswers.size() + 1 > itemConfiguration.getNumberOfChoices()) {
			alternativeAnswers.removeLast();
		}
	}

	//region setter/getter
	@Override
	public SingleChoiceConfiguration getItemConfiguration() {
		return itemConfiguration;
	}

	public String getQuestion() {
		return question;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public List<String> getAlternativeAnswers() {
		return alternativeAnswers;
	}

	public boolean isShuffle() {
		var itemConfiguration = getItemConfiguration();
		if (!(itemConfiguration instanceof SingleChoiceConfiguration configuration)) {
			return false;
		}
		return configuration.isShuffle();
	}
//endregion
}
