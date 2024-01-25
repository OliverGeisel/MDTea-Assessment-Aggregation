package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public record TestConfiguration(String name, String description, String version, int numberTasks,
								TaskOrdering ordering, String markSchema, List<String> marks,
								Map<String, Integer> markMappingPercentage, List<TaskConfiguration> tasks,
								Set<TestPer> level) {


	public boolean hasConfiguration(TaskType taskType) {
		return tasks.stream().anyMatch(task -> task.getForTaskType().equals(taskType));
	}

	public TaskConfiguration getConfiguration(String taskType) throws NoSuchElementException {
		return tasks.stream().filter(task -> task.getForTaskType().equals(taskType)).findFirst().orElseThrow();
	}

	boolean hasTestPer(TestPer level) {
		return this.level.contains(level);
	}

	//region setter/getter
	public Set<TaskType> getTaskTypes() {
		return tasks.stream().map(TaskConfiguration::getForTaskType).collect(Collectors.toSet());
	}
//endregion

	public enum TaskOrdering {
		RANDOM,
		BY_COURSE_ORDER,
		BY_DIFFICULTY,
	}
}
