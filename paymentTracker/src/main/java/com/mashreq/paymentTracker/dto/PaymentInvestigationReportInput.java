package com.mashreq.paymentTracker.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.type.EDMSProcessType;
import com.mashreq.paymentTracker.type.PaymentType;

public class PaymentInvestigationReportInput implements ReportInput {
	
	private ReportComponentDTO component;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO countryCodePrompt;
	private FederatedReportPromptDTO timeInDaysPrompt;
	private String userReferenceNum;
	private String roleName;
	private String originalUserReferenceNum;
	private List<PaymentInvestigationReportOutput> failedSystemOutputs = new ArrayList<PaymentInvestigationReportOutput>();
	private boolean trackAndTraceVersion;

	private String sourceReferenceNum;
	private String sourceType;
	private String coreReferenceNum;
	private PaymentType paymentType = PaymentType.OUTWARD;
	private boolean coreRecordFound = false;
	private boolean coreRecordFoundUsingCoreRef = false;
	private boolean coreRecordFoundUsingSourceRef = false;
	// this is required to stop processing other channels
	private boolean channelDataFound = false;
	private boolean processOnlyFlexAccountingBasedOnDebitAccount = false;
	private EDMSProcessType edmsProcessType = EDMSProcessType.FTO;
	private GatewayDataContext gatewayDataContext;
	private MatrixReportContext matrixReportContext = new MatrixReportContext();
	private Set<String> referenceList;

	private String govCheck = MashreqFederatedReportConstants.NO;
	private String govCheckReference;

	private PaymentInvestigationReportOutput molRecord;

	public ReportComponentDTO getComponent() {
		return component;
	}

	public void setComponent(ReportComponentDTO component) {
		this.component = component;
	}

