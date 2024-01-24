package de.olivergeisel.materialgenerator.aggregation.extraction;

import java.util.LinkedList;
import java.util.List;

public record PromptParameters(int retries, int maxTokens, double temperature, double topP) {

	public PromptParameters {
		if (retries < 1) {
			throw new IllegalArgumentException("retries must be greater than 0");
		}
		if (maxTokens < 1) {
			throw new IllegalArgumentException("maxTokens must be greater than 0");
		}
		if (temperature < 0.0 || temperature > 1.0) {
			throw new IllegalArgumentException("temperature must be greater than 0 and less than 1");
		}
		if (topP < 0.0 || topP > 1.0) {
			throw new IllegalArgumentException("topP must be greater than 0 and less than 1");
		}
	}

	public List<String> toList() {
		var back = new LinkedList<String>();
		if (retries > 1) {
			back.add("-r");
			back.add(Integer.toString(retries));
		}
		if (maxTokens > 0) {
			back.add("-m");
			back.add(Integer.toString(maxTokens));
		}
		if (temperature > 0.0) {
			back.add("-t");
			back.add(Double.toString(temperature));
		}
		if (topP > 0.0) {
			back.add("-p");
			back.add(Double.toString(topP));
		}
		return back;
	}

	@Override
	public String toString() {
		String maxTokens = this.maxTokens <= 0 ? "" : STR."-m \{Integer.toString(this.maxTokens)}";
		String temperature = this.temperature <= 0.0 ? "" : STR."-t \{Double.toString(this.temperature)}";
		String topP = this.topP <= 0.0 ? "" : STR."-p \{Double.toString(this.topP)}";
		String retries = this.retries <= 1 ? "" : STR."-r \{Integer.toString(this.retries)}";

		return STR."\{retries} \{maxTokens} \{temperature} \{topP}";
	}
}
