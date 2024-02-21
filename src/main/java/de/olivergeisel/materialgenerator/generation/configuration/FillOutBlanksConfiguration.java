package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import jakarta.persistence.Embeddable;

@Embeddable
public class FillOutBlanksConfiguration extends ItemConfiguration {

	private int               numberOfBlanks;
	private FillOutBlanksType blankType;


	public FillOutBlanksConfiguration() {
	}

	public FillOutBlanksConfiguration(int numberOfBlanks, FillOutBlanksType blankType) {
		this.numberOfBlanks = numberOfBlanks;
		this.blankType = blankType;
	}

	public FillOutBlanksConfiguration(int numberOfBlanks, FillOutBlanksType blankType, TestParameters testParameters) {
		super(ItemType.FILL_OUT_BLANKS, testParameters);
		this.numberOfBlanks = numberOfBlanks;
		this.blankType = blankType;
	}


	public enum FillOutBlanksType {
		RANDOM,
		TERM,
	}
}
