package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class MaterialMappingEntry {
	@ElementCollection
	private final Set<String> relatedElements = new HashSet<>();
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private       UUID        id              = UUID.randomUUID();
	@OneToOne(cascade = CascadeType.ALL)
	private       Material    material;

	public MaterialMappingEntry(Material material, KnowledgeElement... elements) {
		this.material = material;
		add(elements);
	}

	public MaterialMappingEntry(Material material, Collection<KnowledgeElement> elements) {
		this.material = material;
		add(elements.toArray(new KnowledgeElement[0]));
	}

	public MaterialMappingEntry() {

	}

	public MaterialMappingEntry(Material material) {
		this.material = material;
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

	//region setter/getter
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Material getMaterial() {
		return material;
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
		return "MaterialMappingEntry{" +
			   "relatedElements=" + relatedElements +
			   ", material=" + material +
			   '}';
	}
}
