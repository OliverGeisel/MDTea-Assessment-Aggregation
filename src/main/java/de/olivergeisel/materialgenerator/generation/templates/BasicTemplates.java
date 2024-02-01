package de.olivergeisel.materialgenerator.generation.templates;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.*;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class BasicTemplates {

	public static final Set<String>    TEMPLATES = Set.of("DEFINITION", "TEXT", "EXAMPLE", "ACRONYM", "LIST",
			"SYNONYM", "PROOF", "CODE", "IMAGE", "SUMMARY");
	@Transient
	private static      BasicTemplates instance;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;
	@ManyToOne
	@JoinColumn(name = "definition_template_id")
	private DefinitionTemplate definitionTemplate;
	@ManyToOne
	@JoinColumn(name = "text_template_id")
	private TextTemplate textTemplate;
	@ManyToOne
	@JoinColumn(name = "example_template_id")
	private ExampleTemplate exampleTemplate;
	@ManyToOne
	@JoinColumn(name = "proof_template_id")
	private ProofTemplate   proofTemplate;
	@ManyToOne
	@JoinColumn(name = "list_template_id")
	private ListTemplate    listTemplate;
	@ManyToOne
	@JoinColumn(name = "acronym_template_id")
	private AcronymTemplate acronymTemplate;
	@ManyToOne
	@JoinColumn(name = "synonym_template_id")
	private SynonymTemplate synonymTemplate;
	@ManyToOne
	@JoinColumn(name = "code_template_id")
	private CodeTemplate codeTemplate;
	@ManyToOne
	@JoinColumn(name = "image_template_id")
	private ImageTemplate imageTemplate;
	@ManyToOne
	@JoinColumn(name = "summary_template_id")
	private             SummaryTemplate summaryTemplate;

	protected BasicTemplates() {
		this.definitionTemplate = new DefinitionTemplate();
		this.textTemplate = new TextTemplate();
		this.exampleTemplate = new ExampleTemplate();
		this.acronymTemplate = new AcronymTemplate();
		this.listTemplate = new ListTemplate();
		this.synonymTemplate = new SynonymTemplate();
		this.proofTemplate = new ProofTemplate();
		this.codeTemplate = new CodeTemplate();
		this.imageTemplate = new ImageTemplate();
		this.summaryTemplate = new SummaryTemplate();
	}

	//region setter/getter
	public SummaryTemplate getSummaryTemplate() {
		return summaryTemplate;
	}

	public void setSummaryTemplate(
			SummaryTemplate summaryTemplate) {
		this.summaryTemplate = summaryTemplate;
	}

	public CodeTemplate getCodeTemplate() {
		return codeTemplate;
	}

	public void setCodeTemplate(CodeTemplate codeTemplate) {
		this.codeTemplate = codeTemplate;
	}

	public ImageTemplate getImageTemplate() {
		return imageTemplate;
	}

	public void setImageTemplate(
			ImageTemplate imageTemplate) {
		this.imageTemplate = imageTemplate;
	}

	public ProofTemplate getProofTemplate() {
		return proofTemplate;
	}

	public void setProofTemplate(ProofTemplate proofTemplate) {
		this.proofTemplate = proofTemplate;
	}

	public static BasicTemplates getInstance() {
		if (instance == null) {
			instance = new BasicTemplates();
		}
		return instance;
	}

	public UUID getId() {
		return id;
	}

	public TemplateInfo[] getTemplates() {
		return new TemplateInfo[]{definitionTemplate, textTemplate, exampleTemplate, acronymTemplate, listTemplate,
				synonymTemplate, proofTemplate, codeTemplate, imageTemplate};
	}

	public DefinitionTemplate getDefinitionTemplate() {
		return definitionTemplate;
	}

	public void setDefinitionTemplate(DefinitionTemplate definitionTemplate) {
		this.definitionTemplate = definitionTemplate;
	}

	public TextTemplate getTextTemplate() {
		return textTemplate;
	}

	public void setTextTemplate(TextTemplate textTemplate) {
		this.textTemplate = textTemplate;
	}

	public ExampleTemplate getExampleTemplate() {
		return exampleTemplate;
	}

	public void setExampleTemplate(ExampleTemplate exampleTemplate) {
		this.exampleTemplate = exampleTemplate;
	}

	public AcronymTemplate getAcronymTemplate() {
		return acronymTemplate;
	}

	public void setAcronymTemplate(AcronymTemplate acronymTemplate) {
		this.acronymTemplate = acronymTemplate;
	}

	public ListTemplate getListTemplate() {
		return listTemplate;
	}

	public void setListTemplate(ListTemplate listTemplate) {
		this.listTemplate = listTemplate;
	}

	public SynonymTemplate getSynonymTemplate() {
		return synonymTemplate;
	}

	public void setSynonymTemplate(SynonymTemplate synonymTemplate) {
		this.synonymTemplate = synonymTemplate;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BasicTemplates that)) return false;

		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
