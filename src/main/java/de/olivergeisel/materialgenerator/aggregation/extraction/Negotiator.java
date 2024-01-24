package de.olivergeisel.materialgenerator.aggregation.extraction;

import java.util.List;


/**
 * @param <T>
 */
public interface Negotiator<T> {

	List<T> extract();
}
