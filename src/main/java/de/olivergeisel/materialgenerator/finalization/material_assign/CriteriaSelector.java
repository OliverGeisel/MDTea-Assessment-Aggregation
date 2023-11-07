package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.Topic;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;


/**
 * Interface for a criteria selector. A criteria selector is used to select a material by a {@link MaterialAssigner}
 * based on criteria.
 * <p>
 * The criteria can be anything, but it should be a string. The criteria selector can be used to select a
 * material based on a topic, a goal or a target.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see ContentTarget
 * @see ContentGoal
 * @see Material
 * @see MaterialAssigner
 * @since 0.2.0
 */
public interface CriteriaSelector {

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
	boolean satisfies(Material material, String criteria);

	/**
	 * Checks if the material satisfies the goal.
	 *
	 * @param material material to check
	 * @param goal     goal to check
	 * @return {@literal true} if material satisfies the goal, otherwise {@literal false}
	 * @throws IllegalArgumentException if goal is null
	 */
	boolean satisfies(Material material, Goal goal) throws IllegalArgumentException;

	/**
	 * Checks if the material satisfies the target.
	 *
	 * @param material material to check
	 * @param target   target to check (must be related to a goal)
	 * @return {@literal true} if material satisfies the target, otherwise {@literal false}
	 * @throws IllegalArgumentException if target is null
	 */
	default boolean satisfies(Material material, Topic target) throws IllegalArgumentException {
		if (target == null) throw new IllegalArgumentException("target must not be null");
		return satisfies(material, target.getGoal());
	}

	/**
	 * Checks if the material satisfies the target of the Collection or the alternatives.
	 *
	 * @param material   material to check
	 * @param collection collection to check
	 * @return {@literal true} if material satisfies the target, otherwise {@literal false}
	 * @throws IllegalArgumentException if target is null
	 */
	boolean satisfies(Material material, MaterialOrderCollection collection) throws IllegalArgumentException;

}
