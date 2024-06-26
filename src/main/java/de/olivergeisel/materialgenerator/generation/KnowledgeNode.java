package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains all Knowledge from a {@link KnowledgeModel}, that is related to a specific topic in the structure. This
 * is the {@link KnowledgeObject} where the {@link KnowledgeElement}s are linked to.
 * Each Node has a main {@link KnowledgeElement}. It should be a {@link Term}.
 * Furthermore, it contains a list of related {@link KnowledgeElement}s and the {@link Relation}s between them. The
 * related {@link KnowledgeElement}s are the ones that have a Relation with one of to linked {@link KnowledgeElement}s.
 * <br>
 * Normally a KnowledgeNode is used to find {@link Material} for a {@link Course}. So it can contain a {@link ContentGoal}
 *
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeModel
 * @see KnowledgeElement
 * @see KnowledgeObject
 * @see ContentGoal
 * @since 0.2.0
 */
public class KnowledgeNode {

	private final   List<String>          topics = new ArrayList<>();
	// list of topics from a goal that is related to this node
	@Getter private KnowledgeObject       structurePoint;
	@Getter private KnowledgeElement      mainElement;
	@Getter private KnowledgeElement[]    linkedElements;
	// todo remove duplicates in relatedElements
	@Getter private KnowledgeElement[]    relatedElements;
	@Getter private Relation[]            relations;
	@Getter private Optional<ContentGoal> goal   = Optional.empty();

	public KnowledgeNode(KnowledgeObject structurePoint, KnowledgeElement mainElement,
			KnowledgeElement[] linkedElements, KnowledgeElement[] relatedElements, Relation[] relations) {
		this.structurePoint = structurePoint;
		this.mainElement = mainElement;
		this.linkedElements = linkedElements;
		this.relatedElements = relatedElements;
		this.relations = relations;
	}

	public void addTopics(Collection<String> topics) {
		if (topics == null) {
			throw new IllegalArgumentException("topics must not be null");
		}
		this.topics.addAll(topics);
	}

	public void addTopic(String topic) throws IllegalArgumentException {
		if (topic == null) {
			throw new IllegalArgumentException("topic must not be null");
		}
		this.topics.add(topic);
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType.
	 * It searches in the relatedElements of the KnowledgeNode.
	 *
	 * @param type RelationType to search for. Should be a Relation, that points to the main Element of the
	 *             KnowledgeNode
	 * @return Set of Relations that match the RelationType
	 */
	public Set<Relation> getWantedRelationsFromRelated(RelationType type) {
		return Arrays.stream(getRelatedElements())
					 .flatMap(it -> it.getRelations().stream().filter(relation -> relation.getType().equals(type)))
					 .collect(Collectors.toSet());
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType. Search only in the mainElement of the
	 * KnowledgeNode.
	 *
	 * @param type RelationType to search for. Should be a Relation, that goes from the main Element of the
	 *             KnowledgeNode. Like DefinedBy
	 * @return Set of Relations that match the RelationType
	 */
	public Set<Relation> getWantedRelationsFromMain(RelationType type) {
		return getMainElement().getRelations().stream()
							   .filter(relation -> relation.getType().equals(type)).collect(Collectors.toSet());
	}

	/**
	 * Get all Relations of this KnowledgeNode that match a RelationType.
	 * <p>
	 * Includes the Relations from the mainElement <b>and</b> the relatedElements.
	 *
	 * @param type RelationType to search for.
	 * @return Set of Relations that match the RelationType
	 */
	public Set<Relation> getWantedRelationsFrom(RelationType type) {
		var back = getWantedRelationsFromMain(type);
		back.addAll(getWantedRelationsFromRelated(type));
		return back;
	}

	//region setter/getter
	public List<String> getTopics() {
		return Collections.unmodifiableList(topics);
	}

	public Optional<ContentGoalExpression> getExpression() {
		return goal.map(ContentGoal::getExpression);
	}

	/**
	 * Returns the master keyword of the {@link ContentGoal} of this {@link KnowledgeNode}.
	 *
	 * @return the master keyword of the {@link ContentGoal} of this {@link KnowledgeNode} if present.
	 */
	public Optional<String> getMasterKeyWord() {
		return goal.map(ContentGoal::getMasterKeyword);
	}

	public void setGoal(ContentGoal goal) {
		if (goal == null) {
			throw new IllegalArgumentException("goal can't be set to null");
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
		return STR."KnowledgeNode{structurePoint=\{structurePoint},	mainElement=\{mainElement}, relatedElements size=\{relatedElements.length}, relations size=\{relations.length}";
	}
}
