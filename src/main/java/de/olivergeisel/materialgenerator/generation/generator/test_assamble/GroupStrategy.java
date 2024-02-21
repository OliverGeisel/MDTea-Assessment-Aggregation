package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GroupStrategy<T extends TestMaterial> implements AssemblerStrategy<T> {

	@Override
	public List<MaterialAndMapping<T>> assemble(KnowledgeNode knowledgeNode,
			List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration) {

		var back = new LinkedList<MaterialAndMapping<T>>();
		// find items
		var items = relatedMaterials.stream()
									.filter(materialAndMapping -> materialAndMapping.material() instanceof ItemMaterial);
		var structureItemMapping = new HashMap<String, List<MaterialAndMapping>>();
		for (var item : items.toList()) {
			var itemMaterial = item.material();
			var structureId = itemMaterial.getStructureId();
			structureItemMapping.computeIfAbsent(structureId, _ -> new LinkedList<>()).add(item);
		}
		for (var entry : structureItemMapping.entrySet()) {
			var elements = entry.getValue().stream().flatMap(it -> it.mapping().getRelatedElements().stream());
			var materials = entry.getValue().stream().map(MaterialAndMapping::material).toList();
			var testMaterial = new TestMaterial(materials, configuration);
			testMaterial.setName(STR."Test für \{entry.getKey()}");
			var mappingEntry = new MaterialMappingEntry(testMaterial, elements.toArray(String[]::new));
			var mapping = new MaterialAndMapping<T>(mappingEntry);
			back.add(mapping);
		}
		return back;
	}
}
