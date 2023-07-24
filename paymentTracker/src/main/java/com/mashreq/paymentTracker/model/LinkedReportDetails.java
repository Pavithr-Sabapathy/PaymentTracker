package com.mashreq.paymentTracker.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@DynamicUpdate
@Table(name = "conf_linked_report_info_det")
@NamedQueries({
		@NamedQuery(name = "LinkedReportDetails.findByLinkReportPromptId", query = "SELECT comp from Components comp join Report rep on comp.report = rep.id where rep.id =: reportId") })
public class LinkedReportDetails implements Serializable {
	private static final long serialVersionUID = -5922435091719149617L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "link_rep_id")
	private long linkReportId;

	@Column(name = "link_rpt_prompt_id")
	private long linkReportPromptId;

	@Column(name = "mapped_id")
	private long mappedId;

	@Column(name = "mapping_type")
	private String mappingType;

	public LinkedReportDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkedReportDetails(long id, long linkReportId, long linkReportPromptId, long mappedId, String mappingType) {
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

	public long getReportId() {
		return linkReportId;
	}

	public void setReportId(long linkReportId) {
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

	@Override
	public String toString() {
		return "LinkedReportDetails [id=" + id + ", linkReportId=" + linkReportId + ", linkReportPromptId="
				+ linkReportPromptId + ", mappedId=" + mappedId + ", mappingType=" + mappingType + "]";
	}

}
