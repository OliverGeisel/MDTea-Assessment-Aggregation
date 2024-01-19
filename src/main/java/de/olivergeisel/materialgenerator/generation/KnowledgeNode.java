package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import lombok.Getter;

import java.util.*;

/**
 * Contains all Knowledge from a KnowledgeModel, that is related to a specific topic in the structure.
 * Each Node has a main KnowledgeElement. It should be a Term
 */
public class KnowledgeNode {

	private final   List<String>          topics = new ArrayList<>();
	@Getter private KnowledgeObject       structurePoint;
	@Getter private KnowledgeElement      mainElement;
	// todo remove duplicates in relatedElements
	@Getter private KnowledgeElement[]    relatedElements;
	@Getter private Relation[]            relations;
	@Getter private Optional<ContentGoal> goal   = Optional.empty();

	public KnowledgeNode(KnowledgeObject structurePoint, KnowledgeElement mainElement,
			KnowledgeElement[] relatedElements, Relation[] relations) {
		this.structurePoint = structurePoint;
		this.mainElement = mainElement;
		this.relatedElements = relatedElements;
		this.relations = relations;
	}

	public void addTopic(String topic) throws IllegalArgumentException {
		if (topic == null) {
			throw new IllegalArgumentException("topic must not be null");
		}
		this.topics.add(topic);
	}

	//region setter/getter
	public List<String> getTopics() {
		return Collections.unmodifiableList(topics);
	}

	public Optional<ContentGoalExpression> getExpression() {
		return goal.map(ContentGoal::getExpression);
	}

	public Optional<String> getMasterKeyWord() {
		return goal.map(ContentGoal::getMasterKeyword);
	}

	public void setGoal(ContentGoal goal) {
		if (goal == null) {
			throw new IllegalArgumentException("goal must not be null");
		}
		this.goal = Optional.of(goal);
	}

	public void setStructurePoint(KnowledgeObject structurePoint) {
		this.structurePoint = structurePoint;
	}

	public void setMainElement(KnowledgeElement mainElement) {
		this.mainElement = mainElement;
	}

	public void setRelatedElements(KnowledgeElement[] relatedElements) {
		this.relatedElements = relatedElements;
	}

	public void setRelations(Relation[] relations) {
		this.relations = relations;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeNode that)) return false;

		if (!Objects.equals(structurePoint, that.structurePoint)) return false;
		if (!Objects.equals(mainElement, that.mainElement)) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(relatedElements, that.relatedElements)) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(relations, that.relations);
	}

	@Override
	public int hashCode() {
		int result = structurePoint != null ? structurePoint.hashCode() : 0;
		result = 31 * result + (mainElement != null ? mainElement.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(relatedElements);
		result = 31 * result + Arrays.hashCode(relations);
		return result;
	}

	@Override
	public String toString() {
		return "KnowledgeNode{" +
			   "structurePoint=" + structurePoint +
			   ", mainElement=" + mainElement +
			   ", relatedElements size=" + relatedElements.length +
			   ", relations size=" + relations.length +
			   '}';
	}
}
