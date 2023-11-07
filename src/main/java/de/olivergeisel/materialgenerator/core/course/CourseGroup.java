package de.olivergeisel.materialgenerator.core.course;

/**
 * A CourseGroup is a collection of {@link CourseChapterPart}s.
 * <p>
 * A CourseGroup can contain other CourseGroups and {@link CourseChapterLeafPart}s.
 * A CourseGroup can not contain itself.
 * A CourseGroup can not contain a CourseChapterCollectionPart that contains this CourseGroup.
 */
public class CourseGroup extends CourseChapterCollectionPart {


	public boolean add(CourseChapterPart part) {
		if (this == part) {
			throw new IllegalArgumentException("Cant add part to its self");
		}
		if (part instanceof CourseChapterCollectionPart collection && collection.contains(this)) {
			throw new IllegalArgumentException("Part contains same Group!");
		}
		return parts.add(part);
	}


	@Override
	public boolean contains(CourseChapterPart part) {
		if (part instanceof CourseChapterLeafPart) {
			return false;
		}
		if (part instanceof CourseChapterCollectionPart collection && collection != this) {
			for (var subPart : collection.parts) {
				if (part == subPart ||
					subPart instanceof CourseChapterCollectionPart subCollection && subCollection.contains(part)) {
					return true;
				}
			}
		}
		return false;
	}
}
