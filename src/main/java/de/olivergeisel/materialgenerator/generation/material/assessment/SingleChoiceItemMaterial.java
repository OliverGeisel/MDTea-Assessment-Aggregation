package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.ItemConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class SingleChoiceItemMaterial extends ItemMaterial {

	private String       question;
	private String       correctAnswer;
	@ElementCollection
	private List<String> alternativeAnswers;
	private boolean      shuffle;

	protected SingleChoiceItemMaterial() {
		super();
	}

	public SingleChoiceItemMaterial(String question, String correctAnswer, List<String> alternativeAnswers,
			ItemConfiguration itemConfiguration, boolean shuffle) {
		super(itemConfiguration, TemplateType.SINGLE_CHOICE);
		this.question = question;
		this.correctAnswer = correctAnswer;
		this.alternativeAnswers = alternativeAnswers;
		this.shuffle = shuffle;
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
		return shuffle;
	}
//endregion
}
