package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

public class SingleChoiceConfiguration extends TaskConfiguration {

	private int numberOfChoices = 4;

	public SingleChoiceConfiguration(int numberOfChoices, TestParameters testParameters) {
		super(TaskType.SINGLE_CHOICE, testParameters);
		this.numberOfChoices = numberOfChoices;
	}

	public SingleChoiceConfiguration() {
	}
}
