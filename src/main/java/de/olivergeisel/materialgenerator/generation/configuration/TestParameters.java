package de.olivergeisel.materialgenerator.generation.configuration;

import jakarta.persistence.Embeddable;

/**
 * Configuration for an Item, if it is used in a Test.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TestConfiguration
 * @since 1.1.0
 */
@Embeddable
public class TestParameters {

	public static final TestParameters DEFAULT = new TestParameters();
	private             int            tries;
	private             int            pointsPerUnit;
	private             double         negativeFactor;

	public TestParameters(int tries, int pointsPerUnit, double negativeFactor) {
		this.tries = tries;
		this.pointsPerUnit = pointsPerUnit;
		this.negativeFactor = negativeFactor;
	}

	public TestParameters() {
		this(1, 1, 0);
	}

	//region setter/getter
	public int getTries() {
		return tries;
	}

	public void setTries(int tries) {
		this.tries = tries;
	}

	public int getPointsPerUnit() {
		return pointsPerUnit;
	}

	public void setPointsPerUnit(int pointsPerUnit) {
		this.pointsPerUnit = pointsPerUnit;
	}

	public double getNegativeFactor() {
		return negativeFactor;
	}

	public void setNegativeFactor(double negativeFactor) {
		this.negativeFactor = negativeFactor;
	}
//endregion


}
