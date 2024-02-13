package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.*;

/**
 * A MaterialOrderCollection is a {@link MaterialOrderPart} that contains a list of {@link MaterialOrderPart}s.
 * <p>
 * A MaterialOrderCollection can contain other MaterialOrderCollections.
 * A MaterialOrderCollection can contain itself.
 * Every MaterialOrderCollection has a {@link Topic}. There is a list of aliases for the topic.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see MaterialOrderPart
 * @since 0.2.0
 */
@Entity
public abstract class MaterialOrderCollection extends MaterialOrderPart implements Iterable<MaterialOrderPart> {

	@ElementCollection
	private final List<String> alias = new ArrayList<>(); // KnowledgeObject (Structure) ids

	@ManyToOne
	private Topic topic;

	protected MaterialOrderCollection() {
		super();
	}

	protected MaterialOrderCollection(String name, Topic topic) {
		super(name);
		this.topic = topic;
	}

	protected MaterialOrderCollection(String name, Topic topic, Set<String> alias) {
		super(name);
		this.topic = topic;
		this.alias.addAll(alias);
	}

	public abstract Material findMaterial(UUID materialId);

	public abstract Relevance updateRelevance();

	public abstract int materialCount();

	/**
	 * Assign a set of Materials to this part. Can throw {@link UnsupportedOperationException} when no MAterial can
	 * assigned direct to part.
	 *
	 * @param materials Materials to assign
	 * @return materials that are assigned to the part
	 */
	public abstract Set<Material> assignMaterial(Set<Material> materials);

	/**
	 * Assign a single Material to this part. Can throw {@link UnsupportedOperationException} when this operation is
	 * not supported.
	 *
	 * @param material Material to assign
	 * @return true if material is assigned, false otherwise
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	public abstract boolean assign(Material material) throws UnsupportedOperationException;

	/**
	 * Assign {@link Material} in a {@link MaterialAssigner} to this part. Can throw
	 * {@link UnsupportedOperationException} when this
	 * operation is not supported.
	 *
	 * @param assigner MaterialAssigner to assign material
	 * @return true if at least one material is assigned, false otherwise
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	public abstract boolean assignMaterial(MaterialAssigner assigner);

	/**
	 * Add an alternative name for this part at the end of the list. This alternative is the least important
	 * alternative. If the alternative is already in the list, it will not be added again.
	 *
	 * @param alternative new alternative. Must not be null or empty
	 * @return true if alternative is added, false otherwise
	 */
	public boolean appendAlias(String alternative) {
		if (alternative == null || alternative.isBlank())
			return false;
		if (alias.contains(alternative))
			return false;
		return alias.add(alternative);
	}

	/**
	 * Remove an alternative name for this part. If the alternative is not in the list, nothing happens.
	 *
	 * @param alternative alternative to remove
	 * @return true if alternative is removed, false otherwise
	 */
	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	/**
	 * Move an alternative name up in the list. If the alternative is not in the list or already at the top, nothing
	 * happens.
	 *
	 * @param alternative alternative to move up
	 * @return true if alternative is moved, false otherwise
	 */
	public boolean moveUp(String alternative) {
		int index = alias.indexOf(alternative);
		if (index == -1) {
			return false;
		}
		if (index > 0) {
			alias.remove(index);
			alias.add(index - 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Moves the given alias one position down. If the alias is not found or is already at the bottom, nothing is done.
	 *
	 * @param alternative alias to move (must not be null or empty)
	 * @return true if the alias was moved, false otherwise
	 */
	public boolean moveDown(String alternative) {
		int index = alias.indexOf(alternative);
		if (index == -1) {
			return false;
		}
		if (index < alias.size() - 1) {
			alias.remove(index);
			alias.add(index + 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Moves the given alias to the given index. If the alias is not found or the index is out of bounds, nothing is
	 *
	 * @param alternative alias to move (must not be null or empty)
	 * @param newIndex    index to move the alias to
	 * @return true if the alias was moved, false otherwise
	 */
	public boolean move(String alternative, int newIndex) {
		int index = alias.indexOf(alternative);
		if (index < 0 || index >= alias.size() || newIndex < 0 || newIndex >= alias.size())
			return false;
		alias.remove(index);
		alias.add(newIndex, alternative);
		return true;
	}

	/**
	 * Inserts a new alias at the given index. If the index is out of bounds, the alias is appended.
	 *
	 * @param alternative alias to insert (must not be null or empty)
	 * @param newIndex    index to insert the alias at
	 * @return true if the alias was inserted, false otherwise
	 */
	public boolean insert(String alternative, int newIndex) {
		if (newIndex < 0 || alternative == null || alternative.isBlank() || alias.contains(alternative))
			return false;
		if (newIndex >= alias.size()) {
			appendAlias(alternative);
			return true;
		}
		alias.add(newIndex, alternative);
		return true;
	}

	/**
	 * Returns a list of all aliases of this part. The order of the list say what is the most important alias.
	 *
	 * @param alternatives list of alternatives that should be checked. If null or empty, an empty list is returned.
	 * @return list of matching aliases. Empty list if no alias matches.
	 */
	public List<String> getMatchingAlias(Set<String> alternatives) {
		List<String> back = new ArrayList<>();
		if (alternatives == null || alternatives.isEmpty()) {
			return back;
		}
		for (var aliasToCheck : alias) {
			if (alternatives.contains(aliasToCheck))
				back.add(aliasToCheck);
		}
		return back;
	}

	/**
	 * Returns the index of the first matching alias. If no alias matches, -1 is returned.
	 *
	 * @param alternatives list of alternatives that should be checked. If null or empty, -1 is returned.
	 * @return index of first matching alias. -1 if no alias matches.
	 */
	public AliasPosition getFirstMatchingAlias(Set<String> alternatives) {
		if (alternatives == null || alternatives.isEmpty()) {
			return new AliasPosition(-1, "");
		}
		for (int i = 0; i < this.alias.size(); i++) {
			var aliasToCheck = this.alias.get(i);
			if (alternatives.contains(aliasToCheck))
				return new AliasPosition(i, aliasToCheck);
		}
		return new AliasPosition(-1, "");
	}

	/**
	 * Returns the index of the first matching alias. If no alias matches, -1 is returned.
	 *
	 * @param alternatives list of alternatives that should be checked. If null or empty, -1 is returned.
	 * @return index of first matching alias. -1 if no alias matches.
	 * @see #getFirstMatchingAlias(Set)
	 */
	public int getFirstMatchingAliasIndex(Set<String> alternatives) {
		return getFirstMatchingAlias(alternatives).position();
	}

	/**
	 * Returns the first matching alias. If no alias matches, an empty string is returned.
	 *
	 * @param alternatives list of alternatives that should be checked. If null or empty, an empty string is returned.
	 * @return first matching alias. Empty string if no alias matches.
	 * @see #getFirstMatchingAlias(Set)
	 */
	public String getFirstMatchingAliasName(Set<String> alternatives) {
		return getFirstMatchingAlias(alternatives).alias();
	}

	public abstract boolean remove(UUID partId);


	//region setter/getter
	public abstract Relevance getRelevance();

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * Returns a list of all aliases of this part. The order of the list say what is the most important alias
	 * aliases in the course
	 * The list is unmodifiable.
	 *
	 * @return set of aliases
	 */
	public Set<String> getAlias() {
		return Collections.unmodifiableSet(new LinkedHashSet<>(alias));
	}
//endregion

	public record AliasPosition(int position, String alias) {
		//region setter/getter
		public boolean isEmpty() {
			return position == -1 || alias.isBlank();
		}
//endregion
	}
}
