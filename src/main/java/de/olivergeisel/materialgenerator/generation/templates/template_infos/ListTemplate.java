package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.util.*;

@Entity
public class ListTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("headline");
		allFields.add("entries");
		allFields.add("numerated");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	@ElementCollection
	private final List<String> entries = new LinkedList<>();
	private       String       headline;

	public ListTemplate() {
		super(TemplateType.LIST);
	}

	protected ListTemplate(TemplateType type) {
		super(type);
	}

	protected ListTemplate(TemplateType type, UUID mainTermId) {
		super(type, mainTermId);
	}

	public ListTemplate(UUID mainTermId, String headline, String... elements) {
		super(TemplateType.LIST, mainTermId);
		Collections.addAll(entries, elements);
		this.headline = headline;
	}

	public ListTemplate(UUID mainTermId, String headline, Collection<String> elements) {
		super(TemplateType.LIST, mainTermId);
		entries.addAll(elements);
		this.headline = headline;
	}

	public ListTemplate(UUID mainTermId, String headline) {
		super(TemplateType.LIST, mainTermId);
		this.headline = headline;
	}

	//region setter/getter
	public List<String> getEntries() {
		return entries;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}
//endregion

}
