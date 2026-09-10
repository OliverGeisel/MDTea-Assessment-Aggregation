package de.olivergeisel.materialgenerator.core.courseplan.content;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElement;
import jakarta.persistence.*;

import java.util.*;

/**
 * A mapping of aliases to structures. Every {@link StructureElement} can have multiple aliases. And every
 * {@link ContentTarget} can be related to multiple {@link StructureElement}s. Since this system works topic wise,
 * you need this mapping to assign the materials to the correct {@link StructureElement}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see StructureElement
 * @see ContentTarget
 * @see CoursePlan
 * @since 1.1.0
 */
@Embeddable
@Deprecated(since = "1.2.0", forRemoval = true)
public class TopicStructureAliasMappings {

	/**
	 * The mapping of aliases to structures. The key is the structure, the value is a list of aliases.
	 */
	@CollectionTable(name = "topic_structure_alias_mappings", joinColumns = @JoinColumn(name = "topic_id"))
	@ElementCollection
	private Map<String, AliasList> aliasMappings = new HashMap<>();

	public TopicStructureAliasMappings() {
	}

	public TopicStructureAliasMappings(Map<String, AliasList> aliasMappings) {
		this.aliasMappings = aliasMappings;
	}

	public AliasList getAliasesFor(String structure) {
		return aliasMappings.get(structure);
	}

	/**
	 * Adds a new alias for the given structure.
	 *
	 * @param structure the structure to add the alias to
	 * @param alias     the alias to add
	 */
	public void addAlias(String structure, String alias) {
		if (structure == null || alias == null) {
			return;
		}
		aliasMappings.computeIfAbsent(structure, k -> new AliasList()).add(alias);
	}

	public void addAliases(String structure, List<String> aliases) {
		if (structure == null || aliases == null) {
			return;
		}
		aliasMappings.computeIfAbsent(structure, k -> new AliasList()).addAll(aliases);
	}

	public void removeAlias(String structure, String alias) {
		aliasMappings.get(structure).remove(alias);
	}

	public void removeStructure(String structure) {
		aliasMappings.remove(structure);
	}

	public void clear() {
		aliasMappings.clear();
	}

	/**
	 * Returns a list of all aliases. The list is a flat list of all aliases for all structures.
	 *
	 * @return a list of all aliases
	 */
	public List<String> complete() {
		return aliasMappings.values().stream().flatMap(it-> it.getAliases().stream())
							.toList();
	}

	public boolean containsStructure(String structure) {
		return aliasMappings.containsKey(structure);
	}

	public Set<Map.Entry<String, AliasList>> entrySet() {
		return aliasMappings.entrySet();
	}

	public Set<String> keySet() {
		return aliasMappings.keySet();
	}

	public Collection<AliasList> values() {return aliasMappings.values();
	}

	//region setter/getter
	public boolean isEmpty() {
		return aliasMappings.isEmpty();
	}
//endregion
}
