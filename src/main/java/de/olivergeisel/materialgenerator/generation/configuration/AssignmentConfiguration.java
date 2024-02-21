package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import jakarta.persistence.Embeddable;

@Embeddable
public class AssignmentConfiguration extends ItemConfiguration {

	public AssignmentConfiguration() {}

	public AssignmentConfiguration(TestParameters testParameters) {
		super(ItemType.ASSIGNMENT, testParameters);
	}

	@Override
	public AssignmentConfiguration clone() {
		return new AssignmentConfiguration(getTestParameters().clone());
	}
}
