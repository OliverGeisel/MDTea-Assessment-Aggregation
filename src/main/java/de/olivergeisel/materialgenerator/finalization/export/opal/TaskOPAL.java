package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.saveToFile;

@Getter
class OPALMaterialInfo extends Material {
	private final String   taskName;
	private final long     nodeId;
	private final Material originalMaterial;

	public OPALMaterialInfo(String name, long nodeId, Material material) {
		this.taskName = name;
		this.nodeId = nodeId;
		this.originalMaterial = material;
	}

	public String fileName() {
		return originalMaterial.getName();
	}

	public String materialType() {
		return originalMaterial.getTemplateType().getType();
	}

	public void createFile(File targetDirectory, CourseOrganizerOPAL courseOrganizer) {
		String htmlString = courseOrganizer.templateEngine.process(materialType(), courseOrganizer.context);
		saveToFile(htmlString, targetDirectory, STR."\{fileName()}.html");
	}

	@Override
	public MaterialOrderPart find(UUID id) {
		return null;
	}

//region setter/getter
	@Override
	public boolean isValid() {
		return false;
	}
//endregion
}