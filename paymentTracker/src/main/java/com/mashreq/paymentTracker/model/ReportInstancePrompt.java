package com.mashreq.paymentTracker.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "ops_report_inst_prompt")
public class ReportInstancePrompt implements Serializable {

	private static final long serialVersionUID = 5040713488603240603L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "prompt_key")
	private String promptKey;
	@Column(name = "prompt_value")
	private String promptValue;
	@ManyToOne
	@JoinColumn(name = "report_id")
	private Report report;
	@OneToOne
	@JoinColumn(name = "prompt_id")
	private Prompts prompt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_inst_id")
	private ReportInstance reportInstance;

	@OneToOne
	@JoinColumn(name = "entity_id")
	private DataEntity entity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPromptKey() {
		return promptKey;
	}

	public void setPromptKey(String promptKey) {
		this.promptKey = promptKey;
	}

	public String getPromptValue() {
		return promptValue;
	}

	public void setPromptValue(String promptValue) {
		this.promptValue = promptValue;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Prompts getPrompt() {
		return prompt;
	}

	public void setPrompt(Prompts prompt) {
		this.prompt = prompt;
	}

	public ReportInstance getReportInstance() {
		return reportInstance;
	}

	public void setReportInstance(ReportInstance reportInstance) {
		this.reportInstance = reportInstance;
	}

	public DataEntity getEntity() {
		return entity;
	}

	public void setEntity(DataEntity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "ReportInstancePrompt [id=" + id + ", promptKey=" + promptKey + ", promptValue=" + promptValue
				+ ", report=" + report + ", prompt=" + prompt + ", reportInstance=" + reportInstance + ", entity="
				+ entity + "]";
	}

}
