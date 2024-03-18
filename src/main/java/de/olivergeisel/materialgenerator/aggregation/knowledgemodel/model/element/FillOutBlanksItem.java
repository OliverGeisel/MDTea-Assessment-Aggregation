package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A fill out blanks item is an item where the user has to fill out blanks in a text.
 * <p>
 * The text contains blanks marked with <_____>.
 * The user has to fill out the blanks with the correct answers.
 * The order of the blanks matters. The first blank in the text corresponds to the first answer in the list of blanks.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Item
 * @see KnowledgeElement
 * @since 1.1.0
 */
@Node
public class FillOutBlanksItem extends Item {

	public static final String       BLANK  = "<_____>";
	@ElementCollection
	private             List<String> blanks = new ArrayList<>();
	@Column(length = 2000)
	private             String       text;

	protected FillOutBlanksItem() {
		super("FILL_OUT_BLANKS");
	}

	/**
	 * Creates a new fill out blanks item.
	 *
	 * @param text   The text of the item. The blanks are marked with <_____>.
	 * @param blanks The correct answers for the blanks. Order matters.
	 * @param id     The id of the item.
	 * @throws IllegalArgumentException If the number of blanks does not match the number of blanks in the text or no blanks are given.
	 */
	public FillOutBlanksItem(String text, List<String> blanks, String id) throws IllegalArgumentException {
		super(text, id, "FILL_OUT_BLANKS");
		if (blanks.isEmpty()) {
			throw new IllegalArgumentException("A fill out blanks item must have at least one blank.");
		}
		Matcher matcher = Pattern.compile(BLANK).matcher(text);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		if (count != blanks.size()) {
			throw new IllegalArgumentException(
					STR."The number of blanks must match the number of blanks in the text. Found \{count} blanks in the text and \{blanks.size()} blanks in the list.");
		}
		this.blanks = blanks;
		this.text = text;
	}

	//region setter/getter
	public List<String> getBlanks() {
		return blanks;
	}

	public void setBlanks(List<String> blanks) {
		this.blanks = blanks;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		setContent(text);
	}

	public int getNumberBlanks() {
		return blanks.size();
	}

	@Override
	public void setContent(String content) {
		super.setContent(content);
		this.text = content;
	}
//endregion

}
