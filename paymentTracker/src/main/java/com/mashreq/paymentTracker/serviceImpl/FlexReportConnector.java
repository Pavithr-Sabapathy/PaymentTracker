package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexDetailedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class FlexReportConnector extends ReportConnector {

	@Autowired
	QueryExecutorService queryExecutorService;

	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof FlexDetailedReportInput) {
			FlexDetailedReportInput flexDetailedReport = (FlexDetailedReportInput) reportInput;
			return executeFlexReport(flexDetailedReport, reportContext);
		} else if (reportInput instanceof AdvanceSearchReportInput flexAdvanceSearchReportInput) {
			return processFlexAdvanceSearchReport(flexAdvanceSearchReportInput, reportContext);
		}
		return null;
	}

	private List<ReportDefaultOutput> executeFlexReport(
			FlexDetailedReportInput flexAccountingDetailedFederatedReportInput, ReportContext reportContext) {
		ReportComponentDetailDTO matchedComponentDetail = new ReportComponentDetailDTO();
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		ReportComponentDTO component = flexAccountingDetailedFederatedReportInput.getComponent();
		if (null != component) {
			Set<ReportComponentDetailDTO> componentDetailsSet = component.getReportComponentDetails();
			for (ReportComponentDetailDTO componentDetail : componentDetailsSet) {
				if (componentDetail.getQueryKey().toLowerCase().contains(flexAccountingDetailedFederatedReportInput
						.getAccountingSourcePrompt().getPromptValue().toLowerCase())) {
					matchedComponentDetail = componentDetail;
					matchedComponentDetail.setReportComponent(component);
					break;
				}
			}
			if (matchedComponentDetail != null) {

				ReportComponentDetailContext context = new ReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				context.setQueryId(matchedComponentDetail.getId());
				context.setQueryKey(matchedComponentDetail.getQueryKey());
				context.setQueryString(matchedComponentDetail.getQuery());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getReferenceNumPrompt());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getAccountingSourcePrompt());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getDebitAccountPrompt());
				context.setPrompts(promptsList);
				context.setExecutionId(reportContext.getExecutionId());

				flexReportExecuteResponse = queryExecutorService.executeQuery(matchedComponentDetail, context);

			}
		}
		return flexReportExecuteResponse;
	}

	private List<? extends ReportOutput> processFlexAdvanceSearchReport(
			AdvanceSearchReportInput advanceSearchReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		ReportComponentDTO reportComponent = advanceSearchReportInput.getFlexComponent();
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
		return advanceSearchReportOutputList;
	}

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(
			List<ReportDefaultOutput> flexReportExecuteResponse, AdvanceSearchReportInput advanceSearchReportInput) {
		List<AdvanceSearchReportOutput> reportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		if (!flexReportExecuteResponse.isEmpty()) {
			for (ReportDefaultOutput federatedReportOutput : flexReportExecuteResponse) {
				AdvanceSearchReportOutput advanceSearchReportOutput = new AdvanceSearchReportOutput();
				List<Object> rowData = federatedReportOutput.getRowData();
				advanceSearchReportOutput.setTransactionReference(UtilityClass.getStringRepresentation(rowData.get(0)));
				advanceSearchReportOutput.setBeneficiaryDetails(UtilityClass.getStringRepresentation(rowData.get(1)));
				advanceSearchReportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(2)));
				advanceSearchReportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(3)));
				advanceSearchReportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(4)));
				advanceSearchReportOutput.setStatus(UtilityClass.getStringRepresentation(rowData.get(5)));
				advanceSearchReportOutput.setMessageType(UtilityClass.getStringRepresentation(rowData.get(6)));
				advanceSearchReportOutput.setInitationSource(UtilityClass.getStringRepresentation(rowData.get(7)));
				advanceSearchReportOutput.setMessageThrough(UtilityClass.getStringRepresentation(rowData.get(8)));
				advanceSearchReportOutput.setAccountNum(UtilityClass.getStringRepresentation(rowData.get(9)));
				advanceSearchReportOutput.setRelatedAccount(UtilityClass.getStringRepresentation(rowData.get(10)));
				advanceSearchReportOutput.setInstrumentCode(UtilityClass.getStringRepresentation(rowData.get(11)));
				advanceSearchReportOutput.setExternalRefNum(UtilityClass.getStringRepresentation(rowData.get(12)));
				advanceSearchReportOutput.setCoreReferenceNum(UtilityClass.getStringRepresentation(rowData.get(13)));
				advanceSearchReportOutput.setTransactionDate(UtilityClass.getTimeStampRepresentation(rowData.get(14)));
				advanceSearchReportOutput.setProcessName(UtilityClass.getStringRepresentation(rowData.get(15)));
				advanceSearchReportOutput.setActivityName(UtilityClass.getStringRepresentation(rowData.get(16)));
				String rejectStatus = UtilityClass.getStringRepresentation(rowData.get(17));
				if (rejectStatus.equalsIgnoreCase(
						MashreqFederatedReportConstants.ADVANCE_SEARCH_REPORT_TRANSACTION_REJECT_STATUS)) {
					advanceSearchReportOutput.setStatus(rejectStatus);
				}
				reportOutputList.add(advanceSearchReportOutput);
			}
		}
		return reportOutputList;
	}

	private List<FederatedReportPromptDTO> populatePromptsForAdvanceSearch(
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