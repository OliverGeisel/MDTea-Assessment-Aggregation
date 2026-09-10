package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.FileSystemStorageService;
import de.olivergeisel.materialgenerator.StorageProperties;
import de.olivergeisel.materialgenerator.finalization.export.ImageService;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestPer;
import de.olivergeisel.materialgenerator.generation.configuration.TrueFalseConfiguration;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.data.neo4j.test.autoconfigure.AutoConfigureDataNeo4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.neo4j.Neo4jContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureDataNeo4j
@Testcontainers
@Tag("Integration")
class MaterialRepositoryTest {


	@Container
	static Neo4jContainer neo4jContainer = new Neo4jContainer("neo4j:2026-community");

	@MockitoBean
	ImageService imageService;
	@Autowired
	private MaterialRepository       materialRepository;
	@MockitoBean
	private StorageProperties        storageProperties;
	@MockitoBean
	private FileSystemStorageService fileSystemStorageService;

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
		registry.add(
				"spring.neo4j.authentication.password",
				neo4jContainer::getAdminPassword
		);
	}

	@BeforeEach
	void setUp() {

	}

	@Test
	void saveTestMaterial() {
		var testConfiguration = new TestConfiguration("Test", "Test", "1.0.0",
				1, de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration.ItemSorting.RANDOM,
				"1", List.of("1"), Map.of("1", 1),
				List.of(new TrueFalseConfiguration(null)), Set.of(TestPer.GROUP), false);
		var test = new TestMaterial(null, testConfiguration);
		test = materialRepository.save(test);
		var result = materialRepository.findById(test.getId());
		assertTrue(result.isPresent());
		assertEquals(test, result.get());
	}
}