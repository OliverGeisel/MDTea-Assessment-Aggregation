package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;

public class GeneratorInput {
	private TemplateSet    templates;
	private KnowledgeModel model;
	private CoursePlan     plan;


	public GeneratorInput(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templates = templates;
		this.model = model;
		this.plan = plan;
	}

	//region setter/getter
	public CoursePlan getPlan() {
		return plan;
	}

	public void setPlan(CoursePlan plan) {
		this.plan = plan;
	}

	public KnowledgeModel getModel() {
		return model;
	}

	public void setModel(KnowledgeModel model) {
		this.model = model;
	}

	public TemplateSet getTemplates() {
		return templates;
	}

	public void setTemplates(TemplateSet templates) {
		this.templates = templates;
	}
//endregion
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GeneratorInput that)) return false;

		if (!templates.equals(that.templates)) return false;
		if (!model.equals(that.model)) return false;
		return plan.equals(that.plan);
	}

	@Override
	public int hashCode() {
		int result = templates.hashCode();
		result = 31 * result + model.hashCode();
		result = 31 * result + plan.hashCode();
		return result;
	}
}
