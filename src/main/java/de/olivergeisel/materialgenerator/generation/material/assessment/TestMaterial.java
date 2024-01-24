package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class TestMaterial extends ComplexMaterial {

	public TestMaterial() {
		super(MaterialType.TEST);
	}

	public TestMaterial(List<Material> parts) {
		super(MaterialType.TEST, parts);
	}
}
