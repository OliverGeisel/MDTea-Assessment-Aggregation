package de.olivergeisel.materialgenerator.generation;

import com.codeborne.selenide.Conditional;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UI")
class GeneratorControllerApplicationTest {

	private Conditional<WebDriver> driver;

	@BeforeEach
	void setUp() {
		driver = Selenide.webdriver();
	}

	@AfterEach
	void tearDown() {}

	@Test
	void generator_select_blank_template_set() {
		// Open URL
		open("/generator");
		// get element via Css selector or XPath and check text
		var heading = $("h3");
		// check text (equal to assert-Method in JUnit)
		heading.shouldHave(text("Wähle ein TemplateSet"));

		// interact with the page, e.g. click a button
		// In this case, select blank template
		var section = $$("section").get(1); // get first section
		var button = section.$("button"); // get first button
		button.click();
		// check new page URL
		assertTrue(driver.driver().getWebDriver().getCurrentUrl().contains(
				"/generator/plan-selection?template=blank"
		));

	}

	@Test
	void generator_select_color_template_set() {
		// Open URL
		open("/generator");

		var section = $$("section").get(1);
		var buttons = section.$$("button"); // get all buttons in section
		buttons.get(1).click();
		// check new page URL
		assertTrue(driver.driver().getWebDriver().getCurrentUrl().contains(
				"generator/plan-selection?template=color"
		));
	}

	@Test
	void generatorAuto_with_blank() {
		open("/generator/plan-selection?template=BLANK");
		// Set value (example) for input file-field
		var input = $("input[name='plan']"); // get input field by name
		input.uploadFromClasspath("data/curriculum/Test-Plan.json");
		var submitButton = $("button[type='submit']");
		submitButton.click();
	}


	@Disabled
	@Test
	void overviewGenerationId() {
	}

	@Disabled
	@Test
	void overviewGeneration() {
	}

	@Disabled
	@Test
	void testOverviewGeneration() {
	}

	@Disabled
	@Test
	void generateTests() {
	}

	@Disabled
	@Test
	void testGenerateTests() {
	}

	@Disabled
	@Test
	void generate() {
	}

	@Disabled
	@Test
	void showMaterials() {
	}
}