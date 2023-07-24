package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class LinkMappingResponseDTO implements Serializable {

	private static final long serialVersionUID = -6409248749358802554L;
	private long id;
	private String mappingType;
	private long mappedEntityId;
	private String mappedEntity;
	private long linkReportPromptId;
	
	public LinkMappingResponseDTO() {
		super();
	}
	public LinkMappingResponseDTO(long id, String mappingType, long mappedEntityId, String mappedEntity,
			long linkReportPromptId) {
		super();
		this.id = id;
		this.mappingType = mappingType;
		this.mappedEntityId = mappedEntityId;
		this.mappedEntity = mappedEntity;
		this.linkReportPromptId = linkReportPromptId;
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
	public long getMappedEntityId() {
		return mappedEntityId;
	}
	public void setMappedEntityId(long mappedEntityId) {
		this.mappedEntityId = mappedEntityId;
	}
	public String getMappedEntity() {
		return mappedEntity;
	}
	public void setMappedEntity(String mappedEntity) {
		this.mappedEntity = mappedEntity;
	}
	public long getLinkReportPromptId() {
		return linkReportPromptId;
	}
	public void setLinkReportPromptId(long linkReportPromptId) {
		this.linkReportPromptId = linkReportPromptId;
	}
	@Override
	public String toString() {
		return "LinkMappingResponseDTO [id=" + id + ", mappingType=" + mappingType + ", mappedEntityId="
				+ mappedEntityId + ", mappedEntity=" + mappedEntity + ", linkReportPromptId=" + linkReportPromptId
				+ "]";
	}

	
}