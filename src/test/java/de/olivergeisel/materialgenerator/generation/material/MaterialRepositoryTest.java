package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.FileSystemStorageService;
import de.olivergeisel.materialgenerator.StorageProperties;
import de.olivergeisel.materialgenerator.finalization.export.ImageService;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestPer;
import de.olivergeisel.materialgenerator.generation.configuration.TrueFalseConfiguration;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureDataNeo4j
@Tag("Integration")
class MaterialRepositoryTest {

	@MockBean
	ImageService imageService;
	@Autowired
	private MaterialRepository       materialRepository;
	@Autowired
	private TestEntityManager        entityManager;
	@MockBean
	private StorageProperties        storageProperties;
	@MockBean
	private FileSystemStorageService fileSystemStorageService;

	@BeforeEach
	void setUp() {
	}

	@Test
	void saveTestMaterial() {
		var testConfiguration = new TestConfiguration("Test", "Test", "1.0.0",
				1, de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration.ItemSorting.RANDOM,
				"1", List.of("1"), Map.of("1", 1),
				List.of(new TrueFalseConfiguration(null)), Set.of(TestPer.GROUP));
		var test = new TestMaterial(null, testConfiguration);
		test = materialRepository.save(test);
		var result = materialRepository.findById(test.getId());
		assertTrue(result.isPresent());
		assertEquals(test, result.get());
	}
}