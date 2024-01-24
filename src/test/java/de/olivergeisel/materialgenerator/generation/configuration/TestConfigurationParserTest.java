package de.olivergeisel.materialgenerator.generation.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestConfigurationParserTest {

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
					"tasks": [
				      {
				        "type": "TrueFalse",
				        "testParameters": null
				      },
					]
				}
				""";

		assertThrows(IllegalArgumentException.class, () -> TestConfigurationParser.parse(json));
	}

	@Test
	void parseWithTestPer_isChapter() {
		var json = """
				{
					"testPer": "[CHAPTER]",
					"testParameters": {
						"tries": 1,
						"pointsPerUnit": 1,
						"negativeFactor": 0
					}
				}
				""";
	}

	@Test
	void parseWithTestPer_isAll() {
		var json = """
				{
					"testPer": "[ALL]",
					"testParameters": {
						"tries": 1,
						"pointsPerUnit": 1,
						"negativeFactor": 0
					}
				}
				""";
	}

	@Test
	void parseWithTestPer_isMaterial() {
		var json = """
				{
					"testPer": "[MATERIAL]",
					"testParameters": {
						"tries": 1,
						"pointsPerUnit": 1,
						"negativeFactor": 0
					}
				}
				""";
	}
}