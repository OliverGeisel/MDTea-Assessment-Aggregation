package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.material_assign.BasicMaterialAssigner;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.*;

import java.util.*;

/**
 * Order of the all Materials in the course. Structured by chapters, groups/parts and parts.
 * This is the final order of the course. It is used to generate the final course. It's editable by the user.
 */
@Entity
public class CourseOrder extends de.olivergeisel.materialgenerator.core.course.CourseOrder {
	@OneToMany(cascade = CascadeType.ALL)
	private final List<ChapterOrder> chapterOrder = new LinkedList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private       UUID               id;

	protected CourseOrder() {
	}

	public CourseOrder(CoursePlan plan, Set<Goal> goals) {
		for (var chapter : plan.getStructure().getOrder()) {
			chapterOrder.add(new ChapterOrder(chapter, goals));
		}
	}

	public int materialCount() {
		return chapterOrder.stream().mapToInt(ChapterOrder::materialCount).sum();
	}

	public MaterialOrderPart find(UUID id) {
		return chapterOrder.stream().map(c -> c.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public ChapterOrder findChapter(UUID chapterID) {
		return chapterOrder.stream().filter(c -> c.getId().equals(chapterID)).findFirst().orElse(null);
	}

	public GroupOrder findGroup(UUID groupID) {
		return chapterOrder.stream().map(c -> c.findGroup(groupID)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID partID) {
		return chapterOrder.stream().map(c -> c.findTask(partID)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public Material findMaterial(UUID materialId) {
		return chapterOrder.stream().map(c -> c.findMaterial(materialId)).filter(Objects::nonNull).findFirst()
						   .orElse(null);
	}

	/**
	 * Assign materials to the course. This will be done by the implementation of the OrderParts.
	 *
	 * @param materials Materials to assign
	 * @return True if all chapters, groups and parts could assign to the materials. False otherwise
	 */
	public boolean assignMaterial(Set<Material> materials) {
		var materialAssigner =
				new BasicMaterialAssigner(materials);
		chapterOrder.forEach(c -> c.assignMaterial(materialAssigner));
		return true;
	}

	public boolean assignMaterial(Set<Material> materials, MaterialAssigner assigner) {
		assigner.reinit(materials);
		chapterOrder.forEach(c -> c.assignMaterial(assigner));
		return true;
	}

	public void moveUp(ChapterOrder chapter) {
		int index = chapterOrder.indexOf(chapter);
		if (index > 0) {
			chapterOrder.remove(index);
			chapterOrder.add(index - 1, chapter);
		}
	}

	public void moveDown(ChapterOrder chapter) {
		int index = chapterOrder.indexOf(chapter);
		if (-1 < index && index < chapterOrder.size() - 1) {
			chapterOrder.remove(index);
			chapterOrder.add(index + 1, chapter);
		}
	}

	/**
	 * Append a new chapter to the course.
	 *
	 * @param chapter Chapter to append
	 * @return {@literal true} if the chapter could be appended. {@literal false} otherwise.
	 */
	public boolean append(ChapterOrder chapter) {
		return chapterOrder.add(chapter);
	}

	public boolean remove(ChapterOrder chapter) {
		return chapterOrder.remove(chapter);
	}

	public boolean remove(UUID partId) {
		if (chapterOrder.stream().anyMatch(it -> it.getId().equals(partId))) {
			chapterOrder.removeIf(it -> it.getId().equals(partId));
			return true;
		}
		return chapterOrder.stream().anyMatch(c -> c.remove(partId));
	}

	//region setter/getter
	public boolean isValid() {
		return chapterOrder.stream().allMatch(ChapterOrder::isValid);
	}

	public List<ChapterOrder> getChapterOrder() {
		return Collections.unmodifiableList(chapterOrder);
	}

	public UUID getId() {
		return id;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CourseOrder that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "MaterialOrder{" + "chapterOrder=" + chapterOrder + '}';
	}
}
