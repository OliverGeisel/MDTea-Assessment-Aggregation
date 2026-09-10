package de.olivergeisel.materialgenerator.core.courseplan.content;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class AliasList {

	@ElementCollection
	private List<String> aliases = new ArrayList<>();

	public AliasList() {
	}

	public AliasList(List<String> aliases) {
		this.aliases = aliases;
	}

	public void add(String alias) {
		aliases.add(alias);
	}

	public void remove (String alias) {
		aliases.remove(alias);
	}

	public void addAll(List<String> aliases) {
		this.aliases.addAll(aliases);
	}

	public List<String> getAliases() {
		return aliases;
	}
}