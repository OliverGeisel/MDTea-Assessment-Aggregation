package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Set;

public abstract class MaterialAssignerDecorator extends MaterialAssigner {

	private MaterialAssigner basicAssigner;

	protected MaterialAssignerDecorator(Set<Material> materials) {
		super(materials);
	}

	protected MaterialAssignerDecorator(Set<Material> materials, CriteriaSelector selector) {
		super(materials, selector);
	}

	/**
	 * @param material the material to assign
	 * @param part     the part to assign the material to
	 * @return true if the material was assigned, false if not
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		return false;
	}
}
