package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import jakarta.persistence.Embeddable;

@Embeddable
public class SingleChoiceConfiguration extends ItemConfiguration {

	private int numberOfChoices = 4;

	private boolean shuffle = true;

	public SingleChoiceConfiguration(int numberOfChoices, boolean shuffle, TestParameters testParameters) {
		super(ItemType.SINGLE_CHOICE, testParameters);
		this.numberOfChoices = numberOfChoices;
	}

	@Override
	public SingleChoiceConfiguration clone() {
		return new SingleChoiceConfiguration(numberOfChoices, shuffle, getTestParameters().clone());
	}

//region setter/getter
	public int getNumberOfChoices() {
		return numberOfChoices;
	}

	public SingleChoiceConfiguration() {
	}

	public boolean isShuffle() {
		return shuffle;
	}
//endregion
}
