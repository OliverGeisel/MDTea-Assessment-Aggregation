package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.content.TopicStructureAliasMappings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * A topic is a part of a {@link Goal}. It is the smallest unit that has to be mastered by the students.
 * <p>
 * Each topic is linked with at least one StructureElement of the Plan. In this case a chapter, group or task of
 * the {@link Course}. Or more general a {@link MaterialOrderCollection}
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Goal
 * @see MaterialOrderCollection
 * @see Course
 * @since 0.2.0
 */

@Getter
@Setter
@Entity
@Table(name = "topic")
public class Topic {

	private String                      name;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID                        id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Goal                        goal;
	@Embedded
	private TopicStructureAliasMappings topicStructureAliasMappings = new TopicStructureAliasMappings();

	/**
	 * Create a new Topic with the given {@link ContentTarget} and {@link Goal}.
	 *
	 * @param contentTarget The {@link ContentTarget} to use.
	 * @param goal          The {@link Goal} to link with.
	 * @throws IllegalArgumentException if contentTarget or goal is null or if the topic of the contentTarget is null or blank.
	 */
	public Topic(ContentTarget contentTarget, Goal goal) throws IllegalArgumentException {
		if (contentTarget == null) {
			throw new IllegalArgumentException("ContentTarget must not be null");
		}
		if (goal == null) {
			throw new IllegalArgumentException("Goal must not be null");
		}
		if (contentTarget.getTopic() == null || contentTarget.getTopic().isBlank()) {
			throw new IllegalArgumentException("Topic must not be null or blank");
		}
		name = contentTarget.getTopic();
		topicStructureAliasMappings = contentTarget.getAliases();
		this.goal = goal;
	}

	protected Topic() {

	}

	public static Topic empty() {
		var back = new Topic();
		back.name = "EMPTY TOPIC";
		return back;
	}

	/**
	 * Update the goal this topic is linked to.
	 *
	 * @param goal The new goal.
	 * @throws IllegalArgumentException if goal is null or has no ID.
	 */
	public void updateGoal(Goal goal) {
		if (goal == null) {
			throw new IllegalArgumentException("Goal must not be null");
		}
		if (goal.getId() == null) {
			throw new IllegalArgumentException("Goal must have an ID");
		}
		this.goal = goal;
	}

	/**
	 * Check if this topic is the same as the given {@link ContentTarget}.
	 *
	 * @param contentTarget The {@link ContentTarget} to compare with.
	 * @return true if the topic is the same, false otherwise.
	 */
	public boolean isSame(ContentTarget contentTarget) {
		return name.equals(contentTarget.getTopic());
	}

	//region setter/getter
	public TopicStructureAliasMappings getTopicStructureAliasMappings() {
		return topicStructureAliasMappings;
	}

	public void setTopicStructureAliasMappings(
			TopicStructureAliasMappings topicStructureAliasMappings) {
		this.topicStructureAliasMappings = topicStructureAliasMappings;
	}

	public Goal getGoal() {
		return goal;
	}

	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Topic topic)) return false;

		if (id == null || topic.id == null) {
			if (!Objects.equals(name, topic.name)) return false;
			if (!Objects.equals(goal, topic.goal)) return false;
			return topicStructureAliasMappings.equals(topic.topicStructureAliasMappings);
		}
		return Objects.equals(id, topic.id);
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (goal != null ? goal.hashCode() : 0);
		result = 31 * result + topicStructureAliasMappings.hashCode();
		return result;
	}
}