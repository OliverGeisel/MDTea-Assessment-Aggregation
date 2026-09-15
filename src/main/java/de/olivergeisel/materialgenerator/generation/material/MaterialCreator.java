package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.material.transfer.ExampleMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaterialCreator {

	public MaterialAndMapping<? extends Material> createWikiMaterial(KnowledgeElement mainTerm, String name,
			TemplateType templateType,
			Map<String, String> values, KnowledgeElement... relatedElements) {
		var newMaterial = new Material(MaterialType.WIKI, mainTerm);
		return fillMaterial(mainTerm, name, templateType, values, newMaterial, relatedElements);
	}

	public MaterialAndMapping<? extends ExampleMaterial> createExampleMaterial(KnowledgeElement example,
			KnowledgeElement mainTerm, String name,
			TemplateType templateType, Map<String, String> values, KnowledgeElement... relatedElements) {
		var imagename = "";
		var data = new HashMap<String, String>();
		Arrays.stream(example.getContent().split(";")).forEach(line -> {
			var lineElements = line.split(":");
			if (lineElements.length == 2) {
				data.put(lineElements[0].toUpperCase().trim(), lineElements[1].trim());
			}
		});
		data.putIfAbsent("text", "");
		imagename = data.computeIfAbsent("IMAGE", k -> "NO_IMAGE");
		ExampleMaterial newMaterial =
				imagename.equals("NO_IMAGE")
						? new ExampleMaterial(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId())
						: new ExampleMaterial(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId(),
						imagename);
		var newValues = new HashMap<String, String>();
		newValues.putAll(data);
		newValues.putAll(values);
		return fillMaterial(mainTerm, name, templateType, newValues, newMaterial, relatedElements);
	}

	public MaterialAndMapping<? extends Material> createProofMaterial(KnowledgeElement proof, KnowledgeElement mainTerm,
			String name,
			de.olivergeisel.materialgenerator.generation.templates.TemplateType templateType,
			Map<String, String> values, KnowledgeElement... relatedElements) {
		// Todo Create ProofMaterial class and use it here instead of Material
		Material newMaterial = new Material(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId(),
				MaterialType.WIKI, templateType);
		return fillMaterial(mainTerm, name, templateType, values, newMaterial, relatedElements);
	}

	private <M extends Material> MaterialAndMapping<? extends M> fillMaterial(KnowledgeElement mainTerm, String name,
			TemplateType templateType,
			Map<String, String> values, M newMaterial, KnowledgeElement[] relatedElements) {
		newMaterial.setName(name);
		newMaterial.setTemplateType(templateType);
		newMaterial.setValues(values);
		MaterialMappingEntry<? extends Material> mapping = new MaterialMappingEntry<>(newMaterial);
		mapping.add(mainTerm);
		mapping.add(relatedElements);
		return new MaterialAndMapping(newMaterial, mapping);
	}
}
