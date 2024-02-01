package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Definition;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Example;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Term;
import de.olivergeisel.materialgenerator.generation.generator.transfer_assamble.TransferAssembler;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.List;


/**
 * Summarizes a specific {@link Term}. It's contains {@link Definition}s, {@link ListMaterial}, {@link Example}s and
 * {@link TextMaterial}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ComplexMaterial
 * @see TransferAssembler
 * @since 1.1.0
 */
@Entity
public class SummaryMaterial extends ComplexMaterial {

	private String topic;

	protected SummaryMaterial() {
		super(MaterialType.WIKI);
	}


	public SummaryMaterial(Term topic) {
		super(MaterialType.WIKI);
		this.topic = topic.getContent();
	}

	public boolean addDefinition(Material material) {
		if (!material.getTemplateInfo().getTemplateType().equals(TemplateType.DEFINITION)) {
			return false;
		}
		return append(material);
	}

	public boolean addExample(ExampleMaterial material) {
		return append(material);
	}

	public boolean addList(ListMaterial material) {
		return append(material);
	}

	public boolean addText(TextMaterial material) {
		return append(material);
	}

	public boolean removeDefinition(Material material) {
		if (!material.getTemplateInfo().getTemplateType().equals(TemplateType.DEFINITION)) {
			return false;
		}
		return remove(material);
	}

	public boolean removeExample(ExampleMaterial material) {
		return remove(material);
	}

	public boolean removeList(ListMaterial material) {
		return remove(material);
	}

	public boolean removeText(TextMaterial material) {
		return remove(material);
	}

	//region setter/getter
	public List<Material> getDefinitions() {
		return getParts().stream().filter(it -> it.getTemplateInfo().getTemplateType().equals(TemplateType.DEFINITION))
						 .toList();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public List<ExampleMaterial> getExamples() {
		return getParts().stream().filter(it -> it.getType().equals(MaterialType.EXAMPLE))
						 .map(it -> (ExampleMaterial) it).toList();
	}

	public List<ListMaterial> getLists() {
		return getParts().stream().filter(it -> it.getTemplateInfo().getTemplateType().equals(TemplateType.LIST))
						 .map(it -> (ListMaterial) it).toList();
	}

	public List<TextMaterial> getTexts() {
		return getParts().stream().filter(it -> it.getTemplateInfo().getTemplateType().equals(TemplateType.TEXT))
						 .map(it -> (TextMaterial) it).toList();
	}
//endregion

}
