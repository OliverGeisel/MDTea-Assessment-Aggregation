package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * A Configuration for a {@link TestMaterial}. The Configuration contains the name, description, version, number of tasks,
 * the ordering of the tasks, the mark schema, the marks, the mark mapping percentage, the tasks and the level of the test.
 *
 * @param name
 * @param description
 * @param version
 * @param numberTasks
 * @param ordering
 * @param markSchema
 * @param marks
 * @param markMappingPercentage
 * @param tasks
 * @param level
 *
 * @see TestMaterial
 * @since 1.1.0
 * @version 1.1.0
 * @see ItemConfiguration
 * @see ItemType
 * @see TestPer
 * @see ItemSorting
 * @author Oliver Geisel
 */
@Embeddable
public record TestConfiguration(String name, String description, String version, int numberTasks,
								ItemSorting ordering, String markSchema, @ElementCollection List<String> marks,
								Map<String, Integer> markMappingPercentage,
								@ElementCollection List<ItemConfiguration> tasks,
								@ElementCollection Set<TestPer> level) {


	public boolean hasConfiguration(ItemType itemType) {
		return tasks.stream().anyMatch(task -> task.getForItemType().equals(itemType));
	}

	public ItemConfiguration getConfiguration(ItemType itemType) throws NoSuchElementException {
		return tasks.stream().filter(task -> task.getForItemType().equals(itemType)).findFirst().orElseThrow();
	}

	boolean hasTestPer(TestPer level) {
		return this.level.contains(level);
	}

	//region setter/getter
	public Set<ItemType> getItemTypes() {
		return tasks.stream().map(ItemConfiguration::getForItemType).collect(Collectors.toSet());
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
