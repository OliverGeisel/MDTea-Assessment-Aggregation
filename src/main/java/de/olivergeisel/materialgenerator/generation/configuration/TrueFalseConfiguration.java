package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;

public class TrueFalseConfiguration extends ItemConfiguration {
	public TrueFalseConfiguration(TestParameters testParameters) {super(ItemType.TRUE_FALSE, testParameters);}

	public TrueFalseConfiguration() {
		super(ItemType.TRUE_FALSE);
	}
}
