package de.olivergeisel.materialgenerator.core.courseplan.content;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A CurriculumGoal represents the educational goals of the {@link de.olivergeisel.materialgenerator.core.courseplan.CoursePlan} in MDTea.
 * <p>
 * The Goal has an expression that is a level of complexity based on Bloom-Taxonomie. <br>
 * Every Goal has a Master-Keyword which link the goal with the structure from the knowledgemodel.
 * Normally you define an educational goal as complete sentence.
 * In the complete Sentence are more than one keyword from the {@link KnowledgeModel}. So the relevant keywords are in the
 * specific Words.
 * <br>
 * Example: "Kennenlernen der Grundlagen der Programmierung mit Java"
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see ContentGoalExpression
 * @see ContentTarget
 * @see de.olivergeisel.materialgenerator.core.courseplan.CoursePlan
 * @see KnowledgeModel
 * @see KnowledgeElement
 * @since 0.2.0
 */
public class ContentGoal {

	private final ContentGoalExpression expression;
	private final String                masterKeyword;
	private final List<ContentTarget>   content = new ArrayList<>();
	private final String                name;
	private final String                completeSentence;
	//todo private final List<String> specificWords;

	protected ContentGoal() {
		this.expression = ContentGoalExpression.FIRST_LOOK;
		this.masterKeyword = "";
		this.completeSentence = "";
		this.name = "";
	}

	/**
	 * Create a new ContentGoal.
	 *
	 * @param expression       the expression of the goal.
	 * @param masterKeyword    the master keyword of the goal.
	 * @param content          the content of the goal.
	 * @param completeSentence the complete sentence of the goal.
	 */
	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content,
			String completeSentence) {
		this(expression, masterKeyword, content, completeSentence, "");
	}

	/**
	 * Create a new ContentGoal.
	 *
	 * @param expression       the expression of the goal.
	 * @param masterKeyword    the master keyword of the goal.
	 * @param content          the content of the goal. The content is a list of {@link ContentTarget}. Each target
	 *                         will be linked with the goal.
	 * @param completeSentence the complete sentence of the goal.
	 * @param name             the name of the goal.
	 */
	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content,
			String completeSentence, String name) {
		this.expression = expression;
		this.masterKeyword = masterKeyword;
		//todo this.specificWords = specificWords;
		this.completeSentence = completeSentence;
		this.content.addAll(content);
		content.forEach(c -> c.setRelatedGoal(this));
		this.name = name;
	}

	/**
	 * Add a {@link ContentTarget} to the content of the goal. The target will only be added if it is not already in the
	 * goal. Link the target with this goal.
	 *
	 * @param target the target to add.
	 * @return {@literal true} if the target was added. {@literal false} if the target is already in the content.
	 */
	public boolean add(ContentTarget target) {
		if (content.contains(target)) {
			return false;
		}
		target.setRelatedGoal(this);
		return content.add(target);
	}

	/**
	 * Remove a {@link ContentTarget} from the content of the goal.
	 * Remove the link between the target and this goal.
	 *
	 * @param target the target to remove.
	 * @return {@literal true} if the target was removed.
	 */
	public boolean remove(ContentTarget target) {
		if (content.remove(target)) {
			target.setRelatedGoal(null);
			return true;
		}
		return false;
	}

	//region setter/getter

	/**
	 * Get the complete sentence of the goal. The complete sentence is the sentence that describes the goal and has
	 * a {@link ContentGoalExpression} and a master keyword.
	 *
	 * @return the complete sentence of the goal.
	 */
	public String getCompleteSentence() {
		return completeSentence;
	}

	/**
	 * Get the content of the goal as unmodifiable list. The content is a list of {@link ContentTarget}.
	 *
	 * @return the content of the goal.
	 */
	public List<ContentTarget> getContent() {
		return Collections.unmodifiableList(content);
	}

	/**
	 * Get the expression of the goal. The expression is a level of complexity based on Bloom-Taxonomie.
	 *
	 * @return the expression of the goal.
	 */
	public ContentGoalExpression getExpression() {
		return expression;
	}

	/**
	 * Get the master keyword of the goal. The master keyword is the keyword that represents the main Topic of the goal.
	 * It is used to link the goal with the knowledge model.
	 *
	 * @return the master keyword of the goal.
	 */
	public String getMasterKeyword() {
		return masterKeyword;
	}

	/**
	 * Get the name of the goal.
	 *
	 * @return the name of the goal.
	 */
	public String getName() {
		return name;
	}
//endregion

	@Override
	public String toString() {
		return "ContentGoal{" +
			   "name='" + name + '\'' +
			   ", completeSentence='" + completeSentence + '\'' +
			   '}';
	}
}
