package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
class AbstractGeneratorTest {

	private AbstractGenerator generator;

	@Mock
	private CoursePlan     plan;
	@Mock
	private KnowledgeModel model;
	@Mock
	private TemplateSet    templateSet;


	@BeforeEach
	void setUp() {
		generator = new AbstractGenerator() {
			@Override
			public boolean createSimpleMaterial() {
				return false;
			}

			@Override
			public boolean createComplexMaterial() {
				return false;
			}

			@Override
			protected void processTargets(List<ContentTarget> targets) throws IllegalStateException {

			}

			@Override
			protected List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge) {
				return null;
			}

			@Override
			protected List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
					throws NoSuchElementException {
				return null;
			}
		};
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getMainKnowledge() {
	}

	@Test
	void testGetMainKnowledge() {
	}

	@Test
	void testGetMainKnowledge1() {
	}

	@Test
	void testGetMainKnowledge2() {
	}

	@Test
	void testGetMainKnowledge3() {
	}

	@Test
	void testGetMainKnowledge4() {
	}

	@Test
	void collectElementsWithId() {
	}

	@Test
	void getWantedRelationsKnowledge() {
	}

	@Test
	void inputWithInput() {
		var input = mock(GeneratorInput.class);
		when(input.getModel()).thenReturn(model);
		when(input.getPlan()).thenReturn(plan);
		when(input.getTemplates()).thenReturn(templateSet);

		generator.input(input);

		assertEquals(model, generator.model);
		assertEquals(plan, generator.plan);
		assertEquals(templateSet, generator.templateSet);

	}

	@Test
	void InputWithAllParameters() {
		generator.input(templateSet, model, plan);

		assertEquals(model, generator.model);
		assertEquals(plan, generator.plan);
		assertEquals(templateSet, generator.templateSet);
	}

	@Test
	void update() {
		generator.input(templateSet, model, plan);
		var update = generator.update();
		assertFalse(update);
	}

	@Test
	void output() {
	}

	@Test
	void changedAtStart() {
		assertFalse(generator.isUnchanged());
		generator.changed();
		assertFalse(generator.isUnchanged());
	}

	@Test
	void changedAfterSetOneAttributeTrue() {
		generator.setUnchanged(true);
		generator.setModel(model);
		assertFalse(generator.isUnchanged());

		generator.setUnchanged(true);
		generator.setPlan(plan);
		assertFalse(generator.isUnchanged());

		generator.setUnchanged(true);
		generator.setTemplateSet(templateSet);
		assertFalse(generator.isUnchanged());
	}

	@Test
	void isReadyAtStart() {
		assertFalse(generator.isReady());
	}


	@Test
	void isUnchangedAtBeginning() {
		assertFalse(generator.isUnchanged());
	}

	@Test
	void setUnchanged() {
	}

}