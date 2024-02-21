package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Set;

/**
 * Assign materials to a MaterialOrderCollection. This assigner is strict. It only assigns materials to a part if one
 * of the first n alias match the requirements. n is set by {@literal maxAlias}. If no alias is set, the part is not
 * assigned.
 * <p>
 * This assigner is useful if you want to assign materials to a part that has a lot of alias and you want to
 * assign the material to the part that matches the requirements best.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see MaterialAssigner
 * @see CriteriaSelector
 * @see MaterialOrderCollection
 * @see Material
 * @since 0.2.0
 */
public class StrictMaterialAssigner extends MaterialAssigner {

	private int maxAlias;

	public StrictMaterialAssigner(Set<Material> materials) {
		this(materials, 1);
	}

	public StrictMaterialAssigner(Set<Material> materials, CriteriaSelector selector) {
		this(materials, 1, selector);
	}

	public StrictMaterialAssigner(Set<Material> materials, int maxAlias) {
		super(materials);
		this.maxAlias = maxAlias;
	}

	public StrictMaterialAssigner(Set<Material> materials, int maxAlias, CriteriaSelector selector) {
		super(materials, selector);
		this.maxAlias = maxAlias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(MaterialOrderCollection part) {
		// todo implement
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		// todo implement
		return false;
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
		if (part instanceof ChapterOrder || part instanceof GroupOrder)
			return false;
		if (part.getAlias().isEmpty())
			return false;
		var aliase = part.getAlias().stream().limit(maxAlias);
		var back = false;
		for (var alias : aliase.toList()) {
			for (var material : getUnassignedMaterials()) {
				if (material.getStructureId().contains(alias)) {
					part.assign(material);
					setAssigned(material);
					back = true;
				}
			}
		}
		return back;
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
		if (isAssigned(material))
			return false;
		if (part instanceof ChapterOrder chapter) {
			for (var group : chapter.getGroupOrder()) {
				if (assign(material, group)) {
					return true;
				}
			}
		} else if (part instanceof GroupOrder group) {
			for (var task : group.getTaskOrder()) {
				if (assign(material, task)) {
					return true;
				}
			}
		} else {
			if (part.getAlias().isEmpty())
				return false;
			var aliase = part.getAlias().stream().limit(maxAlias);
			for (var alias : aliase.toList()) {
				if (material.getStructureId().contains(alias)) {
					part.assign(material);
					setAssigned(material);
					return true;
				}
			}
		}
		return false;
	}

	//region setter/getter
	public int getMaxAlias() {
		return maxAlias;
	}

	public void setMaxAlias(int maxAlias) {
		if (maxAlias < 1)
			throw new IllegalArgumentException("maxAlias must be greater than 0!");
		this.maxAlias = maxAlias;
	}
//endregion

}
