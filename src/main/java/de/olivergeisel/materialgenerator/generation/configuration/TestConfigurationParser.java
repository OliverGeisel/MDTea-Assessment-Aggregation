package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.generation.material.assessment.TaskType;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

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
			var jsonName = rootObject.get("name");
			var name = !(jsonName instanceof String nameString) ? "" : nameString;
			var jsonDescription = rootObject.get("description");
			var description = !(jsonDescription instanceof String descriptionString) ? "" : descriptionString;
			try {
				var levels = (List<String>) rootObject.get("level");
				concreteLevels = getLevel(levels);
			} catch (ClassCastException e) {
				logger.warn("testPer ist not a list. aborting.");
				throw new IllegalArgumentException("testPer ist not a list. aborting.");
			}
			var jsonVersion = rootObject.get("version");
			var version = !(jsonVersion instanceof String versionString) ? "" : versionString;

			var jsonTasks = (List<Map<String, ?>>) rootObject.get("tasks");
			if (jsonTasks == null) {
				throw new MissingConfigurationPartException("No tasks specified");
			}
			var taskConfigs = parseTaskConfigurations(jsonTasks);
			var numberOfTasks = (int) rootObject.get("numberTasks");
			var jsonOrdering = rootObject.get("ordering");
			var ordering = TestConfiguration.TaskOrdering.valueOf(jsonOrdering.toString().trim());
			var jsonMarkSchema = rootObject.get("markSchema").toString();
			var marks = (List<String>) rootObject.get("marks");
			var markMappingPercentage = (Map<String, Integer>) rootObject.get("markMappingPercentage");
			return new TestConfiguration(name, description, version, numberOfTasks, ordering, jsonMarkSchema, marks,
					markMappingPercentage, taskConfigs, concreteLevels);
		}
		throw new IllegalArgumentException("root element must be a abject");
	}

	private static List<TaskConfiguration> parseTaskConfigurations(List<Map<String, ?>> jsonTasks)
			throws IllegalArgumentException {
		List<TaskConfiguration> back = new LinkedList<>();
		for (var jsonTask : jsonTasks) {
			var jsonTaskType = (String) jsonTask.get("type");
			var taskType = Arrays.stream(TaskType.values()).toList().stream()
								 .filter(it -> it.name().equals(jsonTaskType)).findFirst()
								 .orElseThrow();
			var jsonTestParameters = (Map<String, ? extends Number>) jsonTask.get("testParameters");

			var testParameters = parseTestParameters(jsonTestParameters);
			TaskConfiguration config = switch (taskType) {
				case TRUE_FALSE -> new TrueFalseConfiguration(testParameters);

				case SINGLE_CHOICE -> {
					var numberOfChoices = (int) jsonTask.get("numberOfChoices");
					yield new SingleChoiceConfiguration(numberOfChoices, testParameters);
				}
				case MULTIPLE_CHOICE -> {
					var numberOfChoices = (int) jsonTask.get("numberOfChoices");
					var numberOfCorrectChoices = (int) jsonTask.get("numberOfCorrectChoices");
					yield new MultipleChoiceConfiguration(numberOfChoices, numberOfCorrectChoices, testParameters);
				}
				case FILL_OUT_BLANKS -> {
					var blanks = FillOutBlanksConfiguration.FillOutBlanksType.valueOf(
							jsonTask.get("blankType").toString().trim());
					var numberOfBlanks = (int) jsonTask.get("numberOfBlanks");
					yield new FillOutBlanksConfiguration(numberOfBlanks, blanks, testParameters);
				}
				case ASSIGNMENT -> new AssignmentConfiguration(testParameters);
				case UNDEFINED -> throw new IllegalArgumentException("TaskType is undefined");
			};
			back.add(config);
		}
		return back;
	}

	private static TestParameters parseTestParameters(Map<String, ? extends Number> jsonTestParameters) {
		if (jsonTestParameters == null) {
			return new TestParameters();
		}
		var tries = (int) jsonTestParameters.get("tries");
		var points = (int) jsonTestParameters.get("pointsPerUnit");
		var negativeFactor = (double) jsonTestParameters.get("negativeFactor");
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
				case "MATERIAL" -> TestPer.MATERIAL;
				case "SUB_GROUP" -> TestPer.SUB_GROUP;
				case "GROUP" -> TestPer.GROUP;
				case "CHAPTER" -> TestPer.CHAPTER;
				default -> throw new IllegalArgumentException("Unknown level: " + level);
			};
			back.add(parsed);
		}
		return back;
	}
}
