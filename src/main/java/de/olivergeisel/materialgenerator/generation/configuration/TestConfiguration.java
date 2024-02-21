package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Configuration for a {@link TestMaterial}. The Configuration contains the configurationName, description, version, number of itemConfigurations,
 * the ordering of the itemConfigurations, the mark schema, the marks, the mark mapping percentage, the itemConfigurations and the level of the test.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TestMaterial
 * @see ItemConfiguration
 * @see ItemType
 * @see TestPer
 * @see ItemSorting
 * @since 1.1.0
 */
@Getter
@Embeddable
public class TestConfiguration {

	private String                  configurationName;
	private String                  description;
	private String                  version;
	private int                     numberTasks;
	private ItemSorting             ordering;
	private String                  markSchema;
	@ElementCollection
	private List<String>            marks;
	@ElementCollection
	private Map<String, Integer>    markMappingPercentage;
	@ElementCollection
	private List<ItemConfiguration> itemConfigurations;
	@ElementCollection
	private Set<TestPer>            level;

	public TestConfiguration(String configurationName, String description, String version, int numberTasks,
			ItemSorting ordering, String markSchema, List<String> marks, Map<String, Integer> markMappingPercentage,
			List<ItemConfiguration> itemConfigurations, Set<TestPer> level) {
		this.configurationName = configurationName;
		this.description = description;
		this.version = version;
		this.numberTasks = numberTasks;
		this.ordering = ordering;
		this.markSchema = markSchema;
		this.marks = new ArrayList<>(marks);
		this.markMappingPercentage = markMappingPercentage;
		this.itemConfigurations = new ArrayList<>(itemConfigurations);
		this.level = level;
	}

	public TestConfiguration() {

	}

	public boolean hasConfiguration(ItemType itemType) {
		return itemConfigurations.stream().anyMatch(task -> task.getForItemType().equals(itemType));
	}

	public ItemConfiguration getConfiguration(ItemType itemType) throws NoSuchElementException {
		return itemConfigurations.stream().filter(task -> task.getForItemType().equals(itemType)).findFirst()
								 .orElseThrow();
	}

	boolean hasTestPer(TestPer level) {
		return this.level.contains(level);
	}

	@Override
	public TestConfiguration clone() {
		var itemConfigurationsCopy =
				this.itemConfigurations.stream().map(ItemConfiguration::clone).collect(Collectors.toList());
		var levelCopy = new HashSet<>(this.level);
		var marksCopy = new ArrayList<>(this.marks);
		var markMappingPercentageCopy = new HashMap<>(this.markMappingPercentage);
		return new TestConfiguration(configurationName, description, version, numberTasks, ordering, markSchema,
				marksCopy,
				markMappingPercentageCopy, itemConfigurationsCopy, levelCopy);
	}
	public Set<ItemType> getItemTypes() {
		return itemConfigurations.stream().map(ItemConfiguration::getForItemType).collect(Collectors.toSet());
	}
	//region setter/getter
//endregion
	/**
	 * The sorting of the items in the test
	 */
	public enum ItemSorting {
		RANDOM,
		BY_COURSE_ORDER,
		BY_DIFFICULTY,
	}
}
