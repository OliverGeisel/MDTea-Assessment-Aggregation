package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import jakarta.persistence.Entity;

import java.util.List;
import java.util.UUID;


/**
 * Summarizes a specific topic.
 */
@Entity
public class SummaryMaterial extends ComplexMaterial {

	private String topic;

	private List<List<UUID>> sections;


	protected SummaryMaterial() {
		super();
	}

	public SummaryMaterial(String topic) {
		super();
		this.topic = topic;
	}

}
