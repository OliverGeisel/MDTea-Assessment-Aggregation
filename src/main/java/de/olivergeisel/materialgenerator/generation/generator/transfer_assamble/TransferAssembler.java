package de.olivergeisel.materialgenerator.generation.generator.transfer_assamble;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.material.transfer.OverviewMaterial;
import de.olivergeisel.materialgenerator.generation.material.transfer.SummaryMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.*;

/**
 * Specific sub-generator for a {@link ComplexMaterial} in the transfer category. <b>PROTOTYPE and PROOF OF CONCEPT
 * VERSION.</b> Only a general class for the sub-generators in this category. Can only generate Summarys for a
 * {@link StructureGroup} (a {@link OverviewMaterial}) or a {@link Term} (a {@link SummaryMaterial}).
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ComplexMaterial
 * @see MaterialAndMapping
 * @see StructureGroup
 * @see Term
 * @see SummaryMaterial
 * @see OverviewMaterial
 * @since 1.1.0
 */
public class TransferAssembler {

	private List<MaterialAndMapping> materials;
	private KnowledgeNode knowledgeNode;

	public TransferAssembler(List<MaterialAndMapping> materials, KnowledgeNode knowledgeNode) {
		this.materials = materials;
		this.knowledgeNode = knowledgeNode;
	}


	public List<MaterialAndMapping> createOverview(Collection<MaterialAndMapping> summaryMaterials) {
		var StructureMapping = new HashMap<String, List<SummaryMaterial>>();
		var summaries = summaryMaterials.stream().map(it -> (SummaryMaterial) it.material()).toList();
		for (var m : summaries) {
			var key = m.getStructureId();
			if (StructureMapping.containsKey(key)) {
				StructureMapping.get(key).add(m);
			} else {
				StructureMapping.put(key, new LinkedList<>(List.of(m)));
			}
		}
		var back = new ArrayList<MaterialAndMapping>();
		for (var entry : StructureMapping.entrySet()) {
			var material = new OverviewMaterial(entry.getKey(), "");
			entry.getValue().forEach(material::append);
			back.add(new MaterialAndMapping<>(material, createMappingEntry(material, summaryMaterials)));
		}
		return back;
	}

	/**
	 * Creates {@link SummaryMaterial}s for the {@link Term}s that is related to the {@link KnowledgeObject} that is the
	 * {@link KnowledgeNode}.
	 *
	 * @return list of {@link MaterialAndMapping} that contains the {@link SummaryMaterial}s.
	 */
	public List<MaterialAndMapping> createSummary() {
		var back = new LinkedList<MaterialAndMapping>();
		if (!(knowledgeNode.getMainElement() instanceof Term term)) {
			return back;
		}
		// filter for matching materials
		var definitions =
				materials.stream().filter(it -> it.material().getTemplateType().equals(TemplateType.DEFINITION))
						 .toList();
		var examples = materials.stream().filter(it -> it.material().getType().equals(MaterialType.EXAMPLE)).toList();
		var lists = materials.stream()
							 .filter(it -> it.material().getTemplateType().equals(TemplateType.LIST))
							 .toList();
		var material = new SummaryMaterial(term);
		var mapping = new MaterialMappingEntry(material);
		material.setTemplateType(TemplateType.SUMMARY);
		definitions.forEach(it -> {
			material.append(it.material());
			mapping.addAll(it.mapping().getRelatedElements());
		});
		examples.forEach(it -> {
			material.append(it.material());
			mapping.addAll(it.mapping().getRelatedElements());
		});
		lists.forEach(it -> {
			material.append(it.material());
			mapping.addAll(it.mapping().getRelatedElements());
		});
		if (!material.getParts().isEmpty()) {
			back.add(new MaterialAndMapping<>(material, mapping));
		}
		return back;
	}

	private MaterialMappingEntry createMappingEntry(ComplexMaterial complexMaterial,
			Collection<MaterialAndMapping> materials) {
		if (materials == null) {
			materials = this.materials;
		}
		var collect = new LinkedList<MaterialMappingEntry>();
		for (var m : complexMaterial.getParts()) {
			for (var ma : materials) {
				if (ma.material().equals(m)) { // Equals cant be used
					collect.add(ma.mapping());
				}
			}
		}
		var allElements = collect.stream().flatMap(it -> it.getRelatedElements().stream()).toList();
		var back = new MaterialMappingEntry(complexMaterial);
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
