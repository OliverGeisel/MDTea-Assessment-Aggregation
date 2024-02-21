package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
class TransferGeneratorTest {

	private TransferGenerator generator;

	@Mock
	private CoursePlan     plan;
	@Mock
	private KnowledgeModel model;
	@Mock
	private TemplateSet    templateSet;

	@BeforeEach
	void setUp() {
		generator = new TransferGenerator();
	}


	@Test
	void createListMaterialCore() {
	}

	@Test
	void materialForKnow() {
	}

	@Test
	void materialForComment() {
	}

	@Test
	void materialForCreate() {
	}

	@Test
	void materialForControl() {
	}

	@Test
	void materialForUse() {
	}

	@Test
	void processTargets() {
	}

	@Test
	void materialForFirstLook() {
	}

	@Test
	void materialForTranslate() {
	}

	@Test
	void inputWithGeneratorInput() {
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
	void inputSeparate() {
		generator.input(templateSet, model, plan);

		assertEquals(model, generator.model);
		assertEquals(plan, generator.plan);
		assertEquals(templateSet, generator.templateSet);
	}

	@Test
	void updateEmptyPlan() {
		generator.input(templateSet, model, plan);
		var update = generator.update();
		assertFalse(update);
		var output = generator.output();
		assertEquals(0, output.size());
	}

}