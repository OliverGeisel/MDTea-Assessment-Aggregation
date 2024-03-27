package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.export.opal.test.OPALItemMaterialInfo;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

public class OPAlTestMaterialInfo extends OPALComplexMaterialInfo<TestMaterial, OPALItemMaterialInfo> {


	public OPAlTestMaterialInfo(TestMaterial originalMaterial, CourseOrganizerOPAL courseOrganizerOPAL) {
		super(originalMaterial, courseOrganizerOPAL);
	}

	public OPAlTestMaterialInfo(TestMaterial originalMaterial) {
		super(originalMaterial, new CourseOrganizerOPAL());
	}


}
