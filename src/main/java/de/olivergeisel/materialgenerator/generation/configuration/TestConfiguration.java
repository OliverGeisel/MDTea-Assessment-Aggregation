package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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

	String      configurationName;
	String      description;
	String      version;
	int         numberTasks;
	ItemSorting ordering;
	String      markSchema;
	@ElementCollection
	List<String>            marks;
	@ElementCollection
	Map<String, Integer>    markMappingPercentage;
	@ElementCollection
	List<ItemConfiguration> itemConfigurations;
	@ElementCollection
	Set<TestPer>            level;

	public TestConfiguration(String configurationName, String description, String version, int numberTasks,
			ItemSorting ordering, String markSchema, List<String> marks, Map<String, Integer> markMappingPercentage,
			List<ItemConfiguration> itemConfigurations, Set<TestPer> level) {
		this.configurationName = configurationName;
		this.description = description;
		this.version = version;
		this.numberTasks = numberTasks;
		this.ordering = ordering;
		this.markSchema = markSchema;
		this.marks = marks;
		this.markMappingPercentage = markMappingPercentage;
		this.itemConfigurations = itemConfigurations;
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

	//region setter/getter
	public Set<ItemType> getItemTypes() {
		return itemConfigurations.stream().map(ItemConfiguration::getForItemType).collect(Collectors.toSet());
	}
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
