package de.olivergeisel.materialgenerator.core.courseplan.content;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A Part of a ContentGoal. Target are small peaces, which fill a small part the {@link ContentGoal}.
 * <p>
 * Example: <br>Goal: "Learn the basics of Java"
 * <br>Targets: "if-Statement", "for-Loop", "while-Loop", "switch-Statement"
 * <p>
 * The targets are the small peaces, which are needed to reach the goal.
 * The goal is the big picture, the targets are the small peaces, which are needed to reach the goal.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ContentGoal
 * @see ContentGoalExpression
 * @since 0.2.0
 */
public class ContentTarget {

	/**
	 * An empty ContentTarget.
	 */
	public static final ContentTarget EMPTY = new ContentTarget("NO_TARGET");

	private String      mainTopicName;
	private ContentGoal relatedGoal;
	private TopicStructureAliasMappings topicStructureAliasMappings = new TopicStructureAliasMappings();

	protected ContentTarget() {
	}

	/**
	 * Create a new ContentTarget.
	 *
	 * @param mainTopicName the value of the target.
	 */
	public ContentTarget(String mainTopicName) {
		this.mainTopicName = mainTopicName;
	}

	public ContentTarget(String mainTopicName, ContentGoal relatedGoal) {
		this.mainTopicName = mainTopicName;
		this.relatedGoal = relatedGoal;
	}

	public ContentTarget(String mainTopicName, ContentGoal relatedGoal, TopicStructureAliasMappings aliases) {
		this.mainTopicName = mainTopicName;
		this.relatedGoal = relatedGoal;
		this.topicStructureAliasMappings = aliases;
	}

	//region setter/getter
	public TopicStructureAliasMappings getAliases() {
		return topicStructureAliasMappings;
	}

	public List<String> getAllAliases() {
		var back = new LinkedList<String>();
		back.add(mainTopicName);
		back.addAll(topicStructureAliasMappings.complete());
		return back;
	}

	public String getMainTopicName() {
		return mainTopicName;
	}

	public void setMainTopicName(String mainTopicName) {
		this.mainTopicName = mainTopicName;
	}

	public ContentGoal getRelatedGoal() {
		return relatedGoal;
	}

	public void setRelatedGoal(ContentGoal relatedGoal) {
		this.relatedGoal = relatedGoal;
	}

	/**
	 * Get the most important alias of the target.
	 * This method is identical to {@link #getMainTopicName()}.
	 *
	 * @return the most important alias of the target.
	 */
	public String getTopic() {
		return mainTopicName;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContentTarget that)) return false;

		if (!Objects.equals(mainTopicName, that.mainTopicName)) return false;
		return Objects.equals(relatedGoal, that.relatedGoal);
	}

	@Override
	public int hashCode() {
		int result = mainTopicName != null ? mainTopicName.hashCode() : 0;
		result = 31 * result + (relatedGoal != null ? relatedGoal.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return STR."ContentTarget{value='\{mainTopicName}\{'\''}, relatedGoal=\{relatedGoal}}";
	}
}
