package de.olivergeisel.materialgenerator.finalization.export.opal.test;

import de.olivergeisel.materialgenerator.finalization.export.opal.CourseOrganizerOPAL;
import de.olivergeisel.materialgenerator.finalization.export.opal.OPALMaterialInfo;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import lombok.Getter;

@Getter
public class OPALItemMaterialInfo<I extends ItemMaterial> extends OPALMaterialInfo {

	private final I originalMaterial;

	public OPALItemMaterialInfo(String relatedTaskName, I originalMaterial, CourseOrganizerOPAL courseOrganizerOPAL) {
		super(relatedTaskName, originalMaterial, courseOrganizerOPAL);
		this.originalMaterial = originalMaterial;
	}


}
