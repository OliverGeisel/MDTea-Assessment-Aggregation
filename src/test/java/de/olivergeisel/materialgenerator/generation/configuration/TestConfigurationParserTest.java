package de.olivergeisel.materialgenerator.generation.configuration;

import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
class TestConfigurationParserTest {

	private String minimalConfiguration(String testsPer) {
		return STR."""
			{
				"testPer": ["\{testsPer}"],
				"testParameters": {
					"tries": 1,
					"pointsPerUnit": 1,
					"negativeFactor": 0
				},
				"numberItems": 5,
				"markSchema": "NUMBERS",
				"marks": [1,2,3,4,5],
				"markMappingPercentage":{},
				"sorting": "RANDOM",
				"items": [
			      {
			        "type": "TrueFalse",
			        "testParameters": null
			      }
				]
			}
			""";
	}

	@Test
	void parseMinimalConfiguration() {
		var json = minimalConfiguration("TASK");
		TestConfiguration testConfiguration;
		try {
			testConfiguration = TestConfigurationParser.parse(json);
		} catch (ParseException e) {
			fail(e);
			return;
		}
		assertNotNull(testConfiguration);
		var numberOfItems = testConfiguration.getLevel();
		assertEquals(TestPer.TASK, numberOfItems.stream().findFirst().orElseThrow());
		var numberOfTasks = testConfiguration.getNumberTasks();
		assertEquals(5, numberOfTasks);
		var markSchema = testConfiguration.getMarkSchema();
		assertEquals("NUMBERS", markSchema);
		var order = testConfiguration.getOrdering();
		assertEquals(TestConfiguration.ItemSorting.RANDOM, order);
		var items = testConfiguration.getItemConfigurations();
		assertEquals(1, items.size());
		var version = testConfiguration.getVersion();
		assertEquals("", version);
		var description = testConfiguration.getDescription();
		assertEquals("", description);
		var configurationName = testConfiguration.getConfigurationName();
		assertEquals("", configurationName);
	}

	@Test
	void parseWithTestPer_isNotList() {
		var json = """
				{
					"testPer": "CHAPTER",
					"testParameters": {
						"tries": 1,
						"pointsPerUnit": 1,
						"negativeFactor": 0
					},
					"numberItems": 5,
					"markSchema": "NUMBERS",
					"sorting": "RANDOM",
					"items": [
				      {
				        "type": "TrueFalse",
				        "testParameters": null
				      },
					]
				}
				""";
		assertThrows(ParseException.class, () -> TestConfigurationParser.parse(json));

	}

	@Test
	void parseWithTestPer_isChapter() {
		var json = minimalConfiguration("CHAPTER");
		try {
			var testConfiguration = TestConfigurationParser.parse(json);
			assertTrue(testConfiguration.hasTestPer(TestPer.CHAPTER));
		} catch (ParseException e) {
			fail(e);
		}
	}

	@Test
	void parseWithTestPer_isAll() {
		var json = minimalConfiguration("ALL");
		try {
			var testConfiguration = TestConfigurationParser.parse(json);
			assertTrue(testConfiguration.hasTestPer(TestPer.ALL));
		} catch (ParseException e) {
			fail(e);
		}
	}

	@Test
	void parseWithTestPer_isTask() {
		var json = minimalConfiguration("TASK");
		try {
			var testConfiguration = TestConfigurationParser.parse(json);
			assertTrue(testConfiguration.hasTestPer(TestPer.TASK));
		} catch (ParseException e) {
			fail(e);
		}
	}

	@Test
	void parseWithTestPer_isGroup() {
		var json = minimalConfiguration("GROUP");
		try {
			var testConfiguration = TestConfigurationParser.parse(json);
			assertTrue(testConfiguration.hasTestPer(TestPer.GROUP));
		} catch (ParseException e) {
			fail(e);
		}
	}

	@Test
	void getDefaultConfiguration() {
		TestConfiguration testConfiguration;
		try {
			testConfiguration = TestConfigurationParser.getDefaultConfiguration();
		} catch (ParseException e) {
			fail(e);
			return;
		}
		assertNotNull(testConfiguration);
		var numberOfItems = testConfiguration.getLevel();
		assertEquals(TestPer.GROUP, numberOfItems.stream().findFirst().orElseThrow());
		var numberOfTasks = testConfiguration.getNumberTasks();
		assertEquals(5, numberOfTasks);
		var markSchema = testConfiguration.getMarkSchema();
		assertEquals("NUMBERS", markSchema);
		var order = testConfiguration.getOrdering();
		assertEquals(TestConfiguration.ItemSorting.RANDOM, order);
		var items = testConfiguration.getItemConfigurations();
		assertEquals(5, items.size());
		var version = testConfiguration.getVersion();
		assertEquals("1.0.0", version);
		var description = testConfiguration.getDescription();
		assertEquals("Default-TestConfiguration for the MDTea-prototype", description);
		var configurationName = testConfiguration.getConfigurationName();
		assertEquals("Default-TestConfiguration", configurationName);
	}

}