package de.olivergeisel.materialgenerator.core.courseplan.content;

public class Curriculum {

	private String       name;
	private String       description;
	private MainFocusSet focusSet;

	public Curriculum(String name, String description, MainFocusSet focusSet) {
		this.name = name;
		this.description = description;
		this.focusSet = focusSet;
	}

//region setter/getter

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MainFocusSet getFocusSet() {
		return focusSet;
	}

	public void setFocusSet(MainFocusSet focusSet) {
		this.focusSet = focusSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion

}
