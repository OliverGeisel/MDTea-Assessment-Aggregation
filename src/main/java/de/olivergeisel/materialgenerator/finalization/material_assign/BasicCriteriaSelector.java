package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.Topic;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.LinkedList;

/**
 * Basic implementation of a criteria selector. It selects a material based on the alias of the material.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see CriteriaSelector
 * @see MaterialAssigner
 * @see Material
 * @see ContentGoal
 * @see de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget
 * @since 0.2.0
 */
public class BasicCriteriaSelector implements CriteriaSelector {
	/**
	 * Compares the given material with the criteria.
	 * <p>
	 * It only compares if the material alias satisfies the criteria. extra behavior can be defined in the
	 * implementation.
	 *
	 * @param material material to check
	 * @param criteria criteria to check
	 * @return {@literal true} if material satisfies the criteria, otherwise {@literal false}
	 */
	@Override
	public boolean satisfies(Material material, String criteria) {
		if (criteria == null) return false;
		var structure = material.getStructureId();
		if (structure == null)
			return false;
		if (criteria.equals(structure)) {
			return true;
		}
		if (criteria.contains(structure)) {
			return true;
		}
		var subNames = criteria.split(" ");
		for (var subName : subNames) {
			if (subName.equals(structure)) {
				return true;
			}
		}
		// check if alias satisfies criteria

		var term = material.getTerm();
		if (term == null) return false;
		if (criteria.equals(term)) {
			return true;
		}
		if (criteria.contains(term)) {
			return true;
		}
		var materialName = material.getName();
		if (materialName == null) return false;
		if (criteria.equals(materialName)) {
			return true;
		}
		return criteria.contains(materialName);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param material material to check
	 * @param goal     goal to check
	 * @return {@literal true} if material satisfies the goal, otherwise {@literal false}
	 * @throws IllegalArgumentException if goal is null
	 */
	@Override
	public boolean satisfies(Material material, Goal goal) throws IllegalArgumentException {
		for (var topic : goal.getTopics()) {
			if (satisfies(material, topic)) return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param material material to check
	 * @param target   target to check
	 * @return {@literal true} if material satisfies the target, otherwise {@literal false}
	 * @throws IllegalArgumentException if target is null
	 */
	@Override
	public boolean satisfies(Material material, Topic target) throws IllegalArgumentException {
		if (target == null) throw new IllegalArgumentException("target must not be null");
		var topicName = target.getName();
		return satisfies(material, topicName);
	}

	/**
	 * @param material   material to check
	 * @param collection collection to check
	 * @return {@literal true} if material satisfies the collection, otherwise {@literal false}
	 * @throws IllegalArgumentException if collection is null
	 */
	@Override
	public boolean satisfies(Material material, MaterialOrderCollection collection) throws IllegalArgumentException {
		if (collection == null) throw new IllegalArgumentException("target must not be null");
		var toCheck = new LinkedList<>(collection.getAlias());
		toCheck.add(collection.getTopic().getName());
		for (var name : toCheck) {
			if (satisfies(material, name)) return true;
		}
		return false;
	}

}
