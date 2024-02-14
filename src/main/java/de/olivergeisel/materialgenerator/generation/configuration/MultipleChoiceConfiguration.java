package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

public class MultipleChoiceConfiguration extends TaskConfiguration {
	private final int numberOfCorrectChoices;
	private final int numberOfChoices;

	public MultipleChoiceConfiguration() {
		this.numberOfChoices = 4;
		this.numberOfCorrectChoices = 2;
	}

	public MultipleChoiceConfiguration(int numberOfChoices, int numberOfCorrectChoices,
			TestParameters testParameters) {
		super(TaskType.MULTIPLE_CHOICE, testParameters);
		this.numberOfChoices = numberOfChoices;
		this.numberOfCorrectChoices = numberOfCorrectChoices;
	}


}
