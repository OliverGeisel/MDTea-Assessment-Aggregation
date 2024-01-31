package de.olivergeisel.materialgenerator.generation.generator.transfer_assamble;

import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.material.transfer.SummaryMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.LinkedList;
import java.util.List;

/**
 * Specific sub-generator for a {@link ComplexMaterial} in the transfer category. <b>PROTOTYPE and PROOF OF CONCEPT
 * VERSION.</b> Only a general class for the sub-generators in this category. Can only generate Summarys for a
 * {@link StructureGroup}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ComplexMaterial
 * @since 1.1.0
 */
public class TransferAssembler {

	private List<MaterialAndMapping> materials;
	private KnowledgeNode            knowledgeNode;

	public TransferAssembler(List<MaterialAndMapping> materials, KnowledgeNode knowledgeNode) {
		this.materials = materials;
		this.knowledgeNode = knowledgeNode;
	}

	public List<MaterialAndMapping> createSummary() {
		var back = new LinkedList<MaterialAndMapping>();
		// filter for matching materials
		var definitions =
				materials.stream().filter(it -> it.material().getTemplateInfo().getTemplateType()
												  .equals(TemplateType.DEFINITION)).toList();
		var examples = materials.stream().filter(it -> it.material().getType().equals(MaterialType.EXAMPLE)).toList();
		//var proofs =
		//		materials.stream().filter(it -> it.material().getTemplateInfo().getTemplateType().equals()).toList();
		var lists =
				materials.stream()
						 .filter(it -> it.material().getTemplateInfo().getTemplateType().equals(TemplateType.LIST))
						 .toList();
		var material = new SummaryMaterial(getKnowledgeObjectName());
		definitions.forEach(it -> material.append(it.material()));
		examples.forEach(it -> material.append(it.material()));
		lists.forEach(it -> material.append(it.material()));
		back.add(new MaterialAndMapping<>(material, createMappingEntry(material)));
		return back;
	}

	private MaterialMappingEntry createMappingEntry(SummaryMaterial summaryMaterial) {
		var collect = new LinkedList<MaterialMappingEntry>();
		for (var m : summaryMaterial.getParts()) {
			for (var ma : materials) {
				if (ma.material().equals(m)) {
					collect.add(ma.mapping());
				}
			}
		}
		var allElements = collect.stream().flatMap(it -> it.getRelatedElements().stream()).toList();
		var back = new MaterialMappingEntry(summaryMaterial);
		back.setRelatedElements(allElements);
		return back;
	}

//region setter/getter
	/**
	 * Get the id of the {@link KnowledgeObject} that all materials are related to.
	 *
	 * @return id of the {@link KnowledgeObject}
	 */
	private String getKnowledgeObjectName() {
		return knowledgeNode.getStructurePoint().getId();
	}
//endregion


}
