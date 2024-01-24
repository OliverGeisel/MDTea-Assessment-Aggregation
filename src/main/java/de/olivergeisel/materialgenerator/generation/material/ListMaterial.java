package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ListMaterial extends Material {

	@ElementCollection
	private List<String> entries = new LinkedList<>();
	private String       headline;
	private boolean      numerated;

	public ListMaterial() {
		super(MaterialType.WIKI);
	}

	protected ListMaterial(MaterialType type, TemplateInfo templateInfo) {
		super(type);
		setTemplateInfo(templateInfo);
	}

	protected ListMaterial(MaterialType type, TemplateInfo templateInfo, String headline, Collection<String> entries,
			boolean numerated, KnowledgeElement element) {
		super(type, element);
		setTemplateInfo(templateInfo);
		this.entries.addAll(entries);
		this.headline = headline;
		this.numerated = numerated;
	}

	public ListMaterial(String headline, Collection<String> entries, boolean numerated) {
		super(MaterialType.WIKI);
		this.entries.addAll(entries);
		this.headline = headline;
		this.numerated = numerated;
	}

	public ListMaterial(String headline, Collection<String> entries) {
		this(headline, entries, false);
	}

	public ListMaterial(String headline) {
		this(headline, List.of());
	}

	//region setter/getter
	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public boolean isNumerated() {
		return numerated;
	}

	public void setNumerated(boolean numerated) {
		this.numerated = numerated;
	}
//endregion


}
