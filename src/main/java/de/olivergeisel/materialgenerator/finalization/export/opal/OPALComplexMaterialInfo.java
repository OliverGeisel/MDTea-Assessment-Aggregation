package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.export.opal.test.OPALItemMaterialInfo;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;

import java.util.ArrayList;
import java.util.List;

public class OPALComplexMaterialInfo<O extends ComplexMaterial, I extends OPALMaterialInfo> extends ComplexMaterial {

	private final long                   nodeId;
	private final CourseOrganizerOPAL    courseOrganizerOPAL;
	private final O                      originalMaterial;
	private final List<OPALMaterialInfo> parts;

	public OPALComplexMaterialInfo(O originalMaterial, CourseOrganizerOPAL courseOrganizerOPAL) {
		this.courseOrganizerOPAL = courseOrganizerOPAL;
		this.nodeId = courseOrganizerOPAL.getNodeId();
		this.originalMaterial = originalMaterial;
		this.parts = new ArrayList<>();
		for (var material : originalMaterial.getParts()) {
			if (material instanceof ItemMaterial item) {
				var newItem = new OPALItemMaterialInfo(originalMaterial.getName(), item, courseOrganizerOPAL);
				parts.add(newItem);
			} else {
				// todo add case for complex material
				var newItem = new OPALMaterialInfo(originalMaterial.getName(), material, courseOrganizerOPAL);
				parts.add(newItem);
			}
		}
	}

	public OPALComplexMaterialInfo(O originalMaterial) {
		this(originalMaterial, new CourseOrganizerOPAL());
	}

	//region setter/getter
	public ComplexMaterial getOriginalMaterial() {
		return originalMaterial;
	}

	public long getNodeId() {
		return nodeId;
	}

	public CourseOrganizerOPAL getCourseOrganizer() {
		return courseOrganizerOPAL;
	}

	@Override
	public String getName() {
		return originalMaterial.getName();
	}

	@Override
	public List<Material> getParts() {
		return parts.stream().map(i -> (Material) i).toList();
	}

	public List<I> getMaterialInfos() {
		return parts.stream().map(i -> (I) i).toList();
	}
//endregion
}
