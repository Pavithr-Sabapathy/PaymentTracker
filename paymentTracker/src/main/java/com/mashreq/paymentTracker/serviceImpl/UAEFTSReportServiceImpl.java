package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.UAEFTSReportService;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

public class UAEFTSReportServiceImpl implements UAEFTSReportService {

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<AdvanceSearchReportOutput> processUAEFTSReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<FederatedReportOutput> flexReportExecuteResponse = new ArrayList<FederatedReportOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		Components component = getMatchedInstanceComponent(componentList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_UAEFTS_COMPONENT_KEY);
		if (null != component) {
			ReportComponentDTO reportComponent = populateReportComponent(component);
			advanceSearchReportInput.setUaeftsComponent(reportComponent);
			Set<ReportComponentDetailDTO> componentDetailsSet = reportComponent.getReportComponentDetails();
			if (!componentDetailsSet.isEmpty()) {
				ReportComponentDetailDTO componentDetail = componentDetailsSet.stream()
						.filter(componentDetails -> componentDetails.getQueryKey()
								.equalsIgnoreCase(MashreqFederatedReportConstants.ADVANCE_SEARCH_UAEFTS_CCN_KEY))
						.findAny().orElse(null);

				if (null != componentDetail) {
					FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
					List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
					FederatedReportPromptDTO referenceNumsPrompt = new FederatedReportPromptDTO();
					referenceNumsPrompt.setPromptKey(MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
					String promptValue = advanceSearchReportInput.getFlexMatrixBasedUaeftsTransactions().keySet()
							.stream().collect(Collectors.joining(","));
					referenceNumsPrompt.setPromptValue(promptValue);
					referenceNumsPrompt.setValueType(PromptValueType.VALUE);
					promptsList.add(referenceNumsPrompt);

					context.setQueryId(componentDetail.getId());
					context.setQueryKey(componentDetail.getQueryKey());
					context.setQueryString(componentDetail.getQuery());
					context.setExecutionId(reportContext.getExecutionId());
					context.setPrompts(promptsList);
					flexReportExecuteResponse = queryExecutorService.executeQuery(componentDetail, context);
					if (!flexReportExecuteResponse.isEmpty()) {
						AdvanceSearchReportOutput advanceSearchReportOutput = populateDataForAdvanceSearch(
								flexReportExecuteResponse, advanceSearchReportInput);
						advanceSearchReportOutputList.add(advanceSearchReportOutput);
					}
				}

			}
		}
		return advanceSearchReportOutputList;
	}

	private AdvanceSearchReportOutput populateDataForAdvanceSearch(
			List<FederatedReportOutput> flexReportExecuteResponse, AdvanceSearchReportInput advanceSearchReportInput) {
		AdvanceSearchReportOutput advanceSearchFederatedReportOutput = new AdvanceSearchReportOutput();
		Map<String, AdvanceSearchReportOutput> flexMatrixBasedUaeftsTransactions = advanceSearchReportInput
				.getFlexMatrixBasedUaeftsTransactions();
		if (!flexReportExecuteResponse.isEmpty()) {
			for (FederatedReportOutput federatedReportOutput : flexReportExecuteResponse) {
				List<Object> rowData = federatedReportOutput.getRowData();
				String referenceNum = UtilityClass.getStringRepresentation(rowData.get(0));
				String ftsStatus = UtilityClass.getStringRepresentation(rowData.get(2));
				String creditConfirmedStatus = UtilityClass.getStringRepresentation(rowData.get(3));
				advanceSearchFederatedReportOutput = flexMatrixBasedUaeftsTransactions.get(referenceNum);
				if (advanceSearchFederatedReportOutput != null) {
					advanceSearchFederatedReportOutput.setStatus(deriveFinalStatus(ftsStatus, creditConfirmedStatus));
				}
			}
		}
		return advanceSearchFederatedReportOutput;
	}

	private String deriveFinalStatus(String ftsStatus, String creditConfirmedStatus) {
		String finalStatus = ftsStatus;
		if (MashreqFederatedReportConstants.ADVANCE_SEARCH_REPORT_TRANSACTION_CREDIT_CONFIRMED_STATUS
				.equalsIgnoreCase(creditConfirmedStatus)) {
			finalStatus = creditConfirmedStatus;
		}
		return finalStatus;
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
