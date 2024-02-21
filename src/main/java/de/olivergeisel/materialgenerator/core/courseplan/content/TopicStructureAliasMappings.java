package de.olivergeisel.materialgenerator.core.courseplan.content;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElement;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

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
public class TopicStructureAliasMappings {

	@ElementCollection
	private Map<String, List<String>> aliasMappings = new HashMap<>();

	public TopicStructureAliasMappings() {
	}

	public TopicStructureAliasMappings(Map<String, List<String>> aliasMappings) {
		this.aliasMappings = aliasMappings;
	}

	public List<String> getAliasesFor(String structure) {
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
		aliasMappings.computeIfAbsent(structure, k -> new LinkedList<>()).add(alias);
	}

	public void addAliases(String structure, List<String> aliases) {
		if (structure == null || aliases == null) {
			return;
		}
		aliasMappings.computeIfAbsent(structure, k -> new LinkedList<>()).addAll(aliases);
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
	 * Returns a list of all aliases.
	 *
	 * @return a list of all aliases
	 */
	public List<String> complete() {
		return aliasMappings.values().stream().flatMap(Collection::stream)
							.toList();
	}

	public boolean containsStructure(String structure) {
		return aliasMappings.containsKey(structure);
	}

	public Set<Map.Entry<String, List<String>>> entrySet() {
		return aliasMappings.entrySet();
	}

	public Set<String> keySet() {
		return aliasMappings.keySet();
	}

	public Collection<List<String>> values() {
		return aliasMappings.values();
	}

	//region setter/getter
	public boolean isEmpty() {
		return aliasMappings.isEmpty();
	}
//endregion
}
