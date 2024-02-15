package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;

public class AssignmentConfiguration extends ItemConfiguration {

	public AssignmentConfiguration() {}

	public AssignmentConfiguration(TestParameters testParameters) {
		super(ItemType.ASSIGNMENT, testParameters);
	}

}
