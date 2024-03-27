package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.List;


/**
 * Basic class for all materials that are used for assessment. A Test is a collection of Tasks.
 * But every test has a {@link TestConfiguration} that defines the pass criteria, and the number of itemConfigurations.
 *
 * @see TestConfiguration
 * @see Material
 * @see ComplexMaterial
 * @see MaterialType
 * @see ItemMaterial
 *
 * @version 1.1.0
 * @since 1.1.0
 * @author Oliver Geisel
 */
@Entity
public class TestMaterial extends ComplexMaterial {

	@Embedded
	@AttributeOverride(name = "description", column = @Column(name = "configuration_description"))
	private TestConfiguration testConfiguration;

	public TestMaterial() {
		super(MaterialType.TEST, TemplateType.TEST);
	}

	public TestMaterial(List<Material> parts, TestConfiguration testConfiguration) {
		super(MaterialType.TEST, TemplateType.TEST, parts);
		this.testConfiguration = testConfiguration;
		while (getParts().size() > testConfiguration.getNumberTasks()) {
			getParts().removeLast();
		}
	}

	public TestMaterial(List<Material> parts, TestConfiguration testConfiguration, Term term) {
		super(MaterialType.TEST, TemplateType.TEST, term, parts);
		this.testConfiguration = testConfiguration;
		while (getParts().size() > testConfiguration.getNumberTasks()) {
			getParts().removeLast();
		}
	}

	//region setter/getter
	public TestConfiguration getTestConfiguration() {
		return testConfiguration;
	}

	//endregion
}
