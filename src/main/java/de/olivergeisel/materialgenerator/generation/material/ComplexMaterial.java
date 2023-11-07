package de.olivergeisel.materialgenerator.generation.material;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ComplexMaterial extends Material {
	@OneToMany
	private final List<Material> parts = new ArrayList<>();

	public ComplexMaterial(MaterialType type, List<Material> parts) {
		super(type);
		this.parts.addAll(parts);
	}

	public ComplexMaterial(List<Material> parts) {
		super(MaterialType.COMPLEX);
		this.parts.addAll(parts);
	}

	public ComplexMaterial(MaterialType type) {
		super(type);
	}

	protected ComplexMaterial() {
		super(MaterialType.COMPLEX);
	}

	//region setter/getter
	public List<Material> getParts() {
		return parts;
	}
//endregion
}
