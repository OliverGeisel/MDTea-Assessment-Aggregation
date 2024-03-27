package de.olivergeisel.materialgenerator.aggregation;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Load all models written down in the models.json file in <b>/gpt-connection</b>.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see AggregationController
 * @since 1.1.0
 */
public class ModelNameLoader {

	private ModelNameLoader() {
	}

	public static Map<String, String> load() {
		try (var file = new FileReader("gpt-connection/models.json")) {
			var json = (List<Map<String, String>>) new JSONParser(file).parse();
			var back = new LinkedHashMap<String, String>();
			for (var entry : json) {
				back.put(entry.get("name"), entry.get("file"));
			}
			return back;
		} catch (ParseException | IOException e) {
			return Map.of();
		}
	}
}
