package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;

public class SingleChoiceConfiguration extends ItemConfiguration {

	private int numberOfChoices = 4;

	public SingleChoiceConfiguration(int numberOfChoices, TestParameters testParameters) {
		super(ItemType.SINGLE_CHOICE, testParameters);
		this.numberOfChoices = numberOfChoices;
	}

	public SingleChoiceConfiguration() {
	}
}
