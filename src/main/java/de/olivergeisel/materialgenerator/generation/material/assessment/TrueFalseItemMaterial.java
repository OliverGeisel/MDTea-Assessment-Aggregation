package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.ItemConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TrueFalseConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

@Entity
public class TrueFalseItemMaterial extends ItemMaterial {

	private static final ItemConfiguration DEFAULT_CONFIGURATION = new TrueFalseConfiguration();


	private String  question;
	private boolean correct;

	protected TrueFalseItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.TRUE_FALSE);
		this.question = "";
		this.correct = true;
	}

	public TrueFalseItemMaterial(String question, boolean correctAnswer) {
		this(question, correctAnswer, DEFAULT_CONFIGURATION);
	}

	public TrueFalseItemMaterial(String question, boolean correctAnswer, ItemConfiguration itemConfiguration) {
		super(itemConfiguration, TemplateType.TRUE_FALSE);
		this.question = question;
		this.correct = correctAnswer;
	}

	@Override
	public boolean isIdentical(Material material) {
		if (!(material instanceof TrueFalseItemMaterial item)) {
			return false;
		}
		if (!super.isIdentical(material)) {
			return false;
		}
		return this.correct == item.correct && this.question.equals(item.question);
	}

	//region setter/getter
	public boolean isCorrect() {
		return correct;
	}

	public String getQuestion() {
		return question;
	}
//endregion

}
