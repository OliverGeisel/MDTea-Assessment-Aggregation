package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import jakarta.persistence.*;

import java.util.*;


/**
 * A Class holding all ids of {@link KnowledgeElement} to a specific {@link Material} that was created with these
 * elements.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @see KnowledgeElement
 * @since 0.2.0
 */
@Entity
public class MaterialMappingEntry<M extends Material> {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();


	@ElementCollection
	private Set<String> relatedElements = new HashSet<>();
	@OneToOne(cascade = CascadeType.ALL, targetEntity = Material.class)
	private M           material;

	protected MaterialMappingEntry() {

	}

	public <CM extends M> MaterialMappingEntry(CM material) {
		this.material = material;
	}

	public <CM extends M> MaterialMappingEntry(CM material, KnowledgeElement... elements) {
		this.material = material;
		add(elements);
	}

	public <CM extends M> MaterialMappingEntry(CM material, String[] elementIds) {
		this.material = material;
		relatedElements.addAll(Arrays.asList(elementIds));
	}

	public <CM extends M> MaterialMappingEntry(CM material, Collection<KnowledgeElement> elements) {
		this.material = material;
		add(elements.toArray(new KnowledgeElement[0]));
	}

	public boolean add(KnowledgeElement... elements) {
		var newIds = Arrays.stream(elements).map(KnowledgeElement::getId).toArray(String[]::new);
		return Collections.addAll(relatedElements, newIds);
	}

	public boolean remove(KnowledgeElement... elements) {
		var newIds = Arrays.stream(elements).map(KnowledgeElement::getId).toArray(String[]::new);
		for (var elem : newIds) {
			relatedElements.remove(elem);
		}
		return true;
	}

	public void addAll(KnowledgeElement[] array) {
		for (var elem : array) {
			add(elem);
		}
	}

	public void addAll(Collection<String> elements) {
		relatedElements.addAll(elements);
	}

	//region setter/getter
	public Set<String> getRelatedElements() {
		return relatedElements;
	}

	public void setRelatedElements(Collection<String> relatedElements) {
		this.relatedElements.clear();
		this.relatedElements.addAll(relatedElements);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public M getMaterial() {
		return material;
	}

	public void setMaterial(M material) {
		this.material = material;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MaterialMappingEntry that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return STR."MaterialMappingEntry{relatedElements=\{relatedElements}, material=\{material}}";
	}
}
