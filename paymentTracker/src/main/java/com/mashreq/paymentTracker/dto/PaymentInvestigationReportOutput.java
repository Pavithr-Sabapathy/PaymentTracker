package com.mashreq.paymentTracker.dto;

import java.sql.Timestamp;

import com.mashreq.paymentTracker.utility.SWIFTDetailedReportType;

public class PaymentInvestigationReportOutput extends ReportBaseOutput {

	private Timestamp landingTime;
	private String activity;
	private Timestamp completionTime;
	private Long duration = 0L;
	private String activityStatus;
	private String source;
	private String sourceRefNum;
	private String currency;
	private String amount;
	private String valueDate;
	private String debitAccount;
	private String receiver;
	private String beneficaryAccount;
	private String beneficaryDetail;
	private String workstage;
	private String completedBy;
	private Timestamp pickupTime;
	// output for track and trace
	private Long cummulativeTime;
	private Long waitTime;
	// non-displayable metrics
	private String mesgType; // uaefts (103,202 etc)
	private String accountingSource; // flex accounting (core/matrix)
	private String detectionId; // safewatch (for quick and direct reference)
	private SWIFTDetailedReportType detailedReportType; // this is for swift
	private String aid; // this is for swift
	private String umidl; // this is for swift
	private String umidh; // this is for swift
	private String emailUrl;
	private String messageSubFormat;
	private transient String govCheck;
	private transient String govCheckReference;
	private transient String customerId;
	private Long componentDetailId;
	
	public Timestamp getLandingTime() {
		return landingTime;
	}
	public void setLandingTime(Timestamp landingTime) {
		this.landingTime = landingTime;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public Timestamp getCompletionTime() {
		return completionTime;
	}
	public void setCompletionTime(Timestamp completionTime) {
		this.completionTime = completionTime;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceRefNum() {
		return sourceRefNum;
	}
	public void setSourceRefNum(String sourceRefNum) {
		this.sourceRefNum = sourceRefNum;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getValueDate() {
		return valueDate;
	}
	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}
	public String getDebitAccount() {
		return debitAccount;
	}
	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getBeneficaryAccount() {
		return beneficaryAccount;
	}
	public void setBeneficaryAccount(String beneficaryAccount) {
		this.beneficaryAccount = beneficaryAccount;
	}
	public String getBeneficaryDetail() {
		return beneficaryDetail;
	}
	public void setBeneficaryDetail(String beneficaryDetail) {
		this.beneficaryDetail = beneficaryDetail;
	}
	public String getWorkstage() {
		return workstage;
	}
	public void setWorkstage(String workstage) {
		this.workstage = workstage;
	}
	public String getCompletedBy() {
		return completedBy;
	}
	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}
	public Timestamp getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(Timestamp pickupTime) {
		this.pickupTime = pickupTime;
	}
	public Long getCummulativeTime() {
		return cummulativeTime;
	}
	public void setCummulativeTime(Long cummulativeTime) {
		this.cummulativeTime = cummulativeTime;
	}
	public Long getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(Long waitTime) {
		this.waitTime = waitTime;
	}
	public String getMesgType() {
		return mesgType;
	}
	public void setMesgType(String mesgType) {
		this.mesgType = mesgType;
	}
	public String getAccountingSource() {
		return accountingSource;
	}
	public void setAccountingSource(String accountingSource) {
		this.accountingSource = accountingSource;
	}
	public String getDetectionId() {
		return detectionId;
	}
	public void setDetectionId(String detectionId) {
		this.detectionId = detectionId;
	}
	public SWIFTDetailedReportType getDetailedReportType() {
		return detailedReportType;
	}
	public void setDetailedReportType(SWIFTDetailedReportType detailedReportType) {
		this.detailedReportType = detailedReportType;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getUmidl() {
		return umidl;
	}
	public void setUmidl(String umidl) {
		this.umidl = umidl;
	}
	public String getUmidh() {
		return umidh;
	}
	public void setUmidh(String umidh) {
		this.umidh = umidh;
	}
	public String getEmailUrl() {
		return emailUrl;
	}
	public void setEmailUrl(String emailUrl) {
		this.emailUrl = emailUrl;
	}
	public String getMessageSubFormat() {
		return messageSubFormat;
	}
	public void setMessageSubFormat(String messageSubFormat) {
		this.messageSubFormat = messageSubFormat;
	}
	public String getGovCheck() {
		return govCheck;
	}
	public void setGovCheck(String govCheck) {
		this.govCheck = govCheck;
	}
	public String getGovCheckReference() {
		return govCheckReference;
	}
	public void setGovCheckReference(String govCheckReference) {
		this.govCheckReference = govCheckReference;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public Long getComponentDetailId() {
		return componentDetailId;
	}
	public void setComponentDetailId(Long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}
	@Override
	public String toString() {
		return "PaymentInvestigationReportOutput [landingTime=" + landingTime + ", activity=" + activity
				+ ", completionTime=" + completionTime + ", duration=" + duration + ", activityStatus=" + activityStatus
				+ ", source=" + source + ", sourceRefNum=" + sourceRefNum + ", currency=" + currency + ", amount="
				+ amount + ", valueDate=" + valueDate + ", debitAccount=" + debitAccount + ", receiver=" + receiver
				+ ", beneficaryAccount=" + beneficaryAccount + ", beneficaryDetail=" + beneficaryDetail + ", workstage="
				+ workstage + ", completedBy=" + completedBy + ", pickupTime=" + pickupTime + ", cummulativeTime="
				+ cummulativeTime + ", waitTime=" + waitTime + ", mesgType=" + mesgType + ", accountingSource="
				+ accountingSource + ", detectionId=" + detectionId + ", detailedReportType=" + detailedReportType
				+ ", aid=" + aid + ", umidl=" + umidl + ", umidh=" + umidh + ", emailUrl=" + emailUrl
				+ ", messageSubFormat=" + messageSubFormat + "]";
	}

}
