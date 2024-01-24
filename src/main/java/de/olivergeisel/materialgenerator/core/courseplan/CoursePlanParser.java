package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;


/**
 * A JSON Parser for CoursePlans. The JSON structure is as follows:
 * <p>
 * A CoursePlan is a JSON Object with three attributes: meta, content and structure.
 * <p>
 * The meta attribute is a JSON Object with the following attributes:
 *     <ul>
 *         <li>name: the name of the course plan</li>
 *         <li>year: the year of the course plan</li>
 *         <li>level: the level of the course plan</li>
 *         <li>type: the type of the course plan</li>
 *         <li>description: the description of the course plan</li>
 *         <li>extra: a JSON Object with additional information</li>
 *     </ul>
 *     The content attribute is a JSON Array with JSON Objects. Each JSON Object represents a goal.
 *     A goal has the following attributes:
 *     <ul>
 *         <li>target: the target of the goal</li>
 *         <li>expression: the expression of the goal</li>
 *         <li>completeSentence: the complete sentence of the goal</li>
 *         <li>content: a JSON Array with JSON Objects. Each JSON Object represents a target</li>
 *     </ul>
 *     The structure attribute is a JSON Array with JSON Objects. Each JSON Object represents a chapter.
 *     Every chapter has the following attributes:
 *     <ul>
 *         <li>name: the name of the chapter</li>
 *         <li>topic: a main focus - related to the content</li>
 *         <li>alternatives: a JSON Array with JSON Objects. Each JSON Object represents a alternative to the topic of the chapter</li>
 *         <li>groups: a JSON Array with JSON Objects. Each JSON Object represents a group</li>
 *    </ul>
 * <p>
 * A group has the similar attributes as a chapter except the tasks attribute.
 * This is a JSON Array with JSON Objects. Each JSON Object represents a task.<br>
 * A task is similar to a group but has no further subparts.
 *
 * <p>
 * The parser will parse the JSON and create a CoursePlan object from it.
 */
public class CoursePlanParser {

	private static final String PLAN_META      = "meta";
	private static final String PLAN_CONTENT   = "content";
	private static final String PLAN_STRUCTURE = "structure";

	private static final String   STRUCTURE_NAME  = "name";
	// ----- META -----
	private static final String[] META_ATTRIBUTES = {"name", "year", "level", "type", "description"};

	// ----- CONTENT -----
	private static final String GOAL_TARGET            = "target";
	private static final String GOAL_EXPRESSION        = "expression";
	private static final String GOAL_COMPLETE_SENTENCE = "completeSentence";
	private static final String GOAL_CONTENT           = "content";

	// ----- STRUCTURE -----
	private static final String STRUCTURE_TOPIC     = "topic";
	private static final String ALTERNATIVES        = "alternatives";
	private static final String GROUPS              = "groups";
	private static final String STRUCTURE_TASKS     = "tasks";
	private static final String STRUCTURE_RELEVANCE = "relevance";


	private final List<ContentTarget> targets = new ArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(CoursePlanParser.class);

	/**
	 * Parses Metadata of plan.
	 *
	 * @param mapping the mapping of the course plan
	 * @return the parsed Metadata or an empty MetataDataSet if the map is empty
	 */
	private CourseMetadata parseMetadata(Map<String, ?> mapping) {
		if (mapping == null) {
			return CourseMetadata.emptyMetadata();
		}
		String name = mapping.get("name").toString();
		String year = mapping.get("year").toString();
		String level = mapping.get("level").toString();
		String type = mapping.get("type").toString();
		String description = (String) mapping.get("description");
		var extraList = (List<Map<String, String>>) mapping.get("extra");
		var extra = new HashMap<String, String>();
		extraList.forEach(elem -> extra.put(elem.get("name"), elem.get("value")));
		return new CourseMetadata(name, year, level, type, description, extra);

	}

	private Set<KnowledgeObject> crateAliasObject(List<String> alternativesJSON) {
		var back = new HashSet<KnowledgeObject>();
		for (var elem : alternativesJSON) {
			back.add(new PotenzialKnowledgeObject(elem));
		}
		return back;
	}

