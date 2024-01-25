package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestPer;
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
public class TestAssembler {

	private KnowledgeNode            knowledgeNode;
	private List<MaterialAndMapping> relatedMaterials;
	private TestConfiguration        configuration;

	public TestAssembler(KnowledgeNode knowledgeNode, List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration) {
		this.knowledgeNode = knowledgeNode;
		this.relatedMaterials = relatedMaterials;
		this.configuration = configuration;

	}

	private static AssemblerStrategy getStrategy(TestPer level) {
		return switch (level) {
			case MATERIAL -> new MaterialStrategy();
			case SUB_GROUP -> new SubGroupStrategy();
			case GROUP -> new GroupStrategy();
			case CHAPTER -> new ChapterStrategy();
			case ALL -> new AllStrategy();
		};
	}

	public List<TestMaterial> assemble() {
		if (knowledgeNode == null || relatedMaterials == null || configuration == null) {
			throw new IllegalStateException("Assembler not configured correctly. Please check your configuration.");
		}
		var back = new LinkedList<TestMaterial>();
		// load assemble strategy by configuration
		var taskPerLevel = configuration.level();
		for (var level : taskPerLevel) {
			var strategy = getStrategy(level);
			var materials = strategy.assemble(knowledgeNode, relatedMaterials, configuration);
			back.addAll(materials);
		}
		// todo order;


		// TODO: Think if something is missing. Check duplicate etc...
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



