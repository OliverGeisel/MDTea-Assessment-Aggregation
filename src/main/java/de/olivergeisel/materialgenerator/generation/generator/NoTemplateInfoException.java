package de.olivergeisel.materialgenerator.generation.generator;

public class NoTemplateInfoException extends RuntimeException {
	public NoTemplateInfoException(String noDefinitionTemplateFound) {
		super(noDefinitionTemplateFound);
	}

	public NoTemplateInfoException(String noDefinitionTemplateFound, Throwable cause) {
		super(noDefinitionTemplateFound, cause);
	}

	public NoTemplateInfoException(Throwable cause) {
		super(cause);
	}
}
