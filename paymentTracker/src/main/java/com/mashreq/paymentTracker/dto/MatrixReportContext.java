package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportInput;

public class MatrixReportContext implements ReportInput {

	private PaymentInvestigationReportOutput pso80tbRecord;
	private String fileReferenceNum;
	private boolean matrixDataFound;
	private boolean manualTransaction = false;
	private boolean dataFoundUsingCoreRef;
	private String refFoundUsingCoreRef;
	private String coreReferenceFoundFromAccStaging;
	private String govCheckReference;
	private String govCheck;
	private String stpFlag;
	private String txnStatus;
	private String paymentMode;

	public PaymentInvestigationReportOutput getPso80tbRecord() {
		return pso80tbRecord;
	}

	public void setPso80tbRecord(PaymentInvestigationReportOutput pso80tbRecord) {
		this.pso80tbRecord = pso80tbRecord;
	}

	public String getFileReferenceNum() {
		return fileReferenceNum;
	}

	public void setFileReferenceNum(String fileReferenceNum) {
		this.fileReferenceNum = fileReferenceNum;
	}

	public boolean isMatrixDataFound() {
		return matrixDataFound;
	}

	public void setMatrixDataFound(boolean matrixDataFound) {
		this.matrixDataFound = matrixDataFound;
	}

	public boolean isManualTransaction() {
		return manualTransaction;
	}

	public void setManualTransaction(boolean manualTransaction) {
		this.manualTransaction = manualTransaction;
	}

	public boolean isDataFoundUsingCoreRef() {
		return dataFoundUsingCoreRef;
	}

	public void setDataFoundUsingCoreRef(boolean dataFoundUsingCoreRef) {
		this.dataFoundUsingCoreRef = dataFoundUsingCoreRef;
	}

	public String getRefFoundUsingCoreRef() {
		return refFoundUsingCoreRef;
	}

	public void setRefFoundUsingCoreRef(String refFoundUsingCoreRef) {
		this.refFoundUsingCoreRef = refFoundUsingCoreRef;
	}

	public String getCoreReferenceFoundFromAccStaging() {
		return coreReferenceFoundFromAccStaging;
	}

	public void setCoreReferenceFoundFromAccStaging(String coreReferenceFoundFromAccStaging) {
		this.coreReferenceFoundFromAccStaging = coreReferenceFoundFromAccStaging;
	}

	public String getGovCheckReference() {
		return govCheckReference;
	}

	public void setGovCheckReference(String govCheckReference) {
		this.govCheckReference = govCheckReference;
	}

	public String getGovCheck() {
		return govCheck;
	}

	public void setGovCheck(String govCheck) {
		this.govCheck = govCheck;
	}

	public String getStpFlag() {
		return stpFlag;
	}

	public void setStpFlag(String stpFlag) {
		this.stpFlag = stpFlag;
	}

	public String getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

}
