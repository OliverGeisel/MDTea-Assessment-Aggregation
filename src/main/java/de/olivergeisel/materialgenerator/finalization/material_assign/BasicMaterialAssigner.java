package de.olivergeisel.materialgenerator.finalization.material_assign;


import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Assign material to a MaterialOrderCollection. This is the basic implementation of the {@link MaterialAssigner}.
 * All this Assigner does is to call the assignMaterial method of the {@link MaterialOrderCollection}.
 * So the MaterialOrderCollection itself decides how to assign the material. Only exception is when the material was
 * already assigned. In this case the material is not assigned to a new part.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see MaterialOrderCollection#assignMaterial(Set)
 * @since 0.2.0
 */
public class BasicMaterialAssigner extends MaterialAssigner {

	public BasicMaterialAssigner(Set<Material> materials) {
		super(materials);
	}

	public BasicMaterialAssigner(Set<Material> materials, CriteriaSelector selector) {
		super(materials, selector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(MaterialOrderCollection part) {
		return switch (part) {
			case ChapterOrder ignored -> false;
			case GroupOrder groupOrder -> groupAssign(groupOrder);
			default -> {
				boolean result = false;
				var topic = part.getTopic();
				if (topic == null) {
					logger.warn("Topic of {} is null", part);
					yield false;
				}
				for (var material : getUnassignedMaterials()) {
					if (selector.satisfies(material, part) && (part.assign(material))) {
						setAssigned(material);
						result = true;
					}
				}
				yield result;
			}
		};
	}

	private boolean groupAssign(GroupOrder groupOrder) {
		if (groupOrder.getTaskOrder().isEmpty()) {
			logger.warn("TaskOrder of {} is empty", groupOrder);
			return false;
		}

		var firstTask = groupOrder.getTaskOrder().get(0);
		boolean result = false;
		if (groupOrder.getTaskOrder().stream().anyMatch(it -> it.getTopic() == null
															  || !it.getTopic().equals(firstTask.getTopic()))) {
			logger.info("Tasks of {} has different topics", groupOrder);
			for (var task : groupOrder.getTaskOrder()) {
				if (assign(task)) {
					result = true;
				}
			}
			return result;
		}

		var materialsPerTask = new LinkedList<TaskOrderMaterial>();
		var materialsForTopic = getUnassignedMaterials().stream().filter(it -> selector.satisfies(it,
				firstTask.getTopic()));
		groupOrder.getTaskOrder().forEach(it -> materialsPerTask.add(new TaskOrderMaterial(it, new LinkedList<>())));
		for (var material : materialsForTopic.toList()) {
			var matching = false;
			int i = 0;
			for (var task : groupOrder.getTaskOrder()) {
				matching = selector.satisfies(material, task.getName());
				if (matching) {
					materialsPerTask.get(i).material().add(material);
					break;
				}
				++i;
			}
			if (!matching) {
				materialsPerTask.get(0).material.add(material);
			}
		}
		for (var entry : materialsPerTask) {
			var task = entry.taskOrder();
			for (var material : entry.material()) {
				task.assign(material);
				setAssigned(material);
				result = true;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		return switch (part) {
			case ChapterOrder ignored1 -> false;
			case GroupOrder ignored -> false;
			default -> {
				var topic = part.getTopic();
				if (topic == null) {
					logger.warn("Topic of {} is null", part);
					yield false;
				}
				if (selector.satisfies(material, topic)) {
					var res = part.assign(material);
					setAssigned(material);
					yield res;
				} else {
					yield false;
				}
			}
		};
	}

	/**
	 * Try to assign all materials to a {@link MaterialOrderCollection}. Does not use the {@link CriteriaSelector} to
	 * the match. The Collection decide which material it will take.
	 *
	 * @param part the part to assign the materials to
	 * @return {@literal true} if at least one material was assigned, otherwise false.
	 */
	@Override
	public boolean assignWithoutCriteria(MaterialOrderCollection part) {
		return switch (part) {
			case ChapterOrder chapterOrder -> false;
			case GroupOrder groupOrder -> false;
			default -> {
				var res = part.assignMaterial(getUnassignedMaterials());
				setAssigned(res);
				yield !res.isEmpty();
			}
		};
	}

	/**
	 * Try to assign a specific material to a {@link MaterialOrderCollection}. Does not use the
	 * {@link CriteriaSelector}. The Collection decide which material it will take.
	 *
	 * @param material the material to assign. Must be in the material set of this assigner.
	 * @param part     the part to assign the material to
	 * @return {@literal true} if the material was assigned, otherwise {@literal false}.
	 */
	@Override
	public boolean assignWithoutCriteria(Material material, MaterialOrderCollection part) {
		if (!materialMap.containsKey(material)) {
			throw new IllegalArgumentException("Material not found in Assigner!");
		}
		var back = part.assign(material);
		materialMap.get(material).setAssigned(true);
		return back;
	}

	private record TaskOrderMaterial(TaskOrder taskOrder, List<Material> material) {
	}
}
