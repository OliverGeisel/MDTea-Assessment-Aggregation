package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PriorityMaterialAssigner extends MaterialAssigner {
	public PriorityMaterialAssigner(Set<Material> materials) {
		super(materials);
	}

	public PriorityMaterialAssigner(Set<Material> materials, CriteriaSelector selector) {
		super(materials, selector);
	}

	/**
	 * Assign materials to a MaterialOrderCollection. It compares the separate parts and assign the material to the
	 * part wehre the first alias matches to the material.
	 *
	 * @param part the part that get the materials.
	 * @return {@literal true} if at least one material is assigned, otherwise {@literal false}.
	 * @throws IllegalArgumentException if part is {@literal null}.
	 */
	@Override
	public boolean assign(MaterialOrderCollection part) throws IllegalArgumentException {
		if (part == null) {
			throw new IllegalArgumentException("part must not be null");
		}
		var unassignedMaterials = getUnassignedMaterials();
		if (unassignedMaterials.isEmpty()) {
			return false;
		}
		var back = false;
		if (part instanceof TaskOrder task) {
			var materialsStructures = materialMap.keySet().stream().map(Material::getStructureId)
												 .collect(Collectors.toSet());
			var matchingAlias = task.getMatchingAlias(materialsStructures);
			if (matchingAlias.isEmpty()) {
				return false;
			}
			for (var material : unassignedMaterials) {
				if (selector.satisfies(material, part) && (part.assign(material))) {
					setAssigned(material);
					back = true;
				}
			}
		} else {
			handeleComplexPart(part, unassignedMaterials);
		}
		return back;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException if part is {@literal null}.
	 */
	@Override
	public boolean assignWithoutCriteria(MaterialOrderCollection part) throws IllegalArgumentException {
		if (part == null) {
			throw new IllegalArgumentException("part must not be null");
		}
		var unassignedMaterials = getUnassignedMaterials();
		if (unassignedMaterials.isEmpty()) {
			return false;
		}
		var back = false;
		if (part instanceof TaskOrder task) {
			var materialsStructures = materialMap.keySet().stream().map(Material::getStructureId)
												 .collect(Collectors.toSet());
			var matchingAlias = task.getMatchingAlias(materialsStructures);
			if (matchingAlias.isEmpty()) {
				return false;
			}
			for (var material : unassignedMaterials) {
				if (matchingAlias.contains(material.getStructureId())) {
					task.assign(material);
					setAssigned(material);
					back = true;
				}
			}
		} else {
			handeleComplexPart(part, unassignedMaterials);
		}
		return back;
	}

	private void handeleComplexPart(MaterialOrderCollection part, Set<Material> unassignedMaterials) {
		switch (part) {
			case ChapterOrder chapter -> chapterHandling(unassignedMaterials, chapter);
			case GroupOrder group -> groupHandling(unassignedMaterials, group);
			default -> throw new IllegalArgumentException("part must be a ChapterOrder or GroupOrder");
		}
	}

	private void chapterHandling(Set<Material> unassignedMaterials, ChapterOrder chapter) {
		for (var group : chapter.getGroupOrder()) {
			groupHandling(unassignedMaterials, group);
		}
	}

	private void groupHandling(Set<Material> unassignedMaterials, GroupOrder group) {
		var possibleAssignMap = new HashMap<Material, List<PossibleAssign>>();
		// find all possible assigns
		for (var material : unassignedMaterials) {
			var structureIdSet = Set.of(material.getStructureId());
			for (var task : group.getTaskOrder()) {
				var position = task.getFirstMatchingAlias(structureIdSet);
				if (position.position() >= 0) {
					possibleAssignMap.compute(material, (k, v) -> {
						if (v == null) {
							v = List.of(new PossibleAssign(task, position.position(), position.alias()));
						} else {
							v.add(new PossibleAssign(task, position.position(), position.alias()));
						}
						return v;
					});
				}
			}
		}
		// assign the material to the task with the lowest position
		for (var entry : possibleAssignMap.entrySet()) {
			var material = entry.getKey();
			var possibleAssigns = entry.getValue();
			var minimalPosition = possibleAssigns.stream().mapToInt(PossibleAssign::getAliasPosition).min().orElse(-1);
			if (minimalPosition >= 0) {
				possibleAssigns.stream().filter(p -> p.getAliasPosition() == minimalPosition)
							   .findFirst()
							   .ifPresent(it -> {
								   it.getTaskOrder().assign(material);
								   setAssigned(material);
							   });
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assignWithoutCriteria(Material material, MaterialOrderCollection part) {
		return false;
	}

	private class PossibleAssign {
		private final TaskOrder taskOrder;
		private       int       aliasPosition;
		private       String    alias;

		private PossibleAssign(TaskOrder taskOrder, int aliasPosition, String alias) {
			if (taskOrder == null) throw new IllegalArgumentException("taskOrder must not be null");
			if (aliasPosition < 0) throw new IllegalArgumentException("aliasPosition must be greater than 0");
			if (alias == null || alias.isBlank()) throw new IllegalArgumentException("alias must not be null or blank");
			this.taskOrder = taskOrder;
			this.aliasPosition = aliasPosition;
			this.alias = alias;
		}

		//region setter/getter
		public TaskOrder getTaskOrder() {
			return taskOrder;
		}

		public int getAliasPosition() {
			return aliasPosition;
		}

		public String getAlias() {
			return alias;
		}
//endregion
	}

}
