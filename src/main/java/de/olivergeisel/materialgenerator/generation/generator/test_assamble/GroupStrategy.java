package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GroupStrategy<T extends TestMaterial> implements AssemblerStrategy<T> {

	@Override
	public <CM extends T> List<MaterialAndMapping<? extends CM>> assemble(KnowledgeNode knowledgeNode,
			List<MaterialAndMapping<? extends Material>> relatedMaterials, TestConfiguration configuration) {

		var back = new LinkedList<MaterialAndMapping<? extends CM>>();
		// find items
		var items = relatedMaterials.stream()
									.filter(materialAndMapping -> materialAndMapping.material() instanceof ItemMaterial);
		var structureItemMapping = new HashMap<String, List<MaterialAndMapping<? extends Material>>>();
		for (MaterialAndMapping<? extends Material> item : items.toList()) {
			var itemMaterial = item.material();
			var structureId = itemMaterial.getStructureId();
			structureItemMapping.computeIfAbsent(structureId, _ -> new LinkedList<>()).add(item);
		}
		for (var entry : structureItemMapping.entrySet()) {
			Stream<String> elements =
					entry.getValue().stream().flatMap(it -> it.mapping().getRelatedElements().stream());
			List<? extends CM> materials = (List<? extends CM>)
					entry.getValue().stream().map(MaterialAndMapping::material).toList();
			var testMaterial = new TestMaterial(materials, configuration.clone());
			testMaterial.setName(STR."Test für \{entry.getKey()}");
			var mappingEntry = new <CM>MaterialMappingEntry(testMaterial, elements.toArray(String[]::new));
			var mapping = new MaterialAndMapping<CM>(mappingEntry);
			back.add(mapping);
		}
		return back;
	}
}
