package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
class OPALGroupInfo extends GroupOrder
		implements MaterialCollectionOPAL<GroupOrder, OPALTaskInfo> {

	private final CourseOrganizerOPAL courseOrganizer;
	private final String              groupName;
	private final List<OPALTaskInfo>  tasks = new ArrayList<>();
	private final long                nodeId;
	private final GroupOrder          originalGroup;

	OPALGroupInfo(GroupOrder group, CourseOrganizerOPAL courseOrganizer) {
		this.groupName = group.getName();
		this.courseOrganizer = courseOrganizer;
		this.nodeId = courseOrganizer.getNextId();
		this.originalGroup = group;
		for (var task : group.getTaskOrder()) {
			var newTask = new OPALTaskInfo(task, courseOrganizer);
			tasks.add(newTask);
		}
	}

	public void createMaterials(File targetDirectory) {
		// todo change inner parts to a collection (itemConfigurations and groups)
			/*var groupDirectory = new File(targetDirectory, groupName);
			if (!groupDirectory.exists()) {
				groupDirectory.mkdirs();
			}*/
		for (var task : tasks) {
			task.createMaterials(targetDirectory);
		}
	}

	//region setter/getter
	@Override
	public List<OPALTaskInfo> getMaterialOrder() {
		return Collections.unmodifiableList(tasks);
	}

	@Override
	public String getName() {
		return groupName;
	}


	@Override
	public GroupOrder getOriginalCollection() {
		return originalGroup;
	}

	@Override
	public List<TaskOrder> getTaskOrder() {
		return new ArrayList<>(tasks);
	}
//endregion
}

