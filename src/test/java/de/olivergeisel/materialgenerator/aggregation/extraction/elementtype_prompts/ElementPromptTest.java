package de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElementPromptTest {

	private ElementPrompt elementPrompt;

	@BeforeEach
	void setUp() {
		elementPrompt =
				new ElementPrompt("instruction", "format | part", "a fragment with knowledge", DeliverType.MULTIPLE) {
//region setter/getter
					public String getPrompt() {
						return STR."\{getInstruction()} \{getFragment()}";
					}
//endregion
				};
	}


	@Test
	void testConstructor() {
		assertEquals("instruction", elementPrompt.getInstruction(), "Instruction should be 'instruction'");
		assertEquals("format | part", elementPrompt.getWantedFormat(), "WantedFormat should be 'format | part'");
		assertEquals("a fragment with knowledge", elementPrompt.getFragment(), "Fragment should be 'wantedFormat'");
		assertEquals(DeliverType.MULTIPLE, elementPrompt.getDeliverType(), "DeliverType should be MULTIPLE");
	}

	@Test
	void testConstructorNullInstruction() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ElementPrompt(null, "format | part", "a fragment with knowledge", DeliverType.MULTIPLE) {
//region setter/getter
				public String getPrompt() {
					return STR."\{getInstruction()} \{getFragment()}";
				}
//endregion
			};
		}, "IllegalArgumentException should be thrown");
	}

	@Test
	void testConstructorNullWantedFormat() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ElementPrompt("instruction", null, "a fragment with knowledge", DeliverType.MULTIPLE) {
//region setter/getter
				public String getPrompt() {
					return STR."\{getInstruction()} \{getFragment()}";
				}
//endregion
			};
		}, "IllegalArgumentException should be thrown");
	}

	@Test
	void testConstructorNullFragment() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ElementPrompt("instruction", "format | part", null, DeliverType.MULTIPLE) {
//region setter/getter
				public String getPrompt() {
					return STR."\{getInstruction()} \{getFragment()}";
				}
//endregion
			};
		}, "IllegalArgumentException should be thrown");
	}

	@Test
	void testConstructorNullDeliverType() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ElementPrompt("instruction", "format | part", "a fragment with knowledge", null) {
//region setter/getter
				public String getPrompt() {
					return STR."\{getInstruction()} \{getFragment()}";
				}
//endregion
			};
		}, "IllegalArgumentException should be thrown");
	}

	@Test
	void getPrompt() {
		assertEquals("instruction a fragment with knowledge", elementPrompt.getPrompt(), "Prompt should be match");
	}

	@Test
	void getDeliverType() {
		assertEquals(DeliverType.MULTIPLE, elementPrompt.getDeliverType(), "DeliverType should be MULTIPLE");
	}

	@Test
	void setDeliverType() {
		elementPrompt.setDeliverType(DeliverType.SINGLE);
		assertEquals(DeliverType.SINGLE, elementPrompt.getDeliverType(), "DeliverType should be SINGLE");
	}

	@Test
	void getWantedFormat() {
		assertEquals("format | part", elementPrompt.getWantedFormat(), "WantedFormat should be 'format | part'");
	}

	@Test
	void setWantedFormat() {
		elementPrompt.setWantedFormat("format | part1 | part2");
		assertEquals("format | part1 | part2", elementPrompt.getWantedFormat(),
				"WantedFormat should be 'format | part1 | part2'");
	}

	@Test
	void getInstruction() {
		assertEquals("instruction", elementPrompt.getInstruction(), "Instruction should be 'instruction'");
	}

	@Test
	void setInstruction() {
		elementPrompt.setInstruction("new instruction");
		assertEquals("new instruction", elementPrompt.getInstruction(), "Instruction should be 'new instruction'");
	}

	@Test
	void getFragment() {
		assertEquals("a fragment with knowledge", elementPrompt.getFragment(),
				"Fragment should be 'a fragment with knowledge'");
	}

	@Test
	void setFragment() {
		elementPrompt.setFragment("new wantedFormat");
		assertEquals("new wantedFormat", elementPrompt.getFragment(), "Fragment should be 'new wantedFormat'");
	}
}