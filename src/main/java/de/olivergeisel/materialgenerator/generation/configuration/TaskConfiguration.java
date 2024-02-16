package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.persistence.Embeddable;

/**
 * A Configuration for a {@link Task}-Material. the TaskType say which {@link Task} is used.
 * The {@link TestParameters} are optional and can be used to configure the material for a {@link TestMaterial}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Task
 * @see Material
 * @since 1.1.0
 */
@Embeddable
public abstract class TaskConfiguration {

	private TaskType       forTaskType;
	private TestParameters testParameters = TestParameters.DEFAULT;

	protected TaskConfiguration(TaskType forTaskType) {
		this(forTaskType, null);
	}

	protected TaskConfiguration(TaskType taskType, TestParameters testParameters) {
		this.forTaskType = taskType;
		this.testParameters = testParameters == null ? TestParameters.DEFAULT : testParameters;
	}

	protected TaskConfiguration() {

	}

	//region setter/getter
	public TestParameters getTestParameters() {
		return testParameters;
	}

	public TaskType getForTaskType() {
		return forTaskType;
	}
//endregion


}
