package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.List;


/**
 * Basic class for all materials that are used for assessment. A Test is a collection of Tasks.
 * But every test has a {@link TestConfiguration} that defines the pass criteria, and the number of tasks.
 */
@Entity
public class TestMaterial extends ComplexMaterial {

	// Todo add test configuration

	public TestMaterial() {
		super(MaterialType.TEST, TemplateType.TASK);
	}

	public TestMaterial(List<Material> parts) {
		super(MaterialType.TEST, TemplateType.TASK, parts);
	}
}
