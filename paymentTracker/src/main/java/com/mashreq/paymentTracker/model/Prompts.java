package com.mashreq.paymentTracker.model;

import java.math.BigInteger;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_prompt")
public class Prompts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Prompt Key should not be empty")
	@Column(name = "pr_key")
	private String promptKey;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "display_name")
	private String displayName;

	@NotNull(message = "Prompt Required should not be empty")
	@Column(name = "pr_required")
	private String promptRequired;

	@NotNull(message = "Prompt Order should not be empty")
	@Column(name = "pr_Order")
	private BigInteger promptOrder;

	@OneToOne
	@JoinColumn(name = "ent_id")
	private DataEntity entity;

	@ManyToOne(targetEntity = Report.class)
	@JsonBackReference
	@JoinColumn(name = "report_id")
	private Report report;

	public Prompts() {
		super();
	}

	
	public Prompts(Long id, @NotNull(message = "Prompt Key should not be empty") String promptKey,
			@NotNull(message = "Display name should not be empty") String displayName,
			@NotNull(message = "Prompt Required should not be empty") String promptRequired,
			@NotNull(message = "Prompt Order should not be empty") BigInteger promptOrder, DataEntity entity,
			Report report) {
		super();
		this.id = id;
		this.promptKey = promptKey;
		this.displayName = displayName;
		this.promptRequired = promptRequired;
		this.promptOrder = promptOrder;
		this.entity = entity;
		this.report = report;
	}


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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPromptRequired() {
		return promptRequired;
	}

	public void setPromptRequired(String promptRequired) {
		this.promptRequired = promptRequired;
	}


	public DataEntity getEntity() {
		return entity;
	}

	public void setEntity(DataEntity entity) {
		this.entity = entity;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public BigInteger getPromptOrder() {
		return promptOrder;
	}

	public void setPromptOrder(BigInteger promptOrder) {
		this.promptOrder = promptOrder;
	}

	@Override
	public String toString() {
		return "Prompts [id=" + id + ", promptKey=" + promptKey + ", displayName=" + displayName + ", promptRequired="
				+ promptRequired + ", promptOrder=" + promptOrder + ", entity=" + entity + ", report=" + report + "]";
	}

}
