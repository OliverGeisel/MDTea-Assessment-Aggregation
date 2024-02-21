package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElementPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a group in the final course plan.
 * <p>
 * Is a unit in the course plan. Contains a list of {@link TaskOrder}s.
 * Can be nested in other groups or chapters.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see MaterialOrderPart
 * @see ChapterOrder
 * @since 0.2.0
 */
@Entity
public class GroupOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<TaskOrder> taskOrder = new LinkedList<>();

	/**
	 * Creates a new GroupOrder from a StructureGroup
	 *
	 * @param part the group to be ordered
	 * @throws IllegalArgumentException if part is null
	 */
	public GroupOrder(StructureElementPart part, Set<Goal> goals) throws IllegalArgumentException {
		if (part == null) throw new IllegalArgumentException("group must not be null");
		if (!(part instanceof StructureGroup group)) {
			throw new IllegalArgumentException("part must be a StructureGroup");
		}
		for (var task : group.getParts()) {
			if (task instanceof StructureTask sTask) {
				taskOrder.add(new TaskOrder(sTask, goals));
			} else if (task instanceof StructureGroup) {
				throw new IllegalArgumentException("nested groups are not supported");
			}
		}
		setName(group.getName());
		var groupTopic = group.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(groupTopic)))
						 .findFirst().orElse(Topic.empty());
		setTopic(topic);
		part.getAlternatives().forEach(this::appendAlias);
	}

	protected GroupOrder() {

	}

	public void moveUp(TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (index > 0) {
			taskOrder.remove(index);
			taskOrder.add(index - 1, task);
		}
	}

	public void moveDown(TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (-1 < index && index < taskOrder.size() - 1) {
			taskOrder.remove(index);
			taskOrder.add(index + 1, task);
		}
	}

	public boolean remove(TaskOrder task) {
		return taskOrder.remove(task);
	}

	public void append(TaskOrder task) {
		taskOrder.add(task);
	}

	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return taskOrder.stream().map(t -> t.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		return getRelevance();
	}

	public TaskOrder findTask(UUID taskId) {
		return taskOrder.stream().filter(t -> t.getId().equals(taskId)).findFirst().orElse(null);
	}

	@Override
	public Material findMaterial(UUID materialId) {
		return taskOrder.stream().map(t -> t.findMaterial(materialId)).filter(Objects::nonNull).findFirst()
						.orElse(null);
	}

	/**
	 * Assigns materials to group part.
	 *
	 * @param materials the materials to assign
	 * @return the assigned materials
	 */
	@Override
	public Set<Material> assignMaterial(Set<Material> materials) {
		return taskOrder.stream().map(t -> t.assignMaterial(materials)).flatMap(Collection::stream)
						.collect(Collectors.toSet());
	}

	/**
	 * @param material the (complex)material to assign
	 * @return true if the material was assigned
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	@Override
	public boolean assign(Material material) throws UnsupportedOperationException {
		if (!(material instanceof ComplexMaterial complexMaterial)) {
			return false;
		}
		assignComplex(complexMaterial);
		return true;
	}

	/**
	 * Assigns materials to this part.
	 *
	 * @param assigner the assigner to use
	 * @return true if materials were assigned
	 */
	@Override
	public boolean assignMaterial(MaterialAssigner assigner) {
		assigner.assign(this);
		return true;
	}

	@Override
	public int materialCount() {
		return taskOrder.stream().mapToInt(TaskOrder::materialCount).sum();
	}

	@Override
	public boolean remove(UUID partId) {
		if (taskOrder.stream().anyMatch(it -> it.getId().equals(partId))) {
			taskOrder.removeIf(it -> it.getId().equals(partId));
			return true;
		}
		return taskOrder.stream().anyMatch(t -> t.remove(partId));
	}

	@Override
	public Collection<NameAndId> collectionsNameAndId() {
		var back = new LinkedList<NameAndId>();
		back.add(new NameAndId(getName(), getId()));
		taskOrder.forEach(t -> back.addAll(t.collectionsNameAndId()));
		return back;
	}

	@Override
	public Iterator<MaterialOrderPart> iterator() {
		return taskOrder.stream().map(it -> (MaterialOrderPart) it).iterator();
	}

	@Override
	public void forEach(Consumer<? super MaterialOrderPart> action) {
		taskOrder.stream().map(it -> (MaterialOrderPart) it).forEach(action);
	}

	@Override
	public Spliterator<MaterialOrderPart> spliterator() {
		return taskOrder.stream().map(it -> (MaterialOrderPart) it).spliterator();
	}

	//region setter/getter

	/**
	 * Get the relevance of this part.
	 *
	 * @return the relevance of this part
	 */
	@Override
	public Relevance getRelevance() {
		return taskOrder.stream().map(TaskOrder::getRelevance).max(Comparator.naturalOrder()).orElse(Relevance.TO_SET);
	}

	@Override
	public List<UUID> getCollectionIds() {
		var back = new LinkedList<UUID>();
		back.add(getId());
		taskOrder.forEach(t -> back.addAll(t.getCollectionIds()));
		return back;
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return taskOrder.stream().allMatch(MaterialOrderPart::isValid)
			   && taskOrder.stream().allMatch(t -> t.getRelevance().ordinal() <= getRelevance().ordinal());
	}

	public List<TaskOrder> getTaskOrder() {
		return Collections.unmodifiableList(taskOrder);
	}
//endregion

	@Override
	public String toString() {
		return STR."GroupOrder{name='\{getName()}\{'\''}, id=\{getId()}, topic=\{getTopic()}}";
	}
}
