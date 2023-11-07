package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class ExtraTemplate extends TemplateInfo {
	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("definition");
		allFields.add("relatedTerms");
		FIELDS = Collections.unmodifiableSet(allFields);
	}


	@ElementCollection
	@CollectionTable(name = "entity_map", joinColumns = @JoinColumn(name = "entity_id"))
	@MapKeyColumn(name = "key_column")
	@Column(name = "value_column")
	private Map<String, String> values = new HashMap<>();

	private String templateName;

	public ExtraTemplate(TemplateType type, String templateName) {
		super(type);
		this.templateName = templateName;
	}

	protected ExtraTemplate() {

	}

	public String getValue(String key) {
		return values.get(key);
	}

	public void setValue(String key, String value) {
		values.put(key, value);
	}

	public void removeField(String key) {
		values.remove(key);
	}

	//region setter/getter
	public Set<String> getKeys() {
		return values.keySet();
	}

	public String getTemplateName() {
		return templateName;
	}


//endregion
}
