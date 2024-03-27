package de.olivergeisel.materialgenerator.finalization.export.opal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DefaultXMLWriter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultXMLWriter.class);

	public static StreamResult write(Document document, String fileName) throws RuntimeException {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			logger.error("transformer for XML couldn't be created.");
			throw new RuntimeException("The transformer was not created");
		}
		// default properties
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(document);
		int lastDotIndex = fileName.lastIndexOf('.');
		fileName = lastDotIndex == -1 || lastDotIndex == 0 ? fileName : fileName.substring(0, lastDotIndex);
		StreamResult result = new StreamResult(STR."\{fileName}.xml");
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			logger.error(STR."Error while creating XML for \{fileName}!");
		}
		logger.info(STR."XML-File \{fileName}.xml created!");
		return result;
	}
}
