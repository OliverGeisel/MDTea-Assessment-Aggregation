package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Represents a chapter in the final course plan.
 * <p>
 * Is the largest unit in the course plan. Contains a list of {@link GroupOrder}s.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see MaterialOrderPart
 * @since 0.2.0
 */
@Entity
public class ChapterOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<GroupOrder> groupOrder;

	/**
	 * Creates a new ChapterOrder from a StructureChapter
	 *
	 * @param stChapter the chapter to be ordered
	 * @param goals     the goals of the course
	 * @throws IllegalArgumentException if chapter is null
	 */
	public ChapterOrder(StructureChapter stChapter, Set<Goal> goals) throws IllegalArgumentException {
		groupOrder = new LinkedList<>();
		if (stChapter == null) throw new IllegalArgumentException("chapter must not be null");
		for (var group : stChapter.getParts()) {
			groupOrder.add(new GroupOrder(group, goals));
		}
		setName(stChapter.getName());
		var chapterTopic = stChapter.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(chapterTopic)))
						 .findFirst().orElse(null);
		setTopic(topic);
		stChapter.getAlternatives().forEach(this::appendAlias);
	}


	protected ChapterOrder() {
		groupOrder = new LinkedList<>();
	}

	public void moveUp(GroupOrder group) {
		int index = groupOrder.indexOf(group);
		if (index > 0) {
			groupOrder.remove(index);
			groupOrder.add(index - 1, group);
		}
	}

	public void moveDown(GroupOrder group) {
		int index = groupOrder.indexOf(group);
		if (-1 < index && index < groupOrder.size() - 1) {
			groupOrder.remove(index);
			groupOrder.add(index + 1, group);
		}
	}

	public boolean append(GroupOrder group) {
		return groupOrder.add(group);
	}

	public boolean remove(GroupOrder group) {
		return groupOrder.remove(group);
	}

	public MaterialOrderPart find(UUID id) {
		if (id.equals(this.getId())) return this;
		return groupOrder.stream().map(g -> g.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public GroupOrder findGroup(UUID groupID) {
		return groupOrder.stream().filter(g -> g.getId().equals(groupID)).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID taskId) {
		return groupOrder.stream().map(g -> g.findTask(taskId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	@Override
	public Material findMaterial(UUID materialId) {
		return groupOrder.stream().map(g -> g.findMaterial(materialId)).filter(Objects::nonNull).findFirst()
						 .orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		return getRelevance();
	}

	@Override
	public int materialCount() {
		return groupOrder.stream().mapToInt(GroupOrder::materialCount).sum();
	}

	/**
	 * Assigns a set of materials to the parts.
	 *
	 * @param materials the materials to be assigned
	 * @return the set of materials that assigned to the chapter
	 */
	@Override
	public Set<Material> assignMaterial(Set<Material> materials) {
		return groupOrder.stream().map(g -> g.assignMaterial(materials)).flatMap(Collection::stream)
						 .collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("ChapterOrder does not support assign(Material)");
	}

	/**
	 * Assigns a materials to a part.
	 *
	 * @param assigner A MaterialAssigner that provides the materials
	 * @return true if the assignment was successful
	 */
	@Override
	public boolean assignMaterial(MaterialAssigner assigner) {
		groupOrder.forEach(g -> g.assignMaterial(assigner));
		return true;
	}

	@Override
	public boolean remove(UUID partId) {
		if (groupOrder.stream().anyMatch(it -> it.getId().equals(partId))) {
			groupOrder.removeIf(it -> it.getId().equals(partId));
			return true;
		}
		return groupOrder.stream().anyMatch(g -> g.remove(partId));
	}

	//region setter/getter

	/**
	 * Get the relevance of the chapter.
	 *
	 * @return the relevance of the chapter. If no relevance is set, TO_SET is returned.
	 */
	@Override
	public Relevance getRelevance() {
		return groupOrder.stream().map(GroupOrder::getRelevance).max(Comparator.comparingInt(Enum::ordinal))
						 .orElse(Relevance.TO_SET);
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return groupOrder.stream().allMatch(GroupOrder::isValid)
			   && groupOrder.stream().allMatch(group -> group.getRelevance().compareTo(getRelevance()) < 1);
	}

	public List<GroupOrder> getGroupOrder() {
		return Collections.unmodifiableList(groupOrder);
	}
//endregion

	@Override
	public String toString() {
		return STR."ChapterOrder{name='\{getName()}', id=\{getId()}, topic=\{getTopic()}}";
	}
}
