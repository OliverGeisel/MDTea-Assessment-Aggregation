package de.olivergeisel.materialgenerator.aggregation;

public class AggregationConfigForm {
	public  String connectionType;
	public  String modelName;
	public  String url;
	public  String apiKey;
	public  int    retries;
	public  int    maxTokens;
	public  double temperature;
	public  double topP;
	private String fragment;
	public AggregationConfigForm() {
		this.fragment = "";
		this.connectionType = "remote";
		this.modelName = "";
		this.url = "";
		this.apiKey = "";
		this.retries = 1;
		this.maxTokens = 800;
		this.temperature = .28;
		this.topP = .95;
	}

	public AggregationConfigForm(String connectionType, String modelName, String url, String apiKey, String fragment,
			int retries,
			int maxTokens, double temperature, double topP) {
		this.connectionType = connectionType;
		this.modelName = modelName;
		this.url = url;
		this.apiKey = apiKey;
		this.fragment = fragment;
		this.retries = retries;
		this.maxTokens = maxTokens;
		this.temperature = temperature;
		this.topP = topP;
	}

	//region setter/getter
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
