package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("UnitTest")
class MaterialOrderCollectionTest {

	private MaterialOrderCollection collection;

	@BeforeEach
	void setUp() {
		collection = new MaterialOrderCollection() {
			@Override
			public Iterator<MaterialOrderPart> iterator() {
				return null;
			}

			@Override
			public Material findMaterial(UUID materialId) {
				return null;
			}

			@Override
			public Relevance updateRelevance() {
				return null;
			}

			@Override
			public int materialCount() {
				return 0;
			}

			@Override
			public Set<Material> assignMaterial(Set<Material> materials) {
				return null;
			}

			@Override
			public boolean assign(Material materials) {
				return false;
			}

			@Override
			public boolean assignMaterial(MaterialAssigner assigner) {
				return false;
			}

			@Override
			public boolean remove(UUID partId) {
				return false;
			}

			@Override
			public Collection<NameAndId> collectionsNameAndId() {
				return null;
			}

//region setter/getter

			@Override
			public MaterialOrderPart find(UUID id) {
				return null;
			}
			@Override
			public List<Material> getMaterials() {
				return null;
			}
			@Override
			public Relevance getRelevance() {
				return null;
			}

			@Override
			public List<UUID> getCollectionIds() {
				return null;
			}

			@Override
			public boolean isValid() {
				return false;
			}
//endregion
		};
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void appendAliasEmptyTest() {
		assertEquals(0, collection.getAlias().size());
		collection.appendAlias("test");
		assertEquals(1, collection.getAlias().size());
	}

	@Test
	void appendAliasNotEmptyTest() {
		collection.appendAlias("test");
		var size = collection.getAlias().size();
		assertFalse(collection.appendAlias("test"), "Alias should not be added twice");
		assertEquals(size, collection.getAlias().size());
	}

	@Test
	void removeAliasNotEmptyTest() {
		collection.appendAlias("test");
		var size = collection.getAlias().size();
		collection.removeAlias("test");
		assertEquals(size - 1, collection.getAlias().size());
	}

	@Test
	void removeAliasEmptyTest() {
		var size = collection.getAlias().size();
		assertFalse(collection.removeAlias("test"));
		assertEquals(size, collection.getAlias().size());
	}

	@Test
	void moveUpOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertTrue(collection.moveUp("test2"));
		assertEquals("test2", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test1", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveUpNotOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.moveUp("test1"));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveUpNotContainTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.moveUp("test4"));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveDownOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertTrue(collection.moveDown("test2"));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveDownNotOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.moveDown("test3"));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveDownNotContainTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.moveDown("test4"));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertTrue(collection.move("test1", 2));
		assertEquals("test2", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test1", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveNotOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.move("test1", 3));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void moveNotContainTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertFalse(collection.move("test4", 2));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void insertOkayTest() {
		assertTrue(collection.insert("test3", 0));
		assertTrue(collection.insert("test2", 0));
		assertTrue(collection.insert("test1", 0));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
		assertEquals("test2", collection.getAlias().stream().skip(1).findFirst().orElse(""));
		assertEquals("test3", collection.getAlias().stream().skip(2).findFirst().orElse(""));
	}

	@Test
	void insertNotOkayTest() {
		collection.appendAlias("test1");
		assertFalse(collection.insert("test1", 1));
		assertFalse(collection.insert("test1", 0));
		assertEquals("test1", collection.getAlias().stream().findFirst().orElse(""));
	}

	@Test
	void getMatchingAliasOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		var result = collection.getMatchingAlias(Set.of("test1", "test2", "test3"));
		assertEquals(3, result.size());
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test2"));
		assertTrue(result.contains("test3"));
	}

	@Test
	void getMatchingAliasNotOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		var result = collection.getMatchingAlias(Set.of("test1", "test2", "test4"));
		assertEquals(2, result.size());
		assertTrue(result.contains("test1"));
		assertTrue(result.contains("test2"));
		assertFalse(result.contains("test3"));
		assertFalse(result.contains("test4"));
	}

	@Test
	void getFirstMatchingAliasOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertEquals(new MaterialOrderCollection.AliasPosition(0, "test1"), collection.getFirstMatchingAlias(Set.of(
				"test1", "test2", "test3")));
	}

	@Test
	void getFirstMatchingAliasNotOkayTest() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertTrue(collection.getFirstMatchingAlias(Set.of("test4")).isEmpty());
	}

	@Test
	void getFirstMatchingAliasIndexOkay() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertEquals(-1, collection.getFirstMatchingAliasIndex(Set.of("test4")));
	}

	@Test
	void getFirstMatchingAliasIndexNotOkay() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertEquals(0, collection.getFirstMatchingAliasIndex(Set.of("test1", "test2", "test3")));
		assertEquals(1, collection.getFirstMatchingAliasIndex(Set.of("test2", "test3")));
	}

	@Test
	void getFirstMatchingAliasNameOkay() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertEquals("test1", collection.getFirstMatchingAliasName(Set.of("test1", "test3")));
	}

	@Test
	void getFirstMatchingAliasNameNotOkay() {
		collection.appendAlias("test1");
		collection.appendAlias("test2");
		collection.appendAlias("test3");
		assertEquals("", collection.getFirstMatchingAliasName(Set.of("test4")));
	}

	@Test
	void setTopicTest() {
		var topic = mock(Topic.class);
		collection.setTopic(topic);
		assertEquals(topic, collection.getTopic());
	}

	@Test
	void getAliasAtStartTest() {
		assertEquals(0, collection.getAlias().size());
	}

	@Test
	void getAliasAfterAppendTest() {
		collection.appendAlias("test");
		assertEquals(1, collection.getAlias().size());
	}
}