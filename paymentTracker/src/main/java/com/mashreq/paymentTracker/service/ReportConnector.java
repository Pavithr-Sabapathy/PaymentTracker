package com.mashreq.paymentTracker.service;

import java.util.ArrayList;
import java.util.List;

import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;

public abstract class ReportConnector {

	public abstract List<? extends ReportOutput> processReportComponent(ReportInput reportInput,
			ReportContext reportContext);

	public ReportComponentDetailContext populateReportComponentDetailContext(ReportComponentDetailDTO componentDetail,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		referenceNumPrompt.setPromptValue(piReportInput.getUserReferenceNum());
		promptInfoList.add(referenceNumPrompt);
		promptInfoList.add(piReportInput.getCountryCodePrompt());
		promptInfoList.add(piReportInput.getTimeInDaysPrompt());
		context.setQueryString(componentDetail.getQuery());
		context.setPrompts(promptInfoList);
		context.setExecutionId(reportContext.getExecutionId());
		return context;
	}

	public PaymentInvestigationReportOutput clonePaymentInvestigationReportOutput(
			PaymentInvestigationReportOutput toBeCloned) {
		// TODO Auto-generated method stub
		PaymentInvestigationReportOutput piReportOutput = new PaymentInvestigationReportOutput();
		piReportOutput.setComponentDetailId(toBeCloned.getComponentDetailId());
		piReportOutput.setActivityStatus(toBeCloned.getActivityStatus());
		piReportOutput.setAmount(toBeCloned.getAmount());
		piReportOutput.setBeneficaryAccount(toBeCloned.getBeneficaryAccount());
		piReportOutput.setBeneficaryDetail(toBeCloned.getBeneficaryDetail());
		piReportOutput.setCompletedBy(toBeCloned.getCompletedBy());
		piReportOutput.setCompletionTime(toBeCloned.getCompletionTime());
		piReportOutput.setCurrency(toBeCloned.getCurrency());
		piReportOutput.setDebitAccount(toBeCloned.getDebitAccount());
		piReportOutput.setLandingTime(toBeCloned.getLandingTime());
		piReportOutput.setReceiver(toBeCloned.getReceiver());
		piReportOutput.setSource(toBeCloned.getSource());
		piReportOutput.setSourceRefNum(toBeCloned.getSourceRefNum());
		piReportOutput.setValueDate(toBeCloned.getValueDate());
		piReportOutput.setWorkstage(toBeCloned.getWorkstage());
		piReportOutput.setMesgType(toBeCloned.getMesgType());
		piReportOutput.setDetectionId(toBeCloned.getDetectionId());
		piReportOutput.setAccountingSource(toBeCloned.getAccountingSource());
		if (toBeCloned.getDetailedReportType() != null) {
			piReportOutput.setDetailedReportType(toBeCloned.getDetailedReportType());
		}
		piReportOutput.setAid(toBeCloned.getAid());
		piReportOutput.setUmidh(toBeCloned.getUmidh());
		piReportOutput.setUmidl(toBeCloned.getUmidl());
		piReportOutput.setEmailUrl(toBeCloned.getEmailUrl());
		piReportOutput.setMessageSubFormat(toBeCloned.getMessageSubFormat());
		piReportOutput.setGovCheck(toBeCloned.getGovCheck());
		piReportOutput.setGovCheckReference(toBeCloned.getGovCheckReference());
		return piReportOutput;

	}
}