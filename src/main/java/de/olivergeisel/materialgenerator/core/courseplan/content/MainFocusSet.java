package de.olivergeisel.materialgenerator.core.courseplan.content;

import java.util.*;

public class MainFocusSet {

	private List<MainFocus> focuses;


	//region setter/getter
	public List<MainFocus> getFocuses() {
		return Collections.unmodifiableList(focuses);
	}

	//
//
	public Map<MainFocus, ContentGoal> getFocusGoalMapping() {
		Map<MainFocus, ContentGoal> back = new HashMap<>();
		for (MainFocus focus : focuses) {
			back.put(focus, focus.getGoals());
		}
		return back;
	}

	public Set<ContentGoal> getGoals() {
		Set<ContentGoal> back = new HashSet<>();
		for (MainFocus focus : focuses) {
			back.add(focus.getGoals());
		}
		return Collections.unmodifiableSet(back);
	}
//endregion
}
