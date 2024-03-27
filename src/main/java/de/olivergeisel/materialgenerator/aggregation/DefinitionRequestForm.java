package de.olivergeisel.materialgenerator.aggregation;

import java.util.HashSet;
import java.util.Set;

public class DefinitionRequestForm extends AggregationConfigForm {

	private Set<String> terms = new HashSet<>();

	public DefinitionRequestForm() {
		super();
	}

	public DefinitionRequestForm(String connectionType, String modelName, String url, String apiKey, String fragment,
			int retries, int maxTokens, double temperature, double topP, Set<String> terms,
			String fragmentLanguage, String targetLanguage) {
		super(connectionType, modelName, url, apiKey, fragment, retries, maxTokens, temperature, topP,
				fragmentLanguage, targetLanguage);
		this.terms = terms;
	}

	//region setter/getter
	public Set<String> getTerms() {
		return terms;
	}

	public void setTerms(Set<String> terms) {
		this.terms = terms;
	}
//endregion
}
