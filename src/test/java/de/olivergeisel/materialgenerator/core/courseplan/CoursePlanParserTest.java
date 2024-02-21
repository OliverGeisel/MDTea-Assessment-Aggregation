package de.olivergeisel.materialgenerator.core.courseplan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
class CoursePlanParserTest {

	private CoursePlanParser coursePlanParser;

	@BeforeEach
	void setUp() {
		coursePlanParser = new CoursePlanParser();
	}

	private CoursePlan getParsedInResource(String filename) {
		var file = new File(STR."src/test/resources/\{filename}");
		CoursePlan plan;
		try {
			plan = coursePlanParser.parseFromFile(file);
		} catch (FileNotFoundException e) {
			fail(e);
			return null; // unreachable
		}
		return plan;
	}

	@Test
	void getTargetsOkay() {
		var plan = getParsedInResource("testplan.json");
		var targets = coursePlanParser.getTargets();
		assertEquals(2, targets.size());
	}

	@Test
	void parseFromFileWithInputStream() {
		var inputStream = getClass().getClassLoader().getResourceAsStream("testplan.json");
		var coursePlan = coursePlanParser.parseFromFile(inputStream);
		assertNotNull(coursePlan);
		assertEquals("Testplan", coursePlan.getMetadata().getName().orElseThrow());
	}

	@Test
	void ParseFromFileWithFile() {
		var plan = getParsedInResource("testplan.json");
		assertNotNull(plan, "No plan was parsed");
		assertEquals("Testplan", plan.getMetadata().getName().orElseThrow());
		assertEquals(1, plan.getGoals().size());
		assertEquals(1, plan.getStructure().getOrder().size());
	}

	@Test
	void parseFromFileCheckMeta() {
		var plan = getParsedInResource("testplan.json");
		assertNotNull(plan, "No plan was parsed");
		var meta = plan.getMetadata();
		assertEquals("Testplan", meta.getName().orElseThrow());
		assertEquals("Das ist ein Testkurs für Debugging und Tests", meta.getDescription().orElseThrow());
		assertEquals("2024", meta.getYear().orElseThrow());
		assertEquals("Universität", meta.getType().orElseThrow());
		assertEquals("Anfang", meta.getLevel().orElseThrow());
		var extra = meta.getOtherInfos();
		assertEquals(2, extra.size());
		var extra1 = extra.get("Studentenzahl");
		assertEquals("15", extra1);
	}

	@Test
	void parseFromFileInvalidFile() {
		assertThrows(FileNotFoundException.class, () -> coursePlanParser.parseFromFile(new File("invalid")));
	}

	@Test
	void parseFromFileInvalidPlan() {
		assertThrows(IllegalStateException.class, () -> getParsedInResource("invalid.json"),
				"if the chapter has no groups the relevance is not set");
	}
}