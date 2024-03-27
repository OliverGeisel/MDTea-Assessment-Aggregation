package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.course.Meta;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A RawCourse is a {@link Course} that is not yet finalized.
 * <p>
 * A RawCourse contains a {@link RawCourseOrder} and a {@link CourseMetadataFinalization}.
 * It can be edited.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Course
 * @see RawCourseOrder
 * @see CourseMetadataFinalization
 * @since 0.2.0
 */
@Entity
public class RawCourse extends Course {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;

	/**
	 * The id of the plan that is used to create this course.
	 */
	private UUID                       planId;
	/**
	 * The name of the templateSet that is used to create this course.
	 */
	private String                     templateName;
	@OneToOne(cascade = CascadeType.ALL)
	private CourseMetadataFinalization metadata;
	@OneToOne(cascade = CascadeType.ALL)
	private RawCourseOrder rawCourseOrder;
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private Set<Material> unassignedMaterials = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Goal> goals;

	/**
	 * Assign materials to this course.
	 *
	 * @param materials The materials to assign
	 * @return True if all materials are assigned. False otherwise
	 */
	public boolean assignMaterial(Collection<MaterialAndMapping> materials) {
		var assigner = rawCourseOrder.assignMaterial(materials.stream().map(MaterialAndMapping::material).collect(
				Collectors.toSet()));
		// remove all items Todo -> only items, that are assigned to a test
		var filtered = assigner.getUnassignedMaterials().stream().filter(it -> !(it instanceof ItemMaterial));
		unassignedMaterials.addAll(filtered.toList());
		return true;
	}

	protected RawCourse() {
	}

	public RawCourse(CoursePlan plan, String templateName, Set<Goal> goals) {
		this.planId = plan.getId();
		this.templateName = templateName;
		this.goals = goals;
		metadata = new CourseMetadataFinalization(plan);
		rawCourseOrder = new RawCourseOrder(plan, goals);
	}

	/**
	 * Get the number of materials that are assigned to this course.
	 *
	 * @return The number of materials
	 */
	public int materialCount() {
		return rawCourseOrder.materialCount();
	}

	//region setter/getter

	/**
	 * Change the plan. THis will also change the course order. Materials must be reassigned.
	 */
	public void changePlan(CoursePlan plan) {
		setPlanId(plan.getId());
		// Todo change rawCourseOrder
	}
	/**
	 * Get the materials that are assigned to this course.
	 *
	 * @return An unmodifiable materials that are assigned to this course
	 */
	public List<Material> getMaterials() {
		return rawCourseOrder.getMaterials();
	}
	public Set<Material> getUnassignedMaterials() {return unassignedMaterials;}

	/**
	 * Say if a course has enough materials and fulfill all requirements to use.
	 *
	 * @return True if all requirements are fulfilled. False otherwise
	 */
	public boolean isValid() {
		return rawCourseOrder.isValid();
	}

	public UUID getId() {
		return id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Set<Goal> getGoals() {
		return goals;
	}

	public UUID getPlanId() {
		return planId;
	}

	private void setPlanId(UUID planId) {
		this.planId = planId;
	}

	public CourseMetadataFinalization getMetadata() {
		return metadata;
	}

	public void setMetadata(CourseMetadataFinalization metadata) {
		this.metadata = metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Meta getMeta() {
		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RawCourseOrder getOrder() {
		return rawCourseOrder;
	}
//endregion
}
