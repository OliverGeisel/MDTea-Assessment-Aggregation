package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.core.course.CourseChapter;
import de.olivergeisel.materialgenerator.core.course.CourseGroup;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.List;


/**
 * Represents a material that contains an overview over a group ({@link CourseGroup}) or chapter
 * ({@link CourseChapter}) in the
 * course plan.
 * <p>
 * The overview has a list of all {@link Term}s in this group or chapter.
 * It also contains a headline and a description. The headline is the title of the group or chapter.
 * The description is a short description of the group or chapter.
 * </p>
 *
 * @version 1.1.0
 * @see ComplexMaterial
 * @see Term
 * @see SummaryMaterial
 * @see CourseGroup
 * @see CourseChapter
 * @since 1.1.0
 */
@Entity
public class OverviewMaterial extends ComplexMaterial {

	@Column(length = 5_000)
	private String description;
	private String headline;

	protected OverviewMaterial() {
		super(MaterialType.COMPLEX, TemplateType.OVERVIEW);
	}

	/**
	 * Creates a new OverviewMaterial with a headline and a description.
	 *
	 * @param headline    the headline of the overview
	 * @param description a short description of the overview. Can be empty.
	 */
	public OverviewMaterial(String headline, String description) {
		this();
		this.headline = headline;
		this.description = description;
	}

	/**
	 * Creates a new OverviewMaterial with a headline, a description and a list of parts.
	 *
	 * @param headline    the headline of the overview
	 * @param description a short description of the overview. Can be empty.
	 * @param parts       a list of {@link SummaryMaterial}s that are part of this overview
	 */
	public OverviewMaterial(String headline, String description, List<SummaryMaterial> parts) {
		this();
		this.headline = headline;
		this.description = description;
		getParts().addAll(parts);
	}

	/**
	 * Appends a {@link SummaryMaterial} to the list of parts of this material.
	 *
	 * @param part the {@link SummaryMaterial} to append
	 * @return true if the part was appended, false otherwise
	 * @throws IllegalArgumentException if the part is not a {@link SummaryMaterial}
	 */
	@Override
	public boolean append(Material part) throws IllegalArgumentException {
		if (!(part instanceof SummaryMaterial)) {
			throw new IllegalArgumentException("Only SummaryMaterial can be added to an OverviewMaterial");
		}
		return super.append(part);
	}

	//region setter/getter
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}
//endregion
}
