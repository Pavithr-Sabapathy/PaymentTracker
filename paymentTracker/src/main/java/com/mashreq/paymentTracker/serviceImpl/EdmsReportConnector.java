package com.mashreq.paymentTracker.serviceImpl;

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
public class EdmsReportConnector extends ReportConnector {
	
	private static final Logger log = LoggerFactory.getLogger(EdmsReportConnector.class);
	private static final String FILENAME = "EdmsReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof AdvanceSearchReportInput advanceSearchInput) {
			return processEdmsAdvanceSearchReport(advanceSearchInput, reportContext);
		}
		return null;
	}

	private List<? extends ReportOutput> processEdmsAdvanceSearchReport(
			AdvanceSearchReportInput advanceSearchReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		ReportComponentDTO reportComponent = advanceSearchReportInput.getEdmsComponent();
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
		log.info(FILENAME + "processEdmsReport [Response] -->" + advanceSearchReportOutputList.toString());
		return advanceSearchReportOutputList;
	}

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(List<ReportDefaultOutput> ReportOutputList,
			AdvanceSearchReportInput advanceSearchReportInput) {

		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		if (!ReportOutputList.isEmpty()) {
			for (ReportDefaultOutput ReportOutput : ReportOutputList) {
				AdvanceSearchReportOutput output = new AdvanceSearchReportOutput();
				List<Object> rowData = ReportOutput.getRowData();
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