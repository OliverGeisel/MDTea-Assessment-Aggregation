package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import java.util.List;

public enum Language {
	ENGLISH("english"),
	GERMAN("german");

	private final String languageText;

	Language(String language) {
		this.languageText = language;
	}

	static Language fromString(String language) {
		if (language == null) {
			return null;
		}
		for (Language l : Language.values()) {
			if (l.getLanguageText().equalsIgnoreCase(language)) {
				return l;
			}
		}
		return null;
	}

	//region setter/getter
	public static List<String> getLanguageStrings() {
		return List.of(ENGLISH.getLanguageText(), GERMAN.getLanguageText());
	}

	public String getLanguageText() {
		return languageText;
	}
//endregion
}