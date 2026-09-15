package de.olivergeisel.materialgenerator.generation.material;

/**
 * Only a simple record to hold a {@link Material} and the related {@link MaterialMappingEntry}.
 *
 * @param material the material
 * @param mapping  the mapping to the material
 * @param <M>      the type of the material
 */
public record MaterialAndMapping<M extends Material>(M material, MaterialMappingEntry<? extends M> mapping) {


	public MaterialAndMapping(MaterialMappingEntry<M> mapping) {
		this(mapping.getMaterial(), mapping);
	}
}
