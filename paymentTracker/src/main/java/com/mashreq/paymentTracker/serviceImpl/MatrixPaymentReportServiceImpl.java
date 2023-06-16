package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.MatrixPaymentReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class MatrixPaymentReportServiceImpl implements MatrixPaymentReportService {

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;

	@Override
	public List<AdvanceSearchReportOutput> processMatrixPaymentReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<FederatedReportOutput> flexReportExecuteResponse = new ArrayList<FederatedReportOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		Components component = getMatchedInstanceComponent(componentList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_FLEX_COMPONENT_KEY);
		if (null != component) {
			ReportComponentDTO reportComponent = populateReportComponent(component);
			advanceSearchReportInput.setMatrixComponent(reportComponent);
			Set<ReportComponentDetailDTO> componentDetailsSet = reportComponent.getReportComponentDetails();
			if (!componentDetailsSet.isEmpty()) {
				ReportComponentDetailDTO componentDetail = componentDetailsSet.iterator().next(); // take first as it
																									// gonna be single
																									// set
				if (null != componentDetail) {
					FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
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
			List<FederatedReportOutput> federatedReportOutputList, AdvanceSearchReportInput advanceSearchReportInput) {

		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();

		if (!federatedReportOutputList.isEmpty()) {

			for (FederatedReportOutput federatedReportOutput : federatedReportOutputList) {

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

	private Components getMatchedInstanceComponent(List<Components> componentList, String componentKey) {
		Optional<Components> componentOptional = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)
						&& component.getActive().equalsIgnoreCase(MashreqFederatedReportConstants.YES))
				.findFirst();
		Components component = componentOptional.get();
		return component;

	}

	private ReportComponentDTO populateReportComponent(Components component) {
		ReportComponentDTO reportComponentDTO = new ReportComponentDTO();
		reportComponentDTO.setActive(CheckType.getCheckType(component.getActive()));
		reportComponentDTO.setComponentKey(component.getComponentKey());
		reportComponentDTO.setComponentName(component.getComponentName());
		reportComponentDTO.setId(component.getId());
		reportComponentDTO.setReportComponentDetails(populateComponentDetails(component.getComponentDetailsList()));
		return reportComponentDTO;
	}

	private Set<ReportComponentDetailDTO> populateComponentDetails(List<ComponentDetails> componentDetailsList) {
		Set<ReportComponentDetailDTO> componentDetailDTO = new HashSet<ReportComponentDetailDTO>();
		componentDetailsList.stream().forEach(componentDetails -> {
			ReportComponentDetailDTO reportComponentDetailDTO = new ReportComponentDetailDTO();
			reportComponentDetailDTO.setId(componentDetails.getId());
			reportComponentDetailDTO.setQuery(componentDetails.getQuery());
			reportComponentDetailDTO.setQueryKey(componentDetails.getQueryKey());
			reportComponentDetailDTO.setReportComponentId(componentDetails.getComponents().getId());
			componentDetailDTO.add(reportComponentDetailDTO);
		});
		return componentDetailDTO;
	}
}
