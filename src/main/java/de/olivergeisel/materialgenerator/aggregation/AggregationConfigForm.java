package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.extraction.ModelParameters;


/**
 * General form to configure the aggregation process.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ModelParameters
 * @see AggregationController
 * @since 1.1.0
 */
public class AggregationConfigForm {


	private String connectionType;
	private String modelName;
	private String url;
	private String apiKey;
	private int    retries;
	private int    maxTokens;
	private double temperature;
	private double topP;
	private String fragment;
	private String targetLanguage;
	private String fragmentLanguage;

	public AggregationConfigForm() {
		this.fragment = "";
		this.connectionType = "remote";
		this.modelName = "";
		this.url = "";
		this.apiKey = "";
		this.retries = 1;
		this.maxTokens = 1800;
		this.temperature = .28;
		this.topP = .95;
		this.targetLanguage = "english";
		this.fragmentLanguage = "english";
	}

	public AggregationConfigForm(String connectionType, String modelName, String url, String apiKey, String fragment,
			int retries, int maxTokens, double temperature, double topP, String fragmentLanguage,
			String targetLanguage) {
		this.connectionType = connectionType;
		this.modelName = modelName;
		this.url = url;
		this.apiKey = apiKey;
		this.fragment = fragment;
		this.retries = retries;
		this.maxTokens = maxTokens;
		this.temperature = temperature;
		this.topP = topP;
		this.fragmentLanguage = fragmentLanguage;
		this.targetLanguage = targetLanguage;
	}

	//region setter/getter
	public String getFragmentLanguage() {
		return fragmentLanguage;
	}

	public void setFragmentLanguage(String fragmentLanguage) {
		this.fragmentLanguage = fragmentLanguage;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public ModelParameters getModelParameters() {
		return new ModelParameters(this.maxTokens, this.temperature, this.topP, this.retries);
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(int maxTokens) {
		this.maxTokens = maxTokens;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTopP() {
		return topP;
	}

	public void setTopP(double topP) {
		this.topP = topP;
	}
//endregion

}
