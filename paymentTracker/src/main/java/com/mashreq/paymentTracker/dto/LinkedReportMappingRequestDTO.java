package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class LinkedReportMappingRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1694636520911516297L;
	private long id;
	private long linkReportId;
	private long linkReportPromptId;
	private long mappedId;
	private String mappingType;

	public LinkedReportMappingRequestDTO() {
		super();
	}

	public LinkedReportMappingRequestDTO(long id, long linkReportId, long linkReportPromptId, long mappedId,
			String mappingType) {
		super();
		this.id = id;
		this.linkReportId = linkReportId;
		this.linkReportPromptId = linkReportPromptId;
		this.mappedId = mappedId;
		this.mappingType = mappingType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLinkReportId() {
		return linkReportId;
	}

	public void setLinkReportId(long linkReportId) {
		this.linkReportId = linkReportId;
	}

	public long getLinkReportPromptId() {
		return linkReportPromptId;
	}

	public void setLinkReportPromptId(long linkReportPromptId) {
		this.linkReportPromptId = linkReportPromptId;
	}

	public long getMappedId() {
		return mappedId;
	}

	public void setMappedId(long mappedId) {
		this.mappedId = mappedId;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

}