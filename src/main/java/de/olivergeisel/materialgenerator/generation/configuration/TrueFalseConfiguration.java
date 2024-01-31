package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

public class TrueFalseConfiguration extends TaskConfiguration {
	public TrueFalseConfiguration(TestParameters testParameters) {super(TaskType.TRUE_FALSE, testParameters);}

	public TrueFalseConfiguration() {
		super(TaskType.TRUE_FALSE);
	}
}
