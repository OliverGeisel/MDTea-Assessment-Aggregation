package de.olivergeisel.materialgenerator.aggregation.extraction;

import java.util.List;


/**
 * A Negotiator is a class that extracts a list of elements from a given source.
 *
 * @param <T>
 */
public interface Negotiator<T> {

	List<T> extract();
}
