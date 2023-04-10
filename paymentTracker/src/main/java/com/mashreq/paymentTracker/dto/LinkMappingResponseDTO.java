package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class LinkMappingResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6409248749358802554L;
	private long id;
	private String mappingType;
	private long mappedEnitytId;
	private String mappedEntity;
	private long linkReportPromptId;
	private String linkReportPrompts;

	public LinkMappingResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkMappingResponseDTO(long id, String mappingType, long mappedEnitytId, String mappedEntity,
			long linkReportPromptId, String linkReportPrompts) {
		super();
		this.id = id;
		this.mappingType = mappingType;
		this.mappedEnitytId = mappedEnitytId;
		this.mappedEntity = mappedEntity;
		this.linkReportPromptId = linkReportPromptId;
		this.linkReportPrompts = linkReportPrompts;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	public String getMappedEntity() {
		return mappedEntity;
	}

	public void setMappedEntity(String mappedEntity) {
		this.mappedEntity = mappedEntity;
	}

	public String getLinkReportPrompts() {
		return linkReportPrompts;
	}

	public void setLinkReportPrompts(String linkReportPrompts) {
		this.linkReportPrompts = linkReportPrompts;
	}

	public long getMappedEnitytId() {
		return mappedEnitytId;
	}

	public void setMappedEnitytId(long mappedEnitytId) {
		this.mappedEnitytId = mappedEnitytId;
	}

	public long getLinkReportPromptId() {
		return linkReportPromptId;
	}

	public void setLinkReportPromptId(long linkReportPromptId) {
		this.linkReportPromptId = linkReportPromptId;
	}

	@Override
	public String toString() {
		return "LinkMappingResponseDTO [id=" + id + ", mappingType=" + mappingType + ", mappedEnitytId="
				+ mappedEnitytId + ", mappedEntity=" + mappedEntity + ", linkReportPromptId=" + linkReportPromptId
				+ ", linkReportPrompts=" + linkReportPrompts + "]";
	}

}