package de.olivergeisel.materialgenerator.aggregation.extraction;


public record ModelParameters(int maxTokens, double temperature, double topP, int retries) {

	public ModelParameters() {
		this(1800, .28, .95, 1);
	}
}