package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReportProcessingRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4029704756392964215L;
	private String role;
	private long userId;
	private String userName;
	private List<PromptsProcessingRequest> prompts;
	private long linkInstanceId;
	private String linkReference;
	private Boolean isMapLinked = false;
	private Date createDate;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<PromptsProcessingRequest> getPrompts() {
		return prompts;
	}

	public void setPrompts(List<PromptsProcessingRequest> prompts) {
		this.prompts = prompts;
	}

	public long getLinkInstanceId() {
		return linkInstanceId;
	}

	public void setLinkInstanceId(long linkInstanceId) {
		this.linkInstanceId = linkInstanceId;
	}

	public String getLinkReference() {
		return linkReference;
	}

	public void setLinkReference(String linkReference) {
		this.linkReference = linkReference;
	}

	public Boolean getIsMapLinked() {
		return isMapLinked;
	}

	public void setIsMapLinked(Boolean isMapLinked) {
		this.isMapLinked = isMapLinked;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "ReportProcessingRequest [role=" + role + ", userId=" + userId + ", userName=" + userName
				+ ", promptsProcessingList=" + prompts + ", linkInstanceId=" + linkInstanceId + ", linkReference="
				+ linkReference + ", isMapLinked=" + isMapLinked + "]";
	}

}
