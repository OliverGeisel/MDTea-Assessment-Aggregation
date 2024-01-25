package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

public class AssignmentConfiguration extends TaskConfiguration {

	public AssignmentConfiguration() {}

	public AssignmentConfiguration(TestParameters testParameters) {
		super(TaskType.ASSIGNMENT, testParameters);
	}

}
