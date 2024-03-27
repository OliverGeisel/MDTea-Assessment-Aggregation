package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.saveToFile;


/**
 * Information about a {@link Material} for the OPAL export.
 * This class is used to create the xml structure for the OPAL export.
 * It contains the original material,
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @see CourseOrganizerOPAL
 * @see SinglePageOPAL
 * @since 1.1.0
 */
@Getter
public class OPALMaterialInfo extends Material {

	private static final String FILE_ENDING = ".html";

	private final String relatedTaskName;
	private final long   nodeId;
	private final Material originalMaterial;
	private final CourseOrganizerOPAL courseOrganizer;

	public OPALMaterialInfo(String relatedTaskName, Material material, CourseOrganizerOPAL courseOrganizer) {
		this.relatedTaskName = relatedTaskName;
		this.nodeId = courseOrganizer.getNextId();
		this.originalMaterial = material;
		this.courseOrganizer = courseOrganizer;
	}

	public String completeFileName() {
		return fileName() + FILE_ENDING;
	}

	public String fileName() {
		// Todo better System for file name
		return originalMaterial.getName();
	}

	public void createFile(File targetDirectory) {
		var context = courseOrganizer.getContext();
		context.setVariable("material", originalMaterial);
		context.setVariable("title", relatedTaskName);
		context.setVariable("rootPath", ".");
		String htmlString = courseOrganizer.getTemplateEngine().process(materialType(), context);
		// todo think about using the node id
		saveToFile(htmlString, targetDirectory, completeFileName());
	}

	public String materialType() {
		return originalMaterial.getTemplateType().getType();
	}

//region setter/getter

	@Override
	public MaterialOrderPart find(UUID id) {
		return null;
	}
	@Override
	public String getName() {
		return originalMaterial.getName();
	}
	@Override
	public boolean isValid() {
		return false;
	}
//endregion
}