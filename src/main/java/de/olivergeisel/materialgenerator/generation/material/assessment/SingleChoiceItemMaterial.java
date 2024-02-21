package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.SingleChoiceConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class SingleChoiceItemMaterial extends ItemMaterial {

	private static final SingleChoiceConfiguration DEFAULT_CONFIGURATION = new SingleChoiceConfiguration();

	private String       question;
	private String       correctAnswer;
	@ElementCollection
	private List<String> alternativeAnswers;

	protected SingleChoiceItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.SINGLE_CHOICE);
	}

	public SingleChoiceItemMaterial(String question, String correctAnswer, List<String> alternativeAnswers,
			SingleChoiceConfiguration itemConfiguration) {
		super(itemConfiguration, TemplateType.SINGLE_CHOICE);
		this.question = question;
		this.correctAnswer = correctAnswer;
		this.alternativeAnswers = alternativeAnswers;
	}

	//region setter/getter
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
		if (itemConfiguration == null || !(itemConfiguration instanceof SingleChoiceConfiguration configuration)) {
			return false;
		}
		return configuration.isShuffle();
	}
//endregion
}
