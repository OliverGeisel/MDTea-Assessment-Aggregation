package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.List;

@Entity
public class WikiPageMaterial extends ComplexMaterial {

	@ManyToOne
	private Material       definition;
	@ManyToOne
	private Material       synonym;
	@ManyToOne
	private Material       acronym;
	@ManyToMany
	private List<Material> texts;
	@ManyToOne
	private Material       example;

	public WikiPageMaterial(List<Material> parts) {
		super(MaterialType.WIKI, new TemplateType("WIKI"), parts);
	}

	protected WikiPageMaterial() {
		super(MaterialType.WIKI, new TemplateType("WIKI"));
	}
}
