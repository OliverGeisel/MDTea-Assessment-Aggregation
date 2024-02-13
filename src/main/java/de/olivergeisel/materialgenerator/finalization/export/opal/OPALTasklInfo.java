package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
class OPALTaskInfo extends TaskOrder implements MaterialCollectionOPAL<TaskOrder, OPALMaterialInfo> {

	private final CourseOrganizerOPAL    courseOrganizer;
	private final String                 taskName;
	private final List<OPALMaterialInfo> materials = new ArrayList<>();
	private final long                   nodeId;
	private       TaskOrder              originalTask;

	public OPALTaskInfo(TaskOrder task, CourseOrganizerOPAL courseOrganizer) {
		this.taskName = task.getName();
		this.courseOrganizer = courseOrganizer;
		this.nodeId = courseOrganizer.getNextId();
		this.originalTask = task;
		for (var material : task.getMaterials()) {
			var newMaterial = new OPALMaterialInfo(task.getName(), courseOrganizer.getNextId(), material);
			materials.add(newMaterial);
		}
	}

	public OPALTaskInfo(String taskName, CourseOrganizerOPAL courseOrganizer) {
		this.taskName = taskName;
		this.courseOrganizer = courseOrganizer;
		this.nodeId = courseOrganizer.getNextId();
	}

	public void createMaterials(File targetDirectory) {
			/*var taskDirectory = new File(targetDirectory, taskName);
			if (!taskDirectory.exists()) {
				taskDirectory.mkdirs();
			}*/
		for (var material : materials) {
			material.createFile(targetDirectory, courseOrganizer);
		}
	}

	//region setter/getter
	@Override
	public List<OPALMaterialInfo> getMaterialOrder() {
		throw new UnsupportedOperationException("Not allowed to get the materials");
	}

	@Override
	public TaskOrder getOriginalCollection() {
		return null;
	}

	public List<Material> getMaterials() {
		return Collections.unmodifiableList(materials);
	}
//endregion
}
