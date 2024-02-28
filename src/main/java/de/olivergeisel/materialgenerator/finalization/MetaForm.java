package de.olivergeisel.materialgenerator.finalization;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.Map;

@Getter
@Setter
public class MetaForm {

	@NotBlank
	private String              name;
	@NotBlank
	private String              description;
	@Range(min = 2000)
	private int                 year;
	@NotBlank
	private String              level;
	@NotBlank
	private String              type;
	private Map<String, String> extras;


	public MetaForm() {
	}

	public MetaForm(String name, String description, int year, String level, String type, Map<String, String> extras) {
		this.name = name;
		this.description = description;
		this.year = year;
		this.level = level;
		this.type = type;
		this.extras = extras;
	}


}
