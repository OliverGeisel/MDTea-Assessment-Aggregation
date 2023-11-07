package de.olivergeisel.materialgenerator.core.course;

import java.util.Map;
import java.util.Optional;

/**
 * A Meta is a collection of meta information about a {@link Course}.
 * <p>
 * A Meta contains at least a name, a description, a level, a type and a year.
 * Other meta information can be added.
 */
public abstract class Meta {
//region setter/getter
	public abstract Optional<String> getName();

	public abstract Optional<String> getDescription();

	public abstract Optional<String> getLevel();

	public abstract Optional<String> getType();

	public abstract Optional<String> getYear();

	public abstract Map<String, String> getOtherInfos();
//endregion

}
