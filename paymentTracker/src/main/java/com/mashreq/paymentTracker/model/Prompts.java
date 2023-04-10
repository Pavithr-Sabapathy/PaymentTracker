package com.mashreq.paymentTracker.model;

import java.math.BigInteger;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_prompt")
public class Prompts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

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

	// TODO -- @NotNull(message = "Entity Id should not be empty")
	@Column(name = "ent_id")
	private BigInteger entityId;

	@ManyToOne(targetEntity = Reports.class)
	@JoinColumn(name = "report_id")
	private Reports report;

	public Prompts() {
		super();
	}

	public Prompts(long id, @NotNull(message = "Prompt Key should not be empty") String promptKey,
			@NotNull(message = "Display name should not be empty") String displayName,
			@NotNull(message = "Prompt Required should not be empty") String promptRequired,
			@NotNull(message = "Prompt Order should not be empty") BigInteger promptOrder, BigInteger entityId,
			Reports report) {
		super();
		this.id = id;
		this.promptKey = promptKey;
		this.displayName = displayName;
		this.promptRequired = promptRequired;
		this.promptOrder = promptOrder;
		this.entityId = entityId;
		this.report = report;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public BigInteger getEntityId() {
		return entityId;
	}

	public void setEntityId(BigInteger entityId) {
		this.entityId = entityId;
	}

	public Reports getReport() {
		return report;
	}

	public void setReport(Reports report) {
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
				+ promptRequired + ", promptOrder=" + promptOrder + ", entityId=" + entityId + ", report=" + report
				+ "]";
	}

}
