package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.saveToFile;

@Getter
class OPALMaterialInfo extends Material {
	private final String materialName;
	private final long   nodeId;
	private final Material originalMaterial;
	private final CourseOrganizerOPAL courseOrganizer;

	public OPALMaterialInfo(String name, long nodeId, Material material, CourseOrganizerOPAL courseOrganizer) {
		this.materialName = name;
		this.nodeId = nodeId;
		this.originalMaterial = material;
		this.courseOrganizer = courseOrganizer;
	}

	public String fileName() {
		// Todo better System for file name
		return originalMaterial.getName();
	}

	public String materialType() {
		return originalMaterial.getTemplateType().getType();
	}

	public void createFile(File targetDirectory) {
		var context = courseOrganizer.getContext();
		context.setVariable("material", originalMaterial);
		context.setVariable("title", materialName);
		String htmlString = courseOrganizer.getTemplateEngine().process(materialType(), context);
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