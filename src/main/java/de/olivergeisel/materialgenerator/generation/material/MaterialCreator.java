package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaterialCreator {

	public MaterialAndMapping createWikiMaterial(KnowledgeElement mainTerm, String name, TemplateInfo templateInfo,
			Map<String, String> values, KnowledgeElement... relatedElements) {
		var newMaterial = new Material(MaterialType.WIKI, mainTerm);
		return fillMaterial(mainTerm, name, templateInfo, values, newMaterial, relatedElements);
	}

	public MaterialAndMapping createExampleMaterial(KnowledgeElement example, KnowledgeElement mainTerm, String name,
			TemplateInfo templateInfo, Map<String, String> values, KnowledgeElement... relatedElements) {
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
		Material newMaterial;
		if (imagename.equals("NO_IMAGE")) {
			newMaterial = new ExampleMaterial(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId());
		} else newMaterial = new ExampleMaterial(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId(),
				imagename);
		var newValues = new HashMap<String, String>();
		newValues.putAll(data);
		newValues.putAll(values);
		return fillMaterial(mainTerm, name, templateInfo, newValues, newMaterial, relatedElements);
	}

	public MaterialAndMapping createProofMaterial(KnowledgeElement proof, KnowledgeElement mainTerm, String name,
			TemplateInfo templateInfo, Map<String, String> values, KnowledgeElement... relatedElements) {
		Material newMaterial = new Material(mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId(),
				MaterialType.WIKI, templateInfo);
		return fillMaterial(mainTerm, name, templateInfo, values, newMaterial, relatedElements);
	}

	private MaterialAndMapping fillMaterial(KnowledgeElement mainTerm, String name, TemplateInfo templateInfo,
			Map<String, String> values, Material newMaterial, KnowledgeElement[] relatedElements) {
		newMaterial.setName(name);
		newMaterial.setTemplateInfo(templateInfo);
		newMaterial.setValues(values);
		MaterialMappingEntry mapping = new MaterialMappingEntry(newMaterial);
		mapping.add(mainTerm);
		mapping.add(relatedElements);
		return new MaterialAndMapping(newMaterial, mapping);
	}
}
