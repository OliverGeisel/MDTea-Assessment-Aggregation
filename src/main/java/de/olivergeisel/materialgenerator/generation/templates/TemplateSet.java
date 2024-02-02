package de.olivergeisel.materialgenerator.generation.templates;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class TemplateSet {
	@ElementCollection
	private final Set<TemplateType> extraTemplates = new HashSet<>();
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private       UUID              id;
	private       String            name;

	public TemplateSet() {
	}

	public TemplateSet(String name) {
		this.name = name;
	}

	public boolean addAllTemplates(TemplateType[] templates) {
		return true;//extraTemplates.addAll(templates);
	}

	public boolean addTemplate(TemplateType template) {
		return extraTemplates.add(template);
	}


	public boolean supportsTemplate(TemplateType type) {
		var basics = TemplateType.class.getDeclaredFields();
		for (var field : basics) {
			try {
				if (field.get(null).equals(type)) return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false; //todo extraTemplates.stream().anyMatch(it -> it.getTemplateType().equals(type));
	}

	//region setter/getter
	public Set<TemplateType> getExtraTemplates() {
		return Collections.unmodifiableSet(extraTemplates);
	}


	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//endregion

}
