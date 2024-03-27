package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.*;

/**
 * A part of the {@link CourseStructure}. Use the Composite Pattern to build a {@link CourseStructure}.
 * This class is the component role of the Composite Pattern.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see CourseStructure
 * @see StructureChapter
 * @see StructureElementPart
 * @since 0.2.0
 */
public abstract class StructureElement {

	private final List<String>  alias = new ArrayList<>(); // KnowledgeObject ids
	protected     Relevance     relevance;
	private       ContentTarget topic;
	private       String        name; // KnowledgeObject id; most important alias

	protected StructureElement() {
		relevance = Relevance.TO_SET;
	}

	protected StructureElement(ContentTarget topic, Relevance relevance, String name,
			Collection<String> alternatives) {
		this.relevance = relevance;
		this.name = name;
		this.topic = topic;
		if (alternatives != null) {
			alias.addAll(alternatives);
		}
	}

	/**
	 * Adds an alternative to the list of alternatives
	 *
	 * @param alternative alternative to add
	 * @return @{@literal true} if the alternative was added, {@literal false} if it was already in the list
	 */
	public boolean addAlias(String alternative) {
		return alias.add(alternative);
	}

	/**
	 * Removes an alternative from the list of alternatives
	 *
	 * @param alternative alternative to remove
	 * @return @{@literal true} if the alternative was removed, {@literal false} if it was not in the list
	 */
	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	/**
	 * Moves an alternative up in the list of alternatives
	 *
	 * @param alternative alternative to move
	 * @return @{@literal true} if the alternative was moved, {@literal false} if it was not in the list or already at the top
	 */
	public boolean moveUpAlias(String alternative) {
		int index = alias.indexOf(alternative);
		if (index > 0) {
			alias.remove(index);
			alias.add(index - 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Moves an alternative down in the list of alternatives
	 *
	 * @param alternative alternative to move
	 * @return @{@literal true} if the alternative was moved, {@literal false} if it was not in the list or already at the bottom
	 */
	public boolean moveDownAlias(String alternative) {
		int index = alias.indexOf(alternative);
		if (index < alias.size() - 1) {
			alias.remove(index);
			alias.add(index + 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Moves an alternative to a specific index in the list of alternatives
	 *
	 * @param alternative alternative to move
	 * @param newIndex    index to move the alternative to
	 * @return @{@literal true} if the alternative was moved, {@literal false} if it was not in the list or the index was out of bounds
	 */
	public boolean moveAlias(String alternative, int newIndex) {
		int index = alias.indexOf(alternative);
		if (index >= 0 && index < alias.size() && newIndex >= 0 && newIndex < alias.size()) {
			alias.remove(index);
			alias.add(newIndex, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Inserts an alternative at a specific index in the list of alternatives
	 *
	 * @param alternative alternative to insert
	 * @param index       index to insert the alternative at
	 * @return @{@literal true} if the alternative was inserted, {@literal false} if the index was out of bounds
	 */
	public boolean insertAlias(String alternative, int index) {
		if (index >= 0 && index < alias.size()) {
			alias.add(index, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Finds an alternative in the list of alternatives or the name of the element.
	 *
	 * @param alias alternative to find
	 * @return the Element that contains the alias, {@literal null} if it was not found
	 */
	public StructureElement findByAlias(String alias) {
		if (name == null) return null;
		if (name.equals(alias) || hasAlias(alias)) return this;
		return null;
	}

	/**
	 * Checks if an alternative is in the list of alternatives
	 *
	 * @param alternative alternative to check
	 * @return @{@literal true} if the alternative is in the list, {@literal false} if it is not
	 */
	public boolean hasAlias(String alternative) {
		return alias.contains(alternative);
	}

	/**
	 * Updates the relevance of the structure element. This method should be called after the structure element was changed.
	 */
	public abstract Relevance updateRelevance();

	//region setter/getter
	public List<String> getAlias() {
		return alias;
	}

	public Set<String> getAlternatives() {
		return Collections.unmodifiableSet(new LinkedHashSet<>(alias));
	}

	public String getName() {
		return name;
	}

	public Relevance getRelevance() {
		return relevance;
	}

	public ContentTarget getTopic() {
		return topic;
	}

	public void setTopic(ContentTarget topic) {
		this.topic = topic;
	}

	public boolean isValid() {
		return relevance != Relevance.TO_SET;
	}
//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StructureElement that)) return false;

		if (!alias.equals(that.alias)) return false;
		if (relevance != that.relevance) return false;
		if (!Objects.equals(topic, that.topic)) return false;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		int result = alias.hashCode();
		result = 31 * result + (relevance != null ? relevance.hashCode() : 0);
		result = 31 * result + (topic != null ? topic.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return STR."StructureElement{name='\{name}', alias=\{alias}, relevance=\{relevance}, topic=\{topic}}";
	}

}
