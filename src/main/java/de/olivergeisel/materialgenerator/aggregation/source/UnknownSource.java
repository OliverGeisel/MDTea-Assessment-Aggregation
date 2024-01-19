package de.olivergeisel.materialgenerator.aggregation.source;

public class UnknownSource extends NotResolvableReference {
	private static UnknownSource instance;

	private UnknownSource() {
		super("unknown", "UnknownSource");
	}

	//region setter/getter
	public static UnknownSource getInstance() {
		if (instance == null) {
			instance = new UnknownSource();
		}
		return instance;
	}

	@Override
	public void setContent(String content) {
		// do nothing
	}
//endregion

}
