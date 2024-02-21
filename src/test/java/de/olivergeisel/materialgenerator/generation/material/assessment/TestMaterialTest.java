package de.olivergeisel.materialgenerator.generation.material.assessment;

import org.junit.jupiter.api.*;

@Tag("Unit")
@Disabled
class TestMaterialTest {

	private TestMaterial testMaterial;

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void constructorTestParameterLess() {
		testMaterial = new TestMaterial();
	}

	@Test
	void constructorTestParameterList() {
		testMaterial = new TestMaterial(null, null);
	}

	@Test
	void getTestConfiguration() {
	}
}