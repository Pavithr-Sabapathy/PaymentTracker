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
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.MOLDetailedFederatedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.MOLFederatedReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.CheckType;

@Component
public class MOLFederatedReportServiceImpl implements MOLFederatedReportService {
	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	ComponentsRepository componentRepository;

	@Autowired
	LinkReportService linkReportService;

	@Autowired
	ComponentsCountryRepository componentsCountryRepository;

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;

	private static final Logger log = LoggerFactory.getLogger(MOLFederatedReportServiceImpl.class);
	private static final String FILENAME = "MOLFederatedReportServiceImpl";

	public ReportExecuteResponseData processMOLReport(ReportInstanceDTO reportInstanceDTO,
			ReportContext reportContext) {
		ReportExecuteResponseData molReportExecuteResponseData = new ReportExecuteResponseData();
		List<ReportOutput> molReportExecuteResponseList = new ArrayList<ReportOutput>();
		Report report = new Report();
		if (null != reportInstanceDTO) {
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(report.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			List<Components> componentList = componentsOptional.get();
			if (!componentList.isEmpty()) {
				for (Components component : componentList) {

					ReportComponentDTO reportComponent = populateReportComponent(component);
					if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)) {
						MOLDetailedFederatedReportInput molDetailedFederatedReportInput = new MOLDetailedFederatedReportInput();
						molDetailedFederatedReportInput.setComponent(reportComponent);
						molDetailedFederatedReportInput = populateBaseInputContext(reportInstanceDTO.getPromptsList());
						List<ReportOutput> molReportExecuteResponse = executeReport(
								molDetailedFederatedReportInput, reportComponent, reportContext);
						if (!molReportExecuteResponse.isEmpty()) {
							molReportExecuteResponseList.addAll(molReportExecuteResponse);

						}
					}
				}
				List<Map<String, Object>> rowDataMapList = populateRowData(molReportExecuteResponseList, report);
				List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = populateColumnDef(report);
				molReportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				molReportExecuteResponseData.setData(rowDataMapList);
			}
		}
		return molReportExecuteResponseData;
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

	private MOLDetailedFederatedReportInput populateBaseInputContext(List<ReportPromptsInstanceDTO> reportPromptsList) {
		MOLDetailedFederatedReportInput molDetailedFederatedReportInput = new MOLDetailedFederatedReportInput();
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(reportPromptsList,
				MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
		if (null != referenceNumPrompt) {
			molDetailedFederatedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}

		return molDetailedFederatedReportInput;
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

	private List<Map<String, Object>> populateRowData(List<ReportOutput> molReportExecuteResponse,
			Report report) {
		List<Map<String, Object>> rowDataList = new ArrayList<Map<String, Object>>();
		List<Metrics> reportMetricsList = report.getMetricsList();
		List<String> metricsDisplayNameList = reportMetricsList.stream().map(Metrics::getDisplayName)
				.collect(Collectors.toList());
		Map<String, Object> rowMap = new HashMap<String, Object>();
		molReportExecuteResponse.stream().forEach(flexReport -> {
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

	private List<ReportOutput> executeReport(MOLDetailedFederatedReportInput molDetailedFederatedReportInput,
			ReportComponentDTO component, ReportContext reportContext) {
		ReportComponentDetailDTO matchedComponentDetail = new ReportComponentDetailDTO();
		List<ReportOutput> flexReportExecuteResponse = new ArrayList<ReportOutput>();
		if (null != component) {
			Set<ReportComponentDetailDTO> componentDetailsSet = component.getReportComponentDetails();
			for (ReportComponentDetailDTO componentDetail : componentDetailsSet) {
				if (componentDetail.getQueryKey().toLowerCase().contains(
						molDetailedFederatedReportInput.getReferenceNumPrompt().getPromptValue().toLowerCase())) {
					matchedComponentDetail = componentDetail;
					break;
				}
			}
			if (matchedComponentDetail != null) {

				FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				context.setQueryId(matchedComponentDetail.getId());
				context.setQueryKey(matchedComponentDetail.getQueryKey());
				context.setQueryString(matchedComponentDetail.getQuery());
				promptsList.add(molDetailedFederatedReportInput.getReferenceNumPrompt());
				context.setPrompts(promptsList);
				context.setExecutionId(reportContext.getExecutionId());

				flexReportExecuteResponse = queryExecutorService.executeQuery(matchedComponentDetail, context);

			}
		}
		return flexReportExecuteResponse;
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

	private List<String> prepareLinkReportInfo(Report reportObject) {
		List<String> metricsWithLinks = new ArrayList<String>();
		List<LinkedReportResponseDTO> linkedreportResponseDTOList = linkReportService
				.fetchLinkedReportByReportId(reportObject.getId());
		linkedreportResponseDTOList.stream().forEach(linkedreportResponseDTO -> {
			metricsWithLinks.add(linkedreportResponseDTO.getSourceMetricName());
		});
		return metricsWithLinks;
	}
}
