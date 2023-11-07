package de.olivergeisel.materialgenerator.core.courseplan.content;

/**
 * Expression of a learning {@link ContentGoal}. A single word, that can be mapped to a Bloom's taxonomy level.
 *
 * @see ContentGoal
 * @see ContentTarget
 */
public enum ContentGoalExpression {

	/**
	 * Simplest form of learning goal. The student get an overview to a topic.
	 */
	FIRST_LOOK, // Bloom KNOWLEDGE
	/**
	 * The student should be able to know a topic.
	 */
	KNOW, // Bloom KNOWLEDGE
	/**
	 * The student should be able to understand a topic.
	 */
	TRANSLATE, // Bloom COMPREHENSION
	/**
	 * The student should be able to apply a topic.
	 */
	CONTROL, // Bloom APPLICATION
	/**
	 * The student should be able to apply a topic.
	 */
	USE, // Bloom APPLICATION
	/**
	 * The student should be able to analyze a topic.
	 */
	COMMENT, // Bloom ANALYSIS
	/**
	 * The student should be able to evaluate a topic.
	 */
	CREATE // Bloom SYNTHESIS / EVALUATION
}
