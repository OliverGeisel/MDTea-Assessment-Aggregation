package de.olivergeisel.materialgenerator.core.courseplan.content;


/**
 * Lernbereich eines Faches (z.B. Deutsch) im Lehrplan.
 * <p>
 * Ein Lernbereich ist ein Teil des Lehrplans eines Faches. Er beschreibt einen Teil des Faches, der in einer bestimmten Zeit
 */
public class LearningArea {
	// Deutsch Lernbereich
	private final int        time;
	private final String     description;
	private       Curriculum curriculum;

	public LearningArea(int time, String description, Curriculum curriculum) {
		this.time = time;
		this.description = description;
		this.curriculum = curriculum;
	}


}
