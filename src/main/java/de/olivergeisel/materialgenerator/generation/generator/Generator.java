package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;

/**
 * A Generator is a class that generates materials for a given curriculum.
 */
public interface Generator {

	/**
	 * Initial method to set the input for the generator. All parameters cant be null.
	 *
	 * @param templates Templates that should be used for the generation.
	 * @param model     The current KnowledgeModel.
	 * @param plan      Plan with curriculum for which the generator should generate materials.
	 */
	void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan);

	/**
	 * Initial method to set the input for the generator. This needs only the GeneratorInput object.
	 *
	 * @param input All input to be needed for the generator. Must be valid and not be null.
	 */
	void input(GeneratorInput input);

	/**
	 * Method to start the generation process. This method should be called after the input method.
	 *
	 * @return True if the generation was successful, false if not.
	 */
	boolean update();

	/**
	 * Method to get the output of the generation process. This method should be called after the update method.
	 *
	 * @return all Materials for the given input.
	 */
	GeneratorOutput output();


//region setter/getter

	/**
	 * Method to check if the generator is ready to start the generation process. This method should be called after
	 * the input method and before the update method.
	 *
	 * @return True if all inputs are valid and not null, false if not.
	 */
	boolean isReady();
//endregion


}


