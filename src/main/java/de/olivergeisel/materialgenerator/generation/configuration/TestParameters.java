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

	@Override
	public TestParameters clone() {
		return new TestParameters(tries, pointsPerUnit, negativeFactor);
	}
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
	//region setter/getter
//endregion

	@Override
	public String toString() {
		return STR."TestParameters{tries=\{tries}, pointsPerUnit=\{pointsPerUnit}, negativeFactor=\{negativeFactor}}";
	}

}
