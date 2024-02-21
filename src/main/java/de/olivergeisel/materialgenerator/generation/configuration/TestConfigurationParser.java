package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

/**
 * Parses a test configuration from a json file
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TestConfiguration
 * @since 1.1.0
 */
public class TestConfigurationParser {

	private static final Set<TestPer> DEFAULT_LEVELS = Set.of(TestPer.CHAPTER, TestPer.GROUP);

	private static final Logger logger = LoggerFactory.getLogger(TestConfigurationParser.class);

	public TestConfigurationParser(InputStream jsonStream) throws ParseException {
		var parser = new JSONParser(jsonStream);
		var parsedObject = parser.parse();

	}

	public static TestConfiguration parse(String jsonString) throws ParseException, IllegalArgumentException {
		if (jsonString == null) {
			throw new IllegalArgumentException("jsonString must not be null");
		}
		var parser = new JSONParser(jsonString);
		var parsedObject = parser.parse();
		return parseTestConfiguration(parsedObject);
	}

	private static TestConfiguration parseTestConfiguration(Object parsedObject)
			throws MissingConfigurationPartException {
		if (parsedObject instanceof Map<?, ?> jsonMap) {
			var rootObject = (Map<String, ?>) jsonMap;
			Set<TestPer> concreteLevels;
			var jsonName = rootObject.get("configurationName");
			var name = !(jsonName instanceof String nameString) ? "" : nameString;
			var jsonDescription = rootObject.get("description");
			var description = !(jsonDescription instanceof String descriptionString) ? "" : descriptionString;
			try {
				var levels = (List<String>) rootObject.get("testPer");
				concreteLevels = getLevel(levels);
			} catch (ClassCastException e) {
				logger.warn("testPer ist not a list. aborting.");
				throw new IllegalArgumentException("testPer ist not a list. aborting.");
			}
			var jsonVersion = rootObject.get("version");
			var version = !(jsonVersion instanceof String versionString) ? "" : versionString;

			var jsonTasks = (List<Map<String, ?>>) rootObject.get("items");
			if (jsonTasks == null) {
				throw new MissingConfigurationPartException("No itemConfigurations specified");
			}
			var taskConfigs = parseTaskConfigurations(jsonTasks);
			var numberOfTasks = Integer.parseInt(rootObject.get("numberItems").toString());
			var jsonOrdering =
					rootObject.get("sorting") == null ? TestConfiguration.ItemSorting.RANDOM : rootObject.get(
							"sorting");
			var ordering = TestConfiguration.ItemSorting.valueOf(jsonOrdering.toString().trim());
			var jsonMarkSchema = rootObject.get("markSchema").toString();
			var marks = (List<String>) rootObject.get("marks");
			var markMappingPercentage = (Map<String, BigInteger>) rootObject.get("markMappingPercentage");
			HashMap<String, Integer> fixedMapping = new HashMap<>();
			for (var mark : markMappingPercentage.entrySet()) {
				int fixed = Math.min(Math.max(0, mark.getValue().intValue()), 100);
				fixedMapping.put(mark.getKey(), fixed);
			}
			return new TestConfiguration(name, description, version, numberOfTasks, ordering, jsonMarkSchema, marks,
					fixedMapping, taskConfigs, concreteLevels);
		}
		throw new IllegalArgumentException("root element must be a abject");
	}

	private static List<ItemConfiguration> parseTaskConfigurations(List<Map<String, ?>> jsonItems)
			throws IllegalArgumentException {
		List<ItemConfiguration> back = new LinkedList<>();
		for (var jsonItem : jsonItems) {
			var jsonTaskType = (String) jsonItem.get("type");
			var taskType = Arrays.stream(ItemType.values())
								 .filter(it -> it.name().replace("_", "")
												 .equalsIgnoreCase(jsonTaskType.replace("_", ""))).findFirst()
								 .orElse(ItemType.UNDEFINED);
			var jsonTestParameters = (Map<String, ? extends Number>) jsonItem.get("testParameters");

			var testParameters = parseTestParameters(jsonTestParameters);
			ItemConfiguration config = switch (taskType) {
				case TRUE_FALSE -> new TrueFalseConfiguration(testParameters);

				case SINGLE_CHOICE -> {
					var numberOfChoices = Integer.parseInt(jsonItem.get("numberOfChoices").toString());
					yield new SingleChoiceConfiguration(numberOfChoices, testParameters);
				}
				case MULTIPLE_CHOICE -> {
					var numberOfChoices = Integer.parseInt(jsonItem.get("numberOfChoices").toString());
					var numberOfCorrectChoices = Integer.parseInt(jsonItem.get("numberOfCorrectChoices").toString());
					yield new MultipleChoiceConfiguration(numberOfChoices, numberOfCorrectChoices, testParameters);
				}
				case FILL_OUT_BLANKS -> {
					var blanks = FillOutBlanksConfiguration.FillOutBlanksType.valueOf(
							jsonItem.get("blankType").toString().trim());
					var numberOfBlanks = Integer.parseInt(jsonItem.get("numberOfBlanks").toString());
					yield new FillOutBlanksConfiguration(numberOfBlanks, blanks, testParameters);
				}
				case ASSIGNMENT -> new AssignmentConfiguration(testParameters);
				case UNDEFINED -> throw new IllegalArgumentException("ItemType is undefined");
			};
			back.add(config);
		}
		return back;
	}

	private static TestParameters parseTestParameters(Map<String, ? extends Number> jsonTestParameters) {
		if (jsonTestParameters == null) {
			return new TestParameters();
		}
		var tries = Integer.parseInt(jsonTestParameters.get("tries").toString());
		var points = Integer.parseInt(jsonTestParameters.get("pointsPerUnit").toString());
		var negativeFactor = Double.parseDouble(jsonTestParameters.get("negativeFactor").toString());
		return new TestParameters(tries, points, negativeFactor);
	}

	private static Set<TestPer> getLevel(List<String> levels) throws IllegalArgumentException {
		var back = new HashSet<TestPer>();
		if (levels == null || levels.isEmpty()) {
			logger.warn("No level specified. Using default: Chapter and Group");
			return DEFAULT_LEVELS;
		}
		for (var level : levels) {
			var parsed = switch (level.toUpperCase().replace("-", "_").trim()) {
				case "ALL" -> TestPer.ALL;
				case "TASK" -> TestPer.TASK;
				case "SUB_GROUP" -> TestPer.SUB_GROUP;
				case "GROUP" -> TestPer.GROUP;
				case "CHAPTER" -> TestPer.CHAPTER;
				default -> throw new IllegalArgumentException(STR."Unknown level: \{level}");
			};
			back.add(parsed);
		}
		return back;
	}

	/**
	 * Parses a test configuration from a file
	 *
	 * @param jsonStream the input stream of the file
	 * @return the parsed test configuration
	 * @throws ParseException if the file could not be parsed
	 */
	public static TestConfiguration parseFromFile(InputStream jsonStream)
			throws ParseException, IllegalArgumentException {
		var parser = new JSONParser(jsonStream);
		var parsedObject = parser.parse();
		return parseTestConfiguration(parsedObject);
	}

//region setter/getter

	/**
	 * Returns the default configuration in this project
	 *
	 * @return the default configuration
	 * @throws ParseException if the default configuration could not be parsed
	 */
	public static TestConfiguration getDefaultConfiguration() throws ParseException {
		InputStream reader = TestConfigurationParser.class.getClassLoader()
														  .getResourceAsStream(
																  "data/config/BasicTestConfiguration.json");
		return parseFromFile(reader);
	}
//endregion
}
