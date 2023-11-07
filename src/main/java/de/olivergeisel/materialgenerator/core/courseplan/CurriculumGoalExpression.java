package de.olivergeisel.materialgenerator.core.courseplan;

public enum CurriculumGoalExpression {

	FIRST_LOOK(1, "Einblicke in ein Thema gewinnen"),
	KNOW(2, "Wissen wiedergeben"),
	TRANSLATE(3, "Wissen anwenden"),
	CONTROL(4, "Wissen kontrollieren"),
	USE(5, "Wissen anwenden"),
	COMMENT(6, "Wissen bewerten und kommentieren"),
	CREATE(7, "Wissen erweitern und neues Wissen schaffen");

	private final int    level;
	private final String description;

	private CurriculumGoalExpression(int level, String description) {
		this.level = level;
		this.description = description;
	}

//region setter/getter

	public String getDescription() {
		return description;
	}

	public int getLevel() {
		return level;
	}
//endregion


}
