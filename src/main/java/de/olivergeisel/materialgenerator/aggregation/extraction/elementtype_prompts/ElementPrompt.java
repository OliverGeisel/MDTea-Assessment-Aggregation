package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;

/**
 * Represents a Prompt for a specific Element-Type.
 * <p>
 *
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @since 1.1.0
 */
public abstract class ElementPrompt<T extends KnowledgeElement> {

	private String      instruction;
	private String      wantedFormat;
	private String      fragment;
	private DeliverType deliverType;

	protected ElementPrompt(String instruction, String wantedFormat, String fragment, DeliverType type) {
		if (instruction == null || wantedFormat == null || fragment == null || type == null) {
			throw new NullPointerException("No argument can be null");
		}
		this.instruction = instruction;
		this.wantedFormat = wantedFormat;
		this.fragment = fragment;
		this.deliverType = type;
	}

	//region setter/getter
	public abstract String getPrompt();

	public DeliverType getDeliverType() {
		return deliverType;
	}

	protected void setDeliverType(DeliverType deliverType) {
		this.deliverType = deliverType;
	}

	public String getWantedFormat() {
		return wantedFormat;
	}

	protected void setWantedFormat(String wantedFormat) {
		this.wantedFormat = wantedFormat;
	}

	public String getInstruction() {
		return instruction;
	}

	protected void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getFragment() {
		return fragment;
	}

	protected void setFragment(String fragment) {
		this.fragment = fragment;
	}
//endregion


}