	/**
	 * Creates an ordered set of aliases from a normal name and a list of alternatives.
	 *
	 * @param normalName       most important alias
	 * @param alternativesJSON list of alternatives
	 * @return ordered set of aliases
	 * @throws IllegalArgumentException if the normal name is null or empty
	 */
	private Set<String> crateAlias(String normalName, List<String> alternativesJSON) throws IllegalArgumentException {
		var back = new LinkedHashSet<String>();
		if (normalName == null || normalName.isBlank()) {
			throw new IllegalArgumentException("Normal name must not be null or blank");
		}
		back.add(normalName);
		if (alternativesJSON != null) {
			for (var elem : alternativesJSON) {
				if (elem != null && !elem.isBlank()) {
					back.add(elem);
				}
			}
		}
		return back;
	}

	/**
	 * Parses a chapter plan from a parsed JSON.
	 *
	 * @param chapterJSON the parsed JSON
	 * @return the parsed chapter plan
	 * @throws CoursePlanParserException if the JSON is not valid. Like the name is null or empty
	 * @throws IllegalStateException     if the parsed chapter is not valid
	 */
	private StructureChapter createChapter(Map<String, ?> chapterJSON)
			throws CoursePlanParserException, IllegalStateException {
		List<Map<String, ?>> groups = (List<Map<String, ?>>) chapterJSON.get(GROUPS);
		String name = chapterJSON.get(STRUCTURE_NAME).toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Chapter name must not be null or blank");
		}
		String weight = chapterJSON.get("weight").toString();
		var alternatives = crateAlias(name, (List<String>) chapterJSON.get(ALTERNATIVES));
		String topicName = chapterJSON.get(STRUCTURE_TOPIC) != null ? chapterJSON.get(STRUCTURE_TOPIC).toString() : "";
		var target = findTopic(topicName);
		var back = new StructureChapter(target, name, Double.parseDouble(weight), alternatives);
		for (var group : groups) {
			try {
				back.add(createGroup(group));
			} catch (CoursePlanParserException e) {
				logger.error("Error while parsing group in chapter %s".formatted(name), e);
			}
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Chapter %s is not valid", back.getName()));
		}
		return back;
	}

	private StructureElementPart createGroup(Map<String, ?> groupJSON) throws CoursePlanParserException {
		String name = groupJSON.get(STRUCTURE_NAME).toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Group name must not be null or blank");
		}
		String topicName = groupJSON.get(STRUCTURE_TOPIC) != null ? groupJSON.get(STRUCTURE_TOPIC).toString() : "";
		var topic = findTopic(topicName);
		var alternatives = crateAlias(name, ((List<String>) groupJSON.get(ALTERNATIVES)));
		var back = new StructureGroup(topic, Relevance.TO_SET, name, alternatives);
		List<Map<String, ?>> tasks = (List<Map<String, ?>>) groupJSON.get(STRUCTURE_TASKS);
		for (var task : tasks) {
			// Todo decide between group and task!
			try {
				back.add(createTask(task));
			} catch (CoursePlanParserException e) {
				logger.error(String.format("Error while parsing task in group %s. Skip task and continue", name), e);
			}
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Group %s is not valid!", back.getName()));
		}
		return back;
	}

	private StructureTask createTask(Map<String, ?> taskJSON) throws CoursePlanParserException {
		Relevance relevance = Relevance.valueOf(taskJSON.get(STRUCTURE_RELEVANCE).toString());
		String name = taskJSON.get(STRUCTURE_NAME).toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Name of task must not be null or blank");
		}
		String topicName = taskJSON.get(STRUCTURE_TOPIC) != null ? taskJSON.get(STRUCTURE_TOPIC).toString() : "";
		ContentTarget topic = findTopic(topicName);
		Set<String> alternatives;
		try {
			alternatives = crateAlias(name, (List<String>) taskJSON.get(ALTERNATIVES));
		} catch (IllegalArgumentException e) {
			logger.error("Could not parse task", e);
			throw new CoursePlanParserException(String.format("Could not parse task %s", name), e);
		}
		return new StructureTask(topic, relevance, name, alternatives);
	}

	private CourseStructure parseCourseStructure(List<Map<String, ?>> structure) {
		CourseStructure back = new CourseStructure();
		for (var chapterJson : structure) {
			try {
				StructureChapter newChapter = createChapter(chapterJson);
				back.add(newChapter);
			} catch (IllegalArgumentException e) {
				logger.error("Could not parse chapter", e);
			}
		}
		return back;
	}

	/**
	 * Gets relevant ContentTarget from the name;
	 *
	 * @param topic the name of the topic to find
	 * @return the ContentTarget or an empty ContentTarget if no target was found or the topic is blank
	 */
	private ContentTarget findTopic(String topic) {
		if (topic.isBlank()) {
			return ContentTarget.EMPTY;
		}
		return targets.stream().filter(target -> target.getTopic().equals(topic)).findFirst()
					  .orElseGet(() -> {
						  logger.warn("Could not find topic %s. Assign empty Topic.".formatted(topic));
						  return ContentTarget.EMPTY;
					  });
	}

	/**
	 * Parses the CurriculumGoals from the given mapping.
	 *
	 * @param goalsJSON the mapping with all goals to parse
	 * @return a list of all parsed goals
	 */
	private List<ContentGoal> parsePlanGoals(Map<String, ?> goalsJSON) {
		List<ContentGoal> back = new LinkedList<>();
		for (var entry : goalsJSON.entrySet()) {
			String goalName = entry.getKey();
			Map<String, ?> goalValues = (Map<String, ?>) entry.getValue();
			String expression = goalValues.get(GOAL_EXPRESSION).toString().toUpperCase().replace("-", "_");
			String masterKeyword = goalValues.get(GOAL_TARGET).toString();
			String completeSentence = goalValues.get(GOAL_COMPLETE_SENTENCE).toString();
			List<String> contentRaw =
					goalValues.get(GOAL_CONTENT) instanceof List<?> list ? (List<String>) list : List.of();
			var content = contentRaw.stream().map(ContentTarget::new).toList();
			var newGoal = new ContentGoal(ContentGoalExpression.valueOf(expression), masterKeyword,
					content, completeSentence, goalName);
			back.add(newGoal);
			targets.addAll(content);
		}
		return back;
	}


	/**
	 * Parses the CurriculumGoals from the given Input. The input must be a valid JSON.
	 *
	 * @param file the input
	 * @return a @see CoursePlan
	 * @throws CoursePlanParserException if the input is not valid
	 */
	public CoursePlan parseFromFile(InputStream file) throws CoursePlanParserException {
		CoursePlan back = null;
		var parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			logger.error("Could not parse JSON", e);
			throw new CoursePlanParserException("Could not parse JSON", e);
		}
		targets.clear();
		if (parsedObject instanceof HashMap<?, ?> parsedPlan) {
			Map<String, ?> metaJSON;
			Map<String, ?> contentJSON;
			Map<String, Map<String, ?>> goalsJSON = new HashMap<>();
			// META
			metaJSON = parsedPlan.get(PLAN_META) instanceof HashMap<?, ?> metaJsonTemp
					? (Map<String, ?>) metaJsonTemp : null;
			var meta = parseMetadata(metaJSON);
			// CONTENT
			contentJSON = (Map<String, ?>) parsedPlan.get(PLAN_CONTENT);
			var curriculumGoalsEntries = contentJSON.entrySet()
													.stream()
													.filter(entry -> Pattern.matches("goal-\\d+", entry.getKey()));
			curriculumGoalsEntries.forEach(entry ->
					goalsJSON.put(entry.getKey(), (Map<String, ?>) entry.getValue())
			);
			List<ContentGoal> goals = parsePlanGoals(goalsJSON);
			// STRUCTURE
			List<Map<String, ?>> courseStructure = (List<Map<String, ?>>) parsedPlan.get(PLAN_STRUCTURE);
			back = new CoursePlan(meta, goals, parseCourseStructure(courseStructure), targets);
		}
		return back;

	}

	/**
	 * Parses the CurriculumGoals from the given Input. The input must be a valid JSON.
	 *
	 * @param file the input
	 * @return a @see CoursePlan
	 * @throws FileNotFoundException     if the file was not found
	 * @throws CoursePlanParserException if the input is not valid
	 */
	public CoursePlan parseFromFile(File file) throws FileNotFoundException, CoursePlanParserException {
		FileInputStream input = new FileInputStream(file);
		return parseFromFile(input);
	}

	//region setter/getter
	public List<ContentTarget> getTargets() {
		return Collections.unmodifiableList(targets);
	}
//endregion
}
