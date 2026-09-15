package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import org.apache.tomcat.util.json.JSONParser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * This class represents a prompt for extracting terms from a given fragment.
 *
 * @author Oliver Geisel
 * @version 1.2.0
 * @see Term
 * @see ElementPrompt
 * @since 1.1.0
 */
public class TermPrompt extends ElementPrompt<Term> {

/*
prompts to tests

- Separate each term by new line and every term start with a \"+\".
			- If you know a translation for the term then add it in brackets after the term. Otherwise leave it out.
			- If you find multiple translations, separate them with a \";\".
			- The terms should be in german, the translations in english.





 */

	private static final String DEFAULT_INSTRUCTION = """   
			program:
			- List all terms in the following query.
			- Terms are subjects in the query.
			- Terms are single words in singular.
			- Terms are a single nouns.
			- Terms start with a capital letter.
			- Follow the pattern:+ [Term] | ([Translation])
			- in example section you can see an example.
			
			example:
			- language: 'The language of the query is english; the language of the answer is english'
			- query: 'A fish is an animal that lives in the water. A fish use gills to breathe.'
			+ Fish | Fisch
			+ Animal | Tier
			+ Water | Wasser
			+ Gill | Kieme
			""";

	private static final String DEFAULT_FORMAT = "+ [Term] | ([Translation])\\n";

	/**
	 * Creates a new TermPrompt with default instruction and format.
	 *
	 * @param fragment the fragment to extract terms from
	 */
	public TermPrompt(String fragment) {
		this(fragment, DEFAULT_INSTRUCTION, DEFAULT_FORMAT, DeliverType.MULTIPLE);
	}

	/**
	 * Creates a new TermPrompt.
	 *
	 * @param fragment the fragment to extract terms from
	 * @param queryLanguage the language of the query
	 * @param targetLanguage the language of the target
	 */
	public TermPrompt(String fragment, String queryLanguage, String targetLanguage) {
		super(DEFAULT_INSTRUCTION, DEFAULT_FORMAT, fragment, DeliverType.MULTIPLE, queryLanguage, targetLanguage);
	}

	/**
	 * Creates a new TermPrompt.
	 *
	 * @param fragment the fragment to extract terms from
	 * @param instruction the instruction for the model
	 * @param format the format of the answer
	 * @param deliverType the delivery type of the answer. Single or multiple answers
	 */
	public TermPrompt(String fragment, String instruction, String format, DeliverType deliverType) {
		super(instruction, format, fragment, deliverType);
	}


	public static TermPrompt fromDisk(String userInput) throws RuntimeException {
		// load the prompt from disk and return a new instance of TermPrompt
		try {
			InputStream inputStream = new FileInputStream(Path.of("/prompts/term.json").toFile());
			JSONParser parser = new JSONParser(inputStream);
			var value = (Map<String, Object>) parser.parse();
			String instructions = (String) value.get("instructions");
			String format = (String) value.get("format");
			String example = (String) value.get("example-input");
			return new TermPrompt(userInput, instructions, format, DeliverType.MULTIPLE);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load prompt from disk");
		}
	}

	//region setter/getter
	@Override
	public String getPrompt() {
		return STR."""
		\{getLanguageString()}

		\{getInstruction()}

		query:
		\{getFragment()}
		""";
	}
//endregion
}
