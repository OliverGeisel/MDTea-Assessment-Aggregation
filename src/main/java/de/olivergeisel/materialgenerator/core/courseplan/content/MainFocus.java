package de.olivergeisel.materialgenerator.core.courseplan.content;

public class MainFocus {
	// Deutsch Schwerpunkt

	private Comment     comment;
	private ContentGoal goals;
	private FocusType   type;

	public MainFocus(Comment comment, ContentGoal goals, FocusType type) {
		this.comment = comment;
		this.goals = goals;
		this.type = type;
	}

	//region setter/getter
	public ContentGoal getGoals() {
		return goals;
	}
//endregion

	private enum FocusType {

		DEFINITION,
		THEOREM,
		EXAMPLE,
		EXERCISE,
		RESSOURCE

	}
}


