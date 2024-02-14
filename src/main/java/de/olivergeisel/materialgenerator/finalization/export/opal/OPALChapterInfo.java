package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
class OPALChapterInfo extends ChapterOrder implements MaterialCollectionOPAL<ChapterOrder, OPALGroupInfo> {

	private final CourseOrganizerOPAL courseOrganizer;
	private final String              chapterName;
	private final List<OPALGroupInfo> groups = new ArrayList<>();
	private final long                nodeId;
	private final ChapterOrder        originalChapter;


	public OPALChapterInfo(ChapterOrder chapter, CourseOrganizerOPAL courseOrganizer) {
		this.chapterName = chapter.getName();
		this.courseOrganizer = courseOrganizer;
		this.nodeId = courseOrganizer.getNextId();
		this.originalChapter = chapter;
		for (var group : chapter.getGroupOrder()) {
			var newGroup = new OPALGroupInfo(group, courseOrganizer);
			groups.add(newGroup);
		}
	}

	public void createMaterials(File targetDirectory) {
			/*var chapterDirectory = new File(targetDirectory, chapterName);
			if (!chapterDirectory.exists()) {
				chapterDirectory.mkdirs();
			}*/
		for (var group : groups) {
			group.createMaterials(targetDirectory);
		}
	}

	//region setter/getter
	@Override
	public String getName() {
		return chapterName;
	}

	@Override
	public List<OPALGroupInfo> getMaterialOrder() {
		return Collections.unmodifiableList(groups);
	}

	@Override
	public ChapterOrder getOriginalCollection() {
		return null;
	}

	@Override
	public List<GroupOrder> getGroupOrder() {
		return new ArrayList<>(groups);
	}
//endregion

}
