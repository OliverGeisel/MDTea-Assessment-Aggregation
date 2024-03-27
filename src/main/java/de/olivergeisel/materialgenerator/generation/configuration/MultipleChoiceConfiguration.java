package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import jakarta.persistence.Embeddable;

@Embeddable
public class MultipleChoiceConfiguration extends ItemConfiguration {

	private int     numberOfCorrectChoices;
	private int     numberOfChoices;
	private boolean shuffle;

	public MultipleChoiceConfiguration() {
		this.numberOfChoices = 4;
		this.numberOfCorrectChoices = 2;
		shuffle = false;
	}

	public MultipleChoiceConfiguration(int numberOfChoices, int numberOfCorrectChoices, boolean shuffle,
			TestParameters testParameters) {
		super(ItemType.MULTIPLE_CHOICE, testParameters);
		this.numberOfChoices = numberOfChoices;
		this.numberOfCorrectChoices = numberOfCorrectChoices;
		this.shuffle = shuffle;
	}

	@Override
	public MultipleChoiceConfiguration clone() {
		return new MultipleChoiceConfiguration(numberOfChoices, numberOfCorrectChoices, shuffle,
				getTestParameters().clone());
	}

	//region setter/getter
	public boolean isShuffle() {
		return shuffle;
	}

	public int getNumberOfCorrectChoices() {
		return numberOfCorrectChoices;
	}

	public int getNumberOfChoices() {
		return numberOfChoices;
	}
//endregion

}
