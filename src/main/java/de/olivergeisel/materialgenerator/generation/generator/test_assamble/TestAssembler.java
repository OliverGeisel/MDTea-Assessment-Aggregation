package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestPer;
import de.olivergeisel.materialgenerator.generation.generator.Assembler;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

import java.util.LinkedList;
import java.util.List;

/**
 * Assembles a test from a given knowledge node and a list of related materials.
 * The test is assembled according to the given configuration.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TestConfiguration
 * @see TestMaterial
 * @see KnowledgeNode
 * @see MaterialAndMapping
 * @since 1.1.0
 */
public class TestAssembler<T extends TestMaterial> implements Assembler<T> {

	private KnowledgeNode     knowledgeNode;
	private List<MaterialAndMapping> relatedMaterials;
	private TestConfiguration configuration;

	public TestAssembler(KnowledgeNode knowledgeNode, List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration) {
		this.knowledgeNode = knowledgeNode;
		this.relatedMaterials = relatedMaterials;
		this.configuration = configuration;

	}

	private static <T extends TestMaterial> AssemblerStrategy<T> getStrategy(TestPer level) {
		return switch (level) {
			case TASK -> new TaskStrategy<T>();
			case SUB_GROUP -> new SubGroupStrategy<T>();
			case GROUP -> new GroupStrategy<T>();
			case CHAPTER -> new ChapterStrategy<T>();
			case ALL -> new AllStrategy<T>();
		};
	}

	public List<MaterialAndMapping<T>> assemble() {
		if (knowledgeNode == null || relatedMaterials == null || configuration == null) {
			throw new IllegalStateException("Assembler not configured correctly. Please check your configuration.");
		}
		var back = new LinkedList<MaterialAndMapping<T>>();
		// load assemble strategy by configuration
		var taskPerLevel = configuration.getLevel();
		for (var level : taskPerLevel) {
			AssemblerStrategy<TestMaterial> strategy = getStrategy(level);
			var materials = strategy.assemble(knowledgeNode, relatedMaterials, configuration);
			var casted = materials.stream().map(m -> (MaterialAndMapping<T>) m).toList();
			back.addAll(casted);
		}
		return back;
	}


	//region setter/getter
	public KnowledgeNode getKnowledgeNode() {
		return knowledgeNode;
	}

	public void setKnowledgeNode(KnowledgeNode knowledgeNode) {
		this.knowledgeNode = knowledgeNode;
	}

	public List<MaterialAndMapping> getRelatedMaterials() {
		return relatedMaterials;
	}

	public void setRelatedMaterials(
			List<MaterialAndMapping> relatedMaterials) {
		this.relatedMaterials = relatedMaterials;
	}

	public TestConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(TestConfiguration configuration) {
		this.configuration = configuration;
	}
//endregion
}



