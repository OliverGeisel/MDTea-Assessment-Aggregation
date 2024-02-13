package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A Goal is a learning goal that should be achieved by the students in a course.
 * They are a persistent representation of the {@link ContentGoal} class.
 * <p>
 * A Goal contains a {@link ContentGoalExpression}, a master keyword, a complete sentence and a list of {@link Topic}s.
 * Each Topic is a subpart that should be learned by the students.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see ContentGoal
 * @see ContentGoalExpression
 * @see Topic
 * @since 0.2.0
 */
@Entity
public class Goal {
	private final ContentGoalExpression expression;
	private final String                masterKeyword;
	@OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Topic>           topics = new LinkedList<>();
	@Column(length = 2_000)
	private final String                completeSentence;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private       UUID                  id;
	private       String                name;
	//private final List<String> specificWords;


	public Goal() {
		this.expression = ContentGoalExpression.FIRST_LOOK;
		this.masterKeyword = "";
		this.completeSentence = "";
		this.name = "";
	}


	public Goal(ContentGoal contentGoal) {
		this.expression = contentGoal.getExpression();
		this.masterKeyword = contentGoal.getMasterKeyword();
		//this.specificWords = specificWords;
		this.completeSentence = contentGoal.getCompleteSentence();
		this.name = contentGoal.getName();
		for (var contentTarget : contentGoal.getContent()) {
			try {
				topics.add(new Topic(contentTarget, this));
			} catch (IllegalArgumentException ignored) {
			}
		}
	}

	boolean isSame(ContentGoal contentGoal) {
		return expression == contentGoal.getExpression() && masterKeyword.equals(contentGoal.getMasterKeyword())
			   && completeSentence.equals(contentGoal.getCompleteSentence())
			   && topics.size() == contentGoal.getContent().size();
	}

	//region setter/getter
	public List<Topic> getTopics() {
		return topics;
	}

	public UUID getId() {
		return id;
	}

	public ContentGoalExpression getExpression() {
		return expression;
	}

	public String getMasterKeyword() {
		return masterKeyword;
	}

	public String getCompleteSentence() {
		return completeSentence;
	}

	public String getName() {
		return name;
	}
//endregion

	@Override
	public String toString() {
		return "Goal{" +
			   "id=" + id + '\'' +
			   ", completeSentence='" + completeSentence +
			   '}';
	}
}