	public FederatedReportPromptDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}

	public void setReferenceNumPrompt(FederatedReportPromptDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public FederatedReportPromptDTO getCountryCodePrompt() {
		return countryCodePrompt;
	}

	public void setCountryCodePrompt(FederatedReportPromptDTO countryCodePrompt) {
		this.countryCodePrompt = countryCodePrompt;
	}

	public FederatedReportPromptDTO getTimeInDaysPrompt() {
		return timeInDaysPrompt;
	}

	public void setTimeInDaysPrompt(FederatedReportPromptDTO timeInDaysPrompt) {
		this.timeInDaysPrompt = timeInDaysPrompt;
	}

	public String getUserReferenceNum() {
		return userReferenceNum;
	}

	public void setUserReferenceNum(String userReferenceNum) {
		this.userReferenceNum = userReferenceNum;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getOriginalUserReferenceNum() {
		return originalUserReferenceNum;
	}

	public void setOriginalUserReferenceNum(String originalUserReferenceNum) {
		this.originalUserReferenceNum = originalUserReferenceNum;
	}

	public List<PaymentInvestigationReportOutput> getFailedSystemOutputs() {
		return failedSystemOutputs;
	}

	public void setFailedSystemOutputs(List<PaymentInvestigationReportOutput> failedSystemOutputs) {
		this.failedSystemOutputs = failedSystemOutputs;
	}

	public boolean isTrackAndTraceVersion() {
		return trackAndTraceVersion;
	}

	public void setTrackAndTraceVersion(boolean trackAndTraceVersion) {
		this.trackAndTraceVersion = trackAndTraceVersion;
	}

	public String getSourceReferenceNum() {
		return sourceReferenceNum;
	}

	public void setSourceReferenceNum(String sourceReferenceNum) {
		this.sourceReferenceNum = sourceReferenceNum;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getCoreReferenceNum() {
		return coreReferenceNum;
	}

	public void setCoreReferenceNum(String coreReferenceNum) {
		this.coreReferenceNum = coreReferenceNum;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isCoreRecordFound() {
		return coreRecordFound;
	}

	public void setCoreRecordFound(boolean coreRecordFound) {
		this.coreRecordFound = coreRecordFound;
	}

	public boolean isCoreRecordFoundUsingCoreRef() {
		return coreRecordFoundUsingCoreRef;
	}

	public void setCoreRecordFoundUsingCoreRef(boolean coreRecordFoundUsingCoreRef) {
		this.coreRecordFoundUsingCoreRef = coreRecordFoundUsingCoreRef;
	}

	public boolean isCoreRecordFoundUsingSourceRef() {
		return coreRecordFoundUsingSourceRef;
	}

	public void setCoreRecordFoundUsingSourceRef(boolean coreRecordFoundUsingSourceRef) {
		this.coreRecordFoundUsingSourceRef = coreRecordFoundUsingSourceRef;
	}

	public boolean isChannelDataFound() {
		return channelDataFound;
	}

	public void setChannelDataFound(boolean channelDataFound) {
		this.channelDataFound = channelDataFound;
	}

	public boolean isProcessOnlyFlexAccountingBasedOnDebitAccount() {
		return processOnlyFlexAccountingBasedOnDebitAccount;
	}

	public void setProcessOnlyFlexAccountingBasedOnDebitAccount(boolean processOnlyFlexAccountingBasedOnDebitAccount) {
		this.processOnlyFlexAccountingBasedOnDebitAccount = processOnlyFlexAccountingBasedOnDebitAccount;
	}

	public EDMSProcessType getEdmsProcessType() {
		return edmsProcessType;
	}

	public void setEdmsProcessType(EDMSProcessType edmsProcessType) {
		this.edmsProcessType = edmsProcessType;
	}

	public GatewayDataContext getGatewayDataContext() {
		return gatewayDataContext;
	}

	public void setGatewayDataContext(GatewayDataContext gatewayDataContext) {
		this.gatewayDataContext = gatewayDataContext;
	}

	public MatrixReportContext getMatrixReportContext() {
		return matrixReportContext;
	}

	public void setMatrixReportContext(MatrixReportContext matrixReportContext) {
		this.matrixReportContext = matrixReportContext;
	}

	public Set<String> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(Set<String> referenceList) {
		this.referenceList = referenceList;
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

	public PaymentInvestigationReportOutput getMolRecord() {
		return molRecord;
	}

	public void setMolRecord(PaymentInvestigationReportOutput molRecord) {
		this.molRecord = molRecord;
	}

	@Override
	public String toString() {
		return "PaymentInvestigationReportInput [component=" + component + ", referenceNumPrompt=" + referenceNumPrompt
				+ ", countryCodePrompt=" + countryCodePrompt + ", timeInDaysPrompt=" + timeInDaysPrompt
				+ ", userReferenceNum=" + userReferenceNum + ", roleName=" + roleName + ", originalUserReferenceNum="
				+ originalUserReferenceNum + ", failedSystemOutputs=" + failedSystemOutputs + ", trackAndTraceVersion="
				+ trackAndTraceVersion + ", sourceReferenceNum=" + sourceReferenceNum + ", sourceType=" + sourceType
				+ ", coreReferenceNum=" + coreReferenceNum + ", paymentType=" + paymentType + ", coreRecordFound="
				+ coreRecordFound + ", coreRecordFoundUsingCoreRef=" + coreRecordFoundUsingCoreRef
				+ ", coreRecordFoundUsingSourceRef=" + coreRecordFoundUsingSourceRef + ", channelDataFound="
				+ channelDataFound + ", processOnlyFlexAccountingBasedOnDebitAccount="
				+ processOnlyFlexAccountingBasedOnDebitAccount + ", edmsProcessType=" + edmsProcessType
				+ ", gatewayDataContext=" + gatewayDataContext + ", matrixReportContext=" + matrixReportContext
				+ ", referenceList=" + referenceList + ", govCheck=" + govCheck + ", govCheckReference="
				+ govCheckReference + ", molRecord=" + molRecord + "]";
	}

}
