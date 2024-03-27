package de.olivergeisel.materialgenerator.finalization;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MetaForm {

	@NotBlank
	private String                          name;
	@NotBlank
	private String                          description;
	@Range(min = 2000)
	private int                             year;
	@NotBlank
	private String                          level;
	@NotBlank
	private String                          type;
	private Map<List<String>, List<String>> extras;


	public MetaForm() {
	}

	public MetaForm(Map map) {
		this.name = (String) map.get("name");
		this.description = (String) map.get("description");
		this.year = Integer.parseInt(map.get("year").toString());
		this.level = (String) map.get("level");
		this.type = (String) map.get("type");
		this.extras = (Map<List<String>, List<String>>) map.get("extras");
	}

	public MetaForm(String name, String description, int year, String level, String type,
			Map<List<String>, List<String>> extras) {
		this.name = name;
		this.description = description;
		this.year = year;
		this.level = level;
		this.type = type;
		this.extras = extras;
	}


}
