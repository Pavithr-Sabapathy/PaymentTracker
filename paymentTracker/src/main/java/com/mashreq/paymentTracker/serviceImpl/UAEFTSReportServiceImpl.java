package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.UAEFTSDetailedReportInput;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.UAEFTSReportService;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class UAEFTSReportServiceImpl implements UAEFTSReportService{

	private static final Logger log = LoggerFactory.getLogger(UAEFTSReportServiceImpl.class);
	private static final String FILENAME = "UAEFTSReportServiceImpl";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	LinkReportService linkReportService;

	@Override
	public List<AdvanceSearchReportOutput> processAdvanceSearchReport(
			AdvanceSearchReportInput advanceSearchReportInput, List<Components> componentList,
			ReportContext reportContext) {
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

	@Override
	public ReportExecuteResponseData processUAEFTSReport(ReportInstanceDTO reportInstanceDTO, ReportContext reportContext) {
		ReportExecuteResponseData reportExecuteResponseData = new ReportExecuteResponseData();
		List<FederatedReportOutput> UAEFTSReportOutputList = new ArrayList<FederatedReportOutput>();
		ReportComponentDetailDTO matchedComponentDetail = new ReportComponentDetailDTO();
		Report report = new Report();
		if (null != reportInstanceDTO) {
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(report.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			List<Components> componentList = componentsOptional.get();
			UAEFTSDetailedReportInput UAEFTSDetailedReportInput = populateBaseInputContext(
					reportInstanceDTO.getPromptsList());
			if (!componentList.isEmpty()) {
				for (Components component : componentList) {
					ReportComponentDTO reportComponent = populateReportComponent(component);
					UAEFTSDetailedReportInput.setComponent(reportComponent);

					matchedComponentDetail = reportComponent.getReportComponentDetails().stream()
							.filter(componentDetail -> componentDetail.getQueryKey().toLowerCase().contains(
									UAEFTSDetailedReportInput.getMesgTypePrompt().getPromptValue().toLowerCase()))
							.findFirst().orElse(null);
				}
				if (null != matchedComponentDetail) {
					FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
					List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
					context.setQueryId(matchedComponentDetail.getId());
					context.setQueryKey(matchedComponentDetail.getQueryKey());
					context.setQueryString(matchedComponentDetail.getQuery());
					promptsList.add(UAEFTSDetailedReportInput.getReferenceNumPrompt());
					context.setPrompts(promptsList);
					context.setExecutionId(reportContext.getExecutionId());
					UAEFTSReportOutputList = queryExecutorService.executeQuery(matchedComponentDetail, context);
				}

			}
			List<Map<String, Object>> rowDataMapList = populateRowData(UAEFTSReportOutputList, report);
			List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = populateColumnDef(report);
			reportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			reportExecuteResponseData.setData(rowDataMapList);
		}
		return reportExecuteResponseData;
	}

	private List<Map<String, Object>> populateRowData(List<FederatedReportOutput> flexReportExecuteResponseList,
			Report report) {
		List<Map<String, Object>> rowDataList = new ArrayList<Map<String, Object>>();
		List<Metrics> reportMetricsList = report.getMetricsList();
		List<String> metricsDisplayNameList = reportMetricsList.stream().map(Metrics::getDisplayName)
				.collect(Collectors.toList());
		Map<String, Object> rowMap = new HashMap<String, Object>();
		flexReportExecuteResponseList.stream().forEach(flexReport -> {
			List<Object> dataList = flexReport.getRowData();

			Iterator<Object> ik = dataList.iterator();
			Iterator<String> iv = metricsDisplayNameList.iterator();

			while (ik.hasNext() && iv.hasNext()) {
				rowMap.put(iv.next(), ik.next());
			}

			rowDataList.add(rowMap);

		});
		return rowDataList;
	}

	private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(Report reportObject) {
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = new ArrayList<ReportExecuteResponseColumnDefDTO>();
		try {
			List<Metrics> metricsList = reportObject.getMetricsList();
			metricsList.stream().forEach(metrics -> {
				ReportExecuteResponseColumnDefDTO reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
				reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
				reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef);
			});
			List<String> metricsWithLinkList = prepareLinkReportInfo(reportObject);
			reportExecuteResponseCloumnDefList.stream().forEach(colummnDef -> {
				if (metricsWithLinkList.contains(colummnDef.getField())) {
					colummnDef.setLinkExists(Boolean.TRUE);
				}
			});

		} catch (JpaSystemException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		} catch (ResourceNotFoundException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		}
		return reportExecuteResponseCloumnDefList;
	}

	private UAEFTSDetailedReportInput populateBaseInputContext(List<ReportPromptsInstanceDTO> promptsList) {
		UAEFTSDetailedReportInput UAEFTSDetailedReportInput = new UAEFTSDetailedReportInput();
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
		FederatedReportPromptDTO mesgTypePrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.MESSAGETYPEPROMPTS);
		if (null != referenceNumPrompt) {
			UAEFTSDetailedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}
		if (null != mesgTypePrompt) {
			UAEFTSDetailedReportInput.setMesgTypePrompt(mesgTypePrompt);
		}
		return UAEFTSDetailedReportInput;
	}

	private List<String> prepareLinkReportInfo(Report reportObject) {
		List<String> metricsWithLinks = new ArrayList<String>();
		List<LinkedReportResponseDTO> linkedreportResponseDTOList = linkReportService
				.fetchLinkedReportByReportId(reportObject.getId());
		linkedreportResponseDTOList.stream().forEach(linkedreportResponseDTO -> {
			metricsWithLinks.add(linkedreportResponseDTO.getSourceMetricName());
		});
		return metricsWithLinks;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> reportPromptsList,
			String promptKey) {
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = reportPromptsList.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
		ReportPromptsInstanceDTO reportInstancePrompt = promptsOptional.get();
		if (null != reportInstancePrompt) {
			List<String> promptsList = new ArrayList<String>();
			if (null != reportInstancePrompt && null != reportInstancePrompt.getPrompt().getPromptValue()) {
				promptsList.add(reportInstancePrompt.getPrompt().getPromptValue());
			}
			if (null != reportInstancePrompt && !reportInstancePrompt.getPrompt().getValue().isEmpty())
				;
			{
				promptsList.addAll(reportInstancePrompt.getPrompt().getValue());
			}
			String promptValue = promptsList.stream().collect(Collectors.joining(","));
			federatedReportPromptDTO.setPromptKey(reportInstancePrompt.getPrompt().getKey());
			federatedReportPromptDTO.setPromptValue(promptValue);
		}

		return federatedReportPromptDTO;
	}

}
