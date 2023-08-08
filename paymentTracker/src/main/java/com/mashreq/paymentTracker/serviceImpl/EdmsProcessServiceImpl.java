package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportDefaultInput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;

@Service
public class EdmsProcessServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;

	@Override
	public FederatedReportDefaultInput populateBaseInputContext(ReportContext reportContext) {
		FederatedReportDefaultInput federatedReportDefaultInput = new FederatedReportDefaultInput();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> instancePromptList = reportInstanceDTO.getPromptsList();
		federatedReportDefaultInput.setInstancePrompts(instancePromptList);
		return federatedReportDefaultInput;
	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		Boolean dataFoundFromBPM = false;
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		/** fetch the report details based on report name **/
		Report report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		CannedReport cannedReport = cannedReportService.populateCannedReportInstance(report);
		List<Components> componentList = componentsDAO.findAllByreportId(cannedReport.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			for (Components component : componentList) {
				ReportComponentDTO reportComponent = populateReportComponent(component);
				if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)
						&& !dataFoundFromBPM) {
					FederatedReportDefaultInput federatedReportDefaultInput = (FederatedReportDefaultInput) reportInput;
					federatedReportDefaultInput.setComponent(reportComponent);
					try {
						List<ReportDefaultOutput> output = processReport(federatedReportDefaultInput, reportContext);
						if (!flexReportExecuteResponse.isEmpty()) {
							if (isEDDDetailReport(component)) {
								dataFoundFromBPM = MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
										.equalsIgnoreCase(component.getComponentName()) ? true : false;
								if (flexReportExecuteResponse.isEmpty()) {
									flexReportExecuteResponse.addAll(output);
								} else {
									if (MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
											.equalsIgnoreCase(component.getComponentName())) {
										flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
										flexReportExecuteResponse.addAll(output);
									}
								}
							} else {
								flexReportExecuteResponse.addAll(output);
							}
						}
					} catch (Exception exception) {

					}
				}
			}
		}
		List<Map<String, Object>> rowDataMapList = reportOutputExecutor.populateRowData(flexReportExecuteResponse,
				report);
		reportExecuteResponseCloumnDefList = reportOutputExecutor.populateColumnDef(report);
		responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
		responseData.setData(rowDataMapList);
		return responseData;
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

	private boolean isEDDDetailReport(Components component) {
		if (MashreqFederatedReportConstants.EDMS_EDD_DETAILED_REP_COMP.equalsIgnoreCase(component.getComponentName())
				|| MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
						.equalsIgnoreCase(component.getComponentName())) {
			return true;
		} else {
			return false;
		}
	}

	private List<ReportDefaultOutput> processReport(FederatedReportDefaultInput federatedReportDefaultInput,
			ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponseList = new ArrayList<ReportDefaultOutput>();
		ReportComponentDTO component = federatedReportDefaultInput.getComponent();
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		if (!componentDetailList.isEmpty()) {
			for (ReportComponentDetailDTO componentDetail : componentDetailList) {
				ReportComponentDetailContext context = new ReportComponentDetailContext();
				context.setQueryId(componentDetail.getId());
				context.setQueryKey(componentDetail.getQueryKey());
				context.setQueryString(componentDetail.getQuery());
				List<FederatedReportPromptDTO> prompts = populatePrompts(
						federatedReportDefaultInput.getInstancePrompts());
				context.setPrompts(prompts);
				context.setExecutionId(reportContext.getExecutionId());
				flexReportExecuteResponseList.addAll(queryExecutorService.executeQuery(componentDetail, context));
			}

		}
		return flexReportExecuteResponseList;
	}

	protected List<FederatedReportPromptDTO> populatePrompts(List<ReportPromptsInstanceDTO> reportPromptDTO) {
		List<PromptInstance> promptsList = reportPromptDTO.stream().map(prompts -> prompts.getPrompt())
				.collect(Collectors.toList());
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		for (PromptInstance instancePrompt : promptsList) {
			FederatedReportPromptDTO promptInfo = new FederatedReportPromptDTO();
			promptInfo.setPromptKey(instancePrompt.getKey());
			promptInfo.setPromptValue(instancePrompt.getPromptValue());
			prompts.add(promptInfo);
		}
		return prompts;
	}

}