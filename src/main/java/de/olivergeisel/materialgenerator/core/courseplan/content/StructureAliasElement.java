package de.olivergeisel.materialgenerator.core.courseplan.content;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
public class StructureAliasElement {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(nullable = false)
	private UUID id;

	/**
	 * The name (key) of the structure.
	 */
	private String structureName;

	/**
	 * The list of aliases for the structure.
	 */
	@ElementCollection
	private List<String> aliases = new ArrayList<>();

	public StructureAliasElement() {
	}

	public StructureAliasElement(String structureName, List<String> aliases) {
		this.structureName = structureName;
		this.aliases = aliases;
	}

}
