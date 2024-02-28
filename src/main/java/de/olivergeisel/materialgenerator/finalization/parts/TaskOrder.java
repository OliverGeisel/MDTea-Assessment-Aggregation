package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.*;
import java.util.function.Consumer;

/**
 * A TaskOrder is a collection of Materials that are ordered in a specific way.
 * The order is defined by the user.
 * <p>
 * Is the smallest Collection Structure in a Plan.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see MaterialOrderPart
 * @see MaterialAssigner
 * @see Material
 * @since 0.2.0
 */
@Entity
public class TaskOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<Material> materialOrder = new LinkedList<>();
	private       Relevance      relevance     = Relevance.TO_SET;

	protected TaskOrder() {
	}

	public TaskOrder(StructureTask relatedTask, Set<Goal> goals) throws IllegalArgumentException {
		if (relatedTask == null) throw new IllegalArgumentException("relatedTask must not be null");
		setName(relatedTask.getName());
		var taskTopic = relatedTask.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(taskTopic)))
						 .findFirst().orElse(Topic.empty());
		setTopic(topic);
		relevance = relatedTask.getRelevance();
		relatedTask.getAlternatives().forEach(this::appendAlias);
	}

	public TaskOrder(String name, Topic topic, Relevance relevance) {
		setName(name);
		setTopic(topic);
		this.relevance = relevance;
	}


	public void moveUp(Material material) {
		int index = materialOrder.indexOf(material);
		if (index > 0) {
			materialOrder.remove(index);
			materialOrder.add(index - 1, material);
		}
	}

	public void moveDown(Material material) {
		int index = materialOrder.indexOf(material);
		if (-1 < index && index < materialOrder.size() - 1) {
			materialOrder.remove(index);
			materialOrder.add(index + 1, material);
		}
	}

	public boolean append(Material material) {
		return materialOrder.add(material);
	}

	public boolean remove(Material material) {
		return materialOrder.remove(material);
	}

	@Override
	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return materialOrder.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public Material findMaterial(UUID materialId) {
		var filter = getComplexMaterials().stream().filter(m -> m.getId().equals(materialId)).findFirst();
		if (filter.isPresent()) {
			return filter.orElseThrow();
		}
		return materialOrder.stream().filter(m -> m.getId().equals(materialId)).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		return relevance;
	}

	@Override
	public int materialCount() {
		return materialOrder.size();
	}

	/**
	 * Assigns the materials to the task. Only materials that match the Topic will be assigned.
	 *
	 * @param materials the materials to assign
	 * @return the materials that were assigned
	 */
	@Override
	public Set<Material> assignMaterial(Set<Material> materials) {
		Set<Material> back = new HashSet<>();
		for (var material : materials) {
			if (isAssignable(material)) {
				materialOrder.add(material);
				back.add(material);
			}
		}
		return back;
	}

	/**
	 * Assigns the material to the part.
	 *
	 * @param material the material to assign
	 * @return true if a material was assigned, false if not
	 */
	@Override
	public boolean assign(Material material) {
		if (materialOrder.contains(material)) return false;
		return materialOrder.add(material);
	}

	/**
	 * Assigns the material from a {@link MaterialAssigner} to the part.
	 *
	 * @param assigner the assigner to use
	 * @return true if a material was assigned, false if not
	 */
	@Override
	public boolean assignMaterial(MaterialAssigner assigner) {
		return assigner.assign(this);
	}

	private boolean isAssignable(Material material) {
		if (material instanceof ItemMaterial)
			return false;
		return getAlias().stream().anyMatch(alias -> alias.contains(material.getStructureId()))
			   || getAlias().stream().anyMatch(alias -> alias.contains(material.getStructureId().split("-")[0].trim()));
	}

	@Override
	public boolean remove(UUID partId) {
		if (materialOrder.removeIf(m -> m.getId().equals(partId))) {
			return true;
		}
		return getComplexMaterials().removeIf(m -> m.getId().equals(partId));
	}

	@Override
	public Collection<NameAndId> collectionsNameAndId() {
		var back = new LinkedList<NameAndId>();
		back.add(new NameAndId(getName(), getId()));
		materialOrder.forEach(m -> back.add(new NameAndId(m.getName(), m.getId())));
		return back;
	}

	@Override
	public Iterator<MaterialOrderPart> iterator() {
		return materialOrder.stream().map(it -> (MaterialOrderPart) it).iterator();
	}

	@Override
	public void forEach(Consumer<? super MaterialOrderPart> action) {
		materialOrder.stream().map(it -> (MaterialOrderPart) it).forEach(action);
	}

	@Override
	public Spliterator<MaterialOrderPart> spliterator() {
		return materialOrder.stream().map(it -> (MaterialOrderPart) it).spliterator();
	}

	//region setter/getter
	@Override
	public Relevance getRelevance() {
		return relevance;
	}

	@Override
	public List<UUID> getCollectionIds() {
		var back = new LinkedList<UUID>();
		back.add(getId());
		materialOrder.forEach(m -> back.add(m.getId()));
		return back;
	}

	public void setRelevance(Relevance relevance) {
		this.relevance = relevance;
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return switch (relevance) {
			case OPTIONAL, INFORMATIONAL, IMPORTANT -> true;
			case MANDATORY -> !materialOrder.isEmpty();
			case TO_SET -> false;
			default -> false;
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	public List<Material> getMaterials() {
		return Collections.unmodifiableList(materialOrder);
	}
//endregion

	@Override
	public String toString() {
		return STR."TaskOrder{name='\{getName()}, id=\{getId()}\{'\''}, topic=\{getTopic()}}";
	}

}
