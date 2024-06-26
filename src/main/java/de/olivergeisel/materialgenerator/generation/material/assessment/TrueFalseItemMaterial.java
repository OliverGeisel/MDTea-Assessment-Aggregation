package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.TrueFalseConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class TrueFalseItemMaterial extends ItemMaterial {

	private static final TrueFalseConfiguration DEFAULT_CONFIGURATION = new TrueFalseConfiguration();


	private String  question;
	private boolean correct;
	@Embedded
	private TrueFalseConfiguration itemConfiguration;

	protected TrueFalseItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.TRUE_FALSE);
		this.question = "";
		this.correct = true;
		itemConfiguration = DEFAULT_CONFIGURATION;
	}

	public TrueFalseItemMaterial(String question, boolean correctAnswer) {
		this(question, correctAnswer, DEFAULT_CONFIGURATION);
	}

	public TrueFalseItemMaterial(String question, boolean correctAnswer, TrueFalseConfiguration itemConfiguration) {
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
	@Override
	public TrueFalseConfiguration getItemConfiguration() {
		return itemConfiguration;
	}
	public boolean isCorrect() {
		return correct;
	}

	public String getQuestion() {
		return question;
	}
//endregion

}
