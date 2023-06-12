package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class PromptInstance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9198439413368119873L;
	private Long id;
	private String key;
	private String promptValue;
	private List<String> value;
	private String name;
	private String order;
	private Boolean required;
	private EntityDTO entity;
	private Long reportId;
	private Long entityId;
	private ReportInstanceDTO reportVO;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPromptValue() {
		return promptValue;
	}

	public void setPromptValue(String promptValue) {
		this.promptValue = promptValue;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public EntityDTO getEntity() {
		return entity;
	}

	public void setEntity(EntityDTO entity) {
		this.entity = entity;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public ReportInstanceDTO getReportVO() {
		return reportVO;
	}

	public void setReportVO(ReportInstanceDTO reportVO) {
		this.reportVO = reportVO;
	}

	@Override
	public String toString() {
		return "promptInstance [id=" + id + ", key=" + key + ", promptValue=" + promptValue + ", value=" + value
				+ ", name=" + name + ", order=" + order + ", required=" + required + ", entity=" + entity
				+ ", reportId=" + reportId + ", entityId=" + entityId + ", reportVO=" + reportVO + "]";
	}

}
