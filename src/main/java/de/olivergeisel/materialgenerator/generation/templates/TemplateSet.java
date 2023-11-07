package de.olivergeisel.materialgenerator.generation.templates;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.*;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class TemplateSet {
	@Transient
	private final BasicTemplates     basicTemplates = new BasicTemplates();
	@OneToMany
	private final Set<ExtraTemplate> extraTemplates = new HashSet<>();
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private       UUID               id;
	private       String             name;

	public TemplateSet() {
	}

	public TemplateSet(String name) {
		this.name = name;
	}

	public boolean addAllTemplates(TemplateInfo[] templates) {
		return true;//extraTemplates.addAll(templates);
	}

	public boolean addTemplate(ExtraTemplate template) {
		return extraTemplates.add(template);
	}

	public boolean removeTemplate(TemplateInfo template) {
		return true; //extraTemplates.remove(template);
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
	public Set<ExtraTemplate> getExtraTemplates() {
		return Collections.unmodifiableSet(extraTemplates);
	}

	public TextTemplate getTextTemplate() {
		return basicTemplates.getTextTemplate();
	}

	public void setTextTemplate(TextTemplate textTemplate) {
		basicTemplates.setTextTemplate(textTemplate);
	}

	public ExampleTemplate getExampleTemplate() {
		return basicTemplates.getExampleTemplate();
	}

	public void setExampleTemplate(ExampleTemplate exampleTemplate) {
		basicTemplates.setExampleTemplate(exampleTemplate);
	}

	public DefinitionTemplate getDefinitionTemplate() {
		return basicTemplates.getDefinitionTemplate();
	}

	public void setDefinitionTemplate(DefinitionTemplate definitionTemplate) {
		this.basicTemplates.setDefinitionTemplate(definitionTemplate);
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

	public void setListTemplate(ListTemplate listTemplate) {
		basicTemplates.setListTemplate(listTemplate);
	}

	public void setSynonymTemplate(SynonymTemplate synonymTemplate) {
		basicTemplates.setSynonymTemplate(synonymTemplate);
	}

	public void setAcronymTemplate(AcronymTemplate acronymTemplate) {
		basicTemplates.setAcronymTemplate(acronymTemplate);
	}
//endregion

}
