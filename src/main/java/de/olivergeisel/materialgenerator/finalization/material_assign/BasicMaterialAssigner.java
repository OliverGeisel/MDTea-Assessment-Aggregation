package de.olivergeisel.materialgenerator.finalization.material_assign;


import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import de.olivergeisel.materialgenerator.generation.material.transfer.OverviewMaterial;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	/**
	 * Create a new BasicMaterialAssigner with a set of materials and a selector.
	 * Use the {@link BasicCriteriaSelector} to select the materials
	 *
	 * @param materials the materials to assign
	 */
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
					if (material instanceof OverviewMaterial || material instanceof TestMaterial
						|| material instanceof ItemMaterial) {
						// todo only in this version hard coded. can be enabled by config
						continue;
					}
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
		}
		// assign complex materials
		var complexMaterials =
				getUnassignedMaterials().stream().filter(it -> it instanceof OverviewMaterial
															   || it instanceof TestMaterial);
		boolean result = false;
		for (var material : complexMaterials.toList()) {
			if (selector.satisfies(material, groupOrder) && (groupOrder.assign(material))) {
				setAssigned(material);
				result = true;
			}
		}

		// assign other materials to subparts if they different
		var firstTask = groupOrder.getTaskOrder().getFirst();
		if (groupOrder.getTaskOrder().stream().anyMatch(
				it -> it.getTopic() == null || !it.getTopic().equals(firstTask.getTopic()))) {
			logger.info("Tasks of {} has different topics", groupOrder);
			for (var task : groupOrder.getTaskOrder()) {
				if (assign(task)) {
					result = true;
				}
			}
		} else { // when all have the same topic -> todo  assign to the best matching task by name of task
			for (var task : groupOrder.getTaskOrder()) {
				if (assign(task)) {
					result = true;
				}
			}
			/* old version - maybe rethink and combine
			var materialsPerTask = new LinkedList<TaskOrderMaterial>();
			var otherMaterials = getUnassignedMaterials().stream().filter(it -> !(it instanceof OverviewMaterial
																				  || it instanceof TestMaterial));
			var materialsForTopic = otherMaterials.filter(it -> selector.satisfies(it, firstTask.getTopic()));
			groupOrder.getTaskOrder()
					  .forEach(it -> materialsPerTask.add(new TaskOrderMaterial(it, new LinkedList<>())));
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
			}
			// assign to the best (first) matching task // todo improve by selecting alternatives
			for (var entry : materialsPerTask) {
				var task = entry.taskOrder();
				for (var material : entry.material()) {
					if (assign(task)){
						result = true;
					}
				}
			}*/
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		var topic = part.getTopic();
		if (topic == null) {
			logger.warn("Topic of {} is null", part);
			return false;
		}
		return switch (part) {
			case ChapterOrder _ -> false;
			case GroupOrder group -> {
				// assign complex materials to the group
				if (!(material instanceof OverviewMaterial || material instanceof TestMaterial)) {
					yield false;
				}
				if (selector.satisfies(material, group)) {
					var res = group.assign(material);
					setAssigned(material);
					yield res;
				}
				yield false;
			}
			default -> {
				if (selector.satisfies(material, topic)) {
					var res = part.assign(material);
					setAssigned(material);
					yield res;
				}
				yield false;
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
			case GroupOrder groupOrder -> {
				var testsAndOverviews =
						getUnassignedMaterials().stream().filter(it -> it instanceof OverviewMaterial
																	   || it instanceof TestMaterial);
				var res = groupOrder.assignMaterial(testsAndOverviews.collect(Collectors.toSet()));
				yield !res.isEmpty();
			}
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

	/**
	 * Short holding of A {@link TaskOrder} and assigned {@link Material}s
	 *
	 * @param taskOrder
	 * @param material  materials assigned to the taskOrder
	 */
	private record TaskOrderMaterial(TaskOrder taskOrder, List<Material> material) {
	}
}
