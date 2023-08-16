package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.MatrixReportContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class MatrixPaymentReportServiceImpl extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(MatrixPaymentReportServiceImpl.class);
	private static final String FILENAME = "MatrixPaymentReportServiceImpl";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof AdvanceSearchReportInput advanceSearchReportInput) {
			return processMatrixPaymentReport(advanceSearchReportInput, reportContext);
		} else if (reportInput instanceof PaymentInvestigationReportInput paymentInvestigationReportInput) {
			return processPaymentInvestigationReport(paymentInvestigationReportInput, reportContext);
		}
		return null;
	}

	private List<? extends ReportOutput> processPaymentInvestigationReport(
			PaymentInvestigationReportInput paymentInvestigationReportInput, ReportContext reportContext) {
		List<PaymentInvestigationReportOutput> outputList = new ArrayList<PaymentInvestigationReportOutput>();
		ReportComponentDTO componentObj = paymentInvestigationReportInput.getComponent();
		if (null != componentObj) {
			Set<ReportComponentDetailDTO> componentDetailList = componentObj.getReportComponentDetails();
			if (!componentDetailList.isEmpty()) {
				MatrixReportContext matrixReportContext = paymentInvestigationReportInput.getMatrixReportContext();
				PaymentInvestigationReportOutput matrixPaymentAccStagingCoreRef = processComponentDetailForSingleRecord(
						componentDetailList, paymentInvestigationReportInput,
						MashreqFederatedReportConstants.MATRIX_PAYMENT_ACCOUNTING_STAGING_INTERNAL_CORE_REF_KEY,
						reportContext);
				if (matrixPaymentAccStagingCoreRef != null) {
					matrixReportContext.setRefFoundUsingCoreRef(matrixPaymentAccStagingCoreRef.getSourceRefNum());
					matrixReportContext.setDataFoundUsingCoreRef(true);
				}

				PaymentInvestigationReportOutput matrixPaymentTxnMstPay = processComponentDetailForSingleRecord(
						componentDetailList, paymentInvestigationReportInput,
						MashreqFederatedReportConstants.MATRIX_PAYMENT_TXN_MST_PAY_KEY, reportContext);

				if (matrixPaymentTxnMstPay != null) {
					matrixReportContext.setFileReferenceNum(matrixPaymentTxnMstPay.getSourceRefNum());
				}

				PaymentInvestigationReportOutput matrixPaymentPS080TB = processComponentDetailForSingleRecord(
						componentDetailList, paymentInvestigationReportInput,
						MashreqFederatedReportConstants.MATRIX_PAYMENT_PS0808TB_KEY, reportContext);

				PaymentInvestigationReportOutput matrixPaymentAccountingStagingInternal = processComponentDetailForSingleRecord(
						componentDetailList, paymentInvestigationReportInput,
						MashreqFederatedReportConstants.MATRIX_PAYMENT_ACCOUNTING_STAGING_INTERNAL_KEY, reportContext);
				if (matrixPaymentAccountingStagingInternal != null) {
					populateAccountingData(matrixPaymentAccountingStagingInternal, matrixPaymentPS080TB);
					outputList.add(matrixPaymentAccountingStagingInternal);
					paymentInvestigationReportInput
							.setCoreReferenceNum(matrixPaymentAccountingStagingInternal.getSourceRefNum());
					// analysze the manual/non-manual case
					analyzeAndMarkManualPayment(matrixReportContext);
				}
				// copy the pso80tb properties
				if (matrixPaymentPS080TB != null) {
					matrixReportContext.setPso80tbRecord(matrixPaymentPS080TB);
					paymentInvestigationReportInput.setGovCheck(matrixReportContext.getGovCheck());
					paymentInvestigationReportInput.setGovCheckReference(matrixReportContext.getGovCheckReference());
				}
			}
		}
		return outputList;
	}

	private void analyzeAndMarkManualPayment(MatrixReportContext matrixReportContext) {

		boolean isManualPayment = false;
		String stpFlag = matrixReportContext.getStpFlag();
		String txnStatus = matrixReportContext.getTxnStatus();
		String paymentMode = matrixReportContext.getPaymentMode();
		if (null != stpFlag && stpFlag.equalsIgnoreCase("N")) {
			if (null != txnStatus) {
				if (null != paymentMode) {
					if (paymentMode.equalsIgnoreCase("SR")
							&& (txnStatus.equalsIgnoreCase("F") || txnStatus.equalsIgnoreCase("S"))) {
						isManualPayment = true;
					}
				} else {
					if (txnStatus.equalsIgnoreCase("R")) {
						isManualPayment = true;
					}
				}
			}
		}
		matrixReportContext.setManualTransaction(isManualPayment);
	}

	private void populateAccountingData(PaymentInvestigationReportOutput accountingData,
			PaymentInvestigationReportOutput pso80tbData) {

		if (pso80tbData != null && accountingData != null) {
			accountingData.setReceiver(pso80tbData.getReceiver());
			accountingData.setBeneficaryDetail(pso80tbData.getBeneficaryDetail());
		}

	}

	private PaymentInvestigationReportOutput processComponentDetailForSingleRecord(
			Set<ReportComponentDetailDTO> componentDetailList,
			PaymentInvestigationReportInput paymentInvestigationReportInput, String componentDetailKey,
			ReportContext reportContext) {

		PaymentInvestigationReportOutput componentDetailOutput = null;
		ReportComponentDetailDTO matchedComponentDetail = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		if (matchedComponentDetail != null) {
			ReportComponentDetailContext context = populateReportComponentDetailContext(matchedComponentDetail,
					paymentInvestigationReportInput, reportContext);

			List<ReportDefaultOutput> outputList = queryExecutorService.executeQuery(matchedComponentDetail, context);

			componentDetailOutput = populateReportOutputForSingleRecord(outputList, componentDetailKey,
					paymentInvestigationReportInput.getMatrixReportContext());
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}
		return componentDetailOutput;

	}

	private PaymentInvestigationReportOutput populateReportOutputForSingleRecord(List<ReportDefaultOutput> outputList,
			String componentDetailKey, MatrixReportContext matrixReportContext) {

		PaymentInvestigationReportOutput reportOutput = null;
		if (componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.MATRIX_PAYMENT_PS0808TB_KEY)) {
			reportOutput = populatePaymentPSO80TB(outputList, matrixReportContext);
		} else if (componentDetailKey
				.equalsIgnoreCase(MashreqFederatedReportConstants.MATRIX_PAYMENT_ACCOUNTING_STAGING_INTERNAL_KEY)) {
			reportOutput = populatePaymentAccountingStagingInternal(outputList, matrixReportContext);
		} else if (componentDetailKey.equalsIgnoreCase(
				MashreqFederatedReportConstants.MATRIX_PAYMENT_ACCOUNTING_STAGING_INTERNAL_CORE_REF_KEY)) {
			reportOutput = populateDummyPaymentRecordToGetRefNum(outputList);
		} else if (componentDetailKey
				.equalsIgnoreCase(MashreqFederatedReportConstants.MATRIX_PAYMENT_TXN_MST_PAY_KEY)) {
			reportOutput = populateDummyPaymentRecordToGetRefNum(outputList);
		}
		return reportOutput;

	}

	private PaymentInvestigationReportOutput populateDummyPaymentRecordToGetRefNum(
			List<ReportDefaultOutput> reportOutputList) {
		PaymentInvestigationReportOutput reportOutput = null;
		if (!reportOutputList.isEmpty()) {
			ReportDefaultOutput defaultOutput = reportOutputList.get(0);
			List<Object> rowData = defaultOutput.getRowData();
			reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
			reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(0)));
		}
		return reportOutput;
	}

	private PaymentInvestigationReportOutput populatePaymentAccountingStagingInternal(
			List<ReportDefaultOutput> outputList, MatrixReportContext matrixReportContext) {
		PaymentInvestigationReportOutput consolidatedReportOutput = null;
		if (!outputList.isEmpty()) {
			Timestamp minTime = UtilityClass.getTimeStampRepresentation(outputList.get(0).getRowData().get(0));
			Timestamp maxTime = UtilityClass.getTimeStampRepresentation(outputList.get(0).getRowData().get(2));
			String debitAccount = null;
			for (ReportDefaultOutput defaultOutput : outputList) {
				List<Object> rowData = defaultOutput.getRowData();
				PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
				reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				Timestamp landingTime = UtilityClass.getTimeStampRepresentation(rowData.get(0));
				reportOutput.setLandingTime(landingTime);
				reportOutput.setActivity(UtilityClass.getStringRepresentation(rowData.get(1)));
				Timestamp completionTime = UtilityClass.getTimeStampRepresentation(rowData.get(2));
				reportOutput.setCompletionTime(completionTime);
				reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(3)));
				reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(4)));
				reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(5)));
				reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(6)));
				reportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(7)));
				reportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(11)));
				reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(12)));
				String coreReference = UtilityClass.getStringRepresentation(rowData.get(13));
				matrixReportContext.setCoreReferenceFoundFromAccStaging(coreReference);
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_MATRIX_PAYMENT);
				String accountNum = UtilityClass.getStringRepresentation(rowData.get(8));
				String debitCreditFlag = UtilityClass.getStringRepresentation(rowData.get(9));
				String trnCode = UtilityClass.getStringRepresentation(rowData.get(10));
				if (minTime.getTime() > landingTime.getTime()) {
					minTime = landingTime;
				}
				if (maxTime.getTime() < completionTime.getTime()) {
					maxTime = completionTime;
				}
				if (MashreqFederatedReportConstants.MATRIX_TRN_CODES_LIST.contains(trnCode)) {
					if (debitCreditFlag.equalsIgnoreCase(MashreqFederatedReportConstants.DEBIT_FLAG_VALUE)) {
						debitAccount = accountNum;
					} else {
						reportOutput.setBeneficaryAccount(accountNum);
						consolidatedReportOutput = reportOutput;
					}
				}
			}
			if (consolidatedReportOutput != null) {
				consolidatedReportOutput.setLandingTime(minTime);
				consolidatedReportOutput.setCompletionTime(maxTime);
				consolidatedReportOutput.setDebitAccount(debitAccount);
			}
		}
		return consolidatedReportOutput;
	}

	private PaymentInvestigationReportOutput populatePaymentPSO80TB(List<ReportDefaultOutput> outputList,
			MatrixReportContext matrixReportContext) {

		PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
		if (!outputList.isEmpty()) {
			ReportDefaultOutput defaultOutput = outputList.get(0);
			List<Object> rowData = defaultOutput.getRowData();
			reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
			reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(0)));
			reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(1)));
			reportOutput.setDebitAccount(UtilityClass.getStringRepresentation(rowData.get(2)));
			reportOutput.setReceiver(UtilityClass.getStringRepresentation(rowData.get(3)));
			reportOutput.setBeneficaryAccount(UtilityClass.getStringRepresentation(rowData.get(4)));
			reportOutput.setBeneficaryDetail(UtilityClass.getStringRepresentation(rowData.get(5)));
			matrixReportContext.setGovCheckReference(UtilityClass.getStringRepresentation(rowData.get(6)));
			matrixReportContext.setGovCheck(UtilityClass.getStringRepresentation(rowData.get(8)));
			matrixReportContext.setStpFlag(UtilityClass.getStringRepresentation(rowData.get(9)));
			matrixReportContext.setTxnStatus(UtilityClass.getStringRepresentation(rowData.get(10)));
			matrixReportContext.setPaymentMode(UtilityClass.getStringRepresentation(rowData.get(11)));
		}
		return reportOutput;

	}

	public List<? extends ReportOutput> processMatrixPaymentReport(AdvanceSearchReportInput advanceSearchReportInput,
			ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		ReportComponentDTO reportComponent = advanceSearchReportInput.getMatrixComponent();
		if (null != reportComponent) {
			advanceSearchReportInput.setMatrixComponent(reportComponent);
			Set<ReportComponentDetailDTO> componentDetailsSet = reportComponent.getReportComponentDetails();
			if (!componentDetailsSet.isEmpty()) {
				ReportComponentDetailDTO componentDetail = componentDetailsSet.iterator().next(); // take first as it
																									// gonna be single
																									// set
				if (null != componentDetail) {
					ReportComponentDetailContext context = new ReportComponentDetailContext();
					List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
					promptsList = populatePromptsForAdvanceSearch(advanceSearchReportInput);
					context.setQueryId(componentDetail.getId());
					context.setQueryKey(componentDetail.getQueryKey());
					context.setQueryString(componentDetail.getQuery());
					context.setExecutionId(reportContext.getExecutionId());
					context.setPrompts(promptsList);
					flexReportExecuteResponse = queryExecutorService.executeQuery(componentDetail, context);
					if (!flexReportExecuteResponse.isEmpty()) {
						advanceSearchReportOutputList = populateDataForAdvanceSearch(flexReportExecuteResponse,
								advanceSearchReportInput);
					}
				}

			}
		}
		return advanceSearchReportOutputList;
	}

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(
			List<ReportDefaultOutput> federatedReportOutputList, AdvanceSearchReportInput advanceSearchReportInput) {

		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();

		if (!federatedReportOutputList.isEmpty()) {

			for (ReportDefaultOutput federatedReportOutput : federatedReportOutputList) {

				AdvanceSearchReportOutput output = new AdvanceSearchReportOutput();
				List<Object> rowData = federatedReportOutput.getRowData();
				output.setTransactionReference(UtilityClass.getStringRepresentation(rowData.get(0)));
				output.setBeneficiaryDetails(UtilityClass.getStringRepresentation(rowData.get(1)));
				output.setValueDate(UtilityClass.getStringRepresentation(rowData.get(2)));
				output.setCurrency(UtilityClass.getStringRepresentation(rowData.get(3)));
				output.setAmount(UtilityClass.getStringRepresentation(rowData.get(4)));
				output.setStatus(UtilityClass.getStringRepresentation(rowData.get(5)));
				output.setMessageType(UtilityClass.getStringRepresentation(rowData.get(6)));
				output.setInitationSource(UtilityClass.getStringRepresentation(rowData.get(7)));
				output.setMessageThrough(UtilityClass.getStringRepresentation(rowData.get(8)));
				output.setAccountNum(UtilityClass.getStringRepresentation(rowData.get(9)));
				output.setRelatedAccount(UtilityClass.getStringRepresentation(rowData.get(10)));
				output.setInstrumentCode(UtilityClass.getStringRepresentation(rowData.get(11)));
				output.setExternalRefNum(UtilityClass.getStringRepresentation(rowData.get(12)));
				output.setCoreReferenceNum(UtilityClass.getStringRepresentation(rowData.get(13)));
				output.setTransactionDate(UtilityClass.getTimeStampRepresentation(rowData.get(14)));
				output.setProcessName(UtilityClass.getStringRepresentation(rowData.get(15)));
				output.setActivityName(UtilityClass.getStringRepresentation(rowData.get(16)));
				String rejectStatus = UtilityClass.getStringRepresentation(rowData.get(17));
				if (rejectStatus.equalsIgnoreCase(
						MashreqFederatedReportConstants.ADVANCE_SEARCH_REPORT_TRANSACTION_REJECT_STATUS)) {
					output.setStatus(rejectStatus);
				}
				advanceSearchReportOutputList.add(output);
			}
		}
		return advanceSearchReportOutputList;

	}

	public static List<FederatedReportPromptDTO> populatePromptsForAdvanceSearch(
			AdvanceSearchReportInput advanceSearchReportInput) {
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		prompts.add(advanceSearchReportInput.getAccountNumPrompt());
		prompts.add(advanceSearchReportInput.getAmountBetweenPrompt());
		prompts.add(advanceSearchReportInput.getAmountToPrompt());
		prompts.add(advanceSearchReportInput.getCurrencyPrompt());
		prompts.add(advanceSearchReportInput.getFromDatePrompt());
		prompts.add(advanceSearchReportInput.getToDatePrompt());
		prompts.add(advanceSearchReportInput.getTransactionRefNum());
		return prompts;
	}

}
