package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportDefaultInput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
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
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

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

	private static final Logger log = LoggerFactory.getLogger(EdmsProcessServiceImpl.class);
	private static final String FILENAME = "EdmsProcessServiceImpl";

	@Override
	protected FederatedReportDefaultInput populateBaseInputContext(ReportContext reportContext) {
		FederatedReportDefaultInput federatedReportDefaultInput = new FederatedReportDefaultInput();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> instancePromptList = reportInstanceDTO.getPromptsList();
		federatedReportDefaultInput.setInstancePrompts(instancePromptList);
		return federatedReportDefaultInput;
	}

	@Override
	protected ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportOutput> flexReportExecuteResponse = new ArrayList<ReportOutput>();
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
						List<ReportOutput> output = processReport(federatedReportDefaultInput, reportContext);
						if (!flexReportExecuteResponse.isEmpty()) {
							if (isEDDDetailReport(component)) {
								dataFoundFromBPM = MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
										.equalsIgnoreCase(component.getComponentName()) ? true : false;
								if (flexReportExecuteResponse.isEmpty()) {
									flexReportExecuteResponse.addAll(output);
								} else {
									if (MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
											.equalsIgnoreCase(component.getComponentName())) {
										flexReportExecuteResponse = new ArrayList<ReportOutput>();
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

	public List<AdvanceSearchReportOutput> processEdmsReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<ReportOutput> flexReportExecuteResponse = new ArrayList<ReportOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		Components component = getMatchedInstanceComponent(componentList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_EDMS_COMPONENT_KEY);
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
			log.info(FILENAME + "processEdmsReport [Response] -->" + advanceSearchReportOutputList.toString());
		}
		return advanceSearchReportOutputList;
	}

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(List<ReportOutput> ReportOutputList,
			AdvanceSearchReportInput advanceSearchReportInput) {

		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		if (!ReportOutputList.isEmpty()) {
			for (ReportOutput ReportOutput : ReportOutputList) {
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

	private boolean isEDDDetailReport(Components component) {
		if (MashreqFederatedReportConstants.EDMS_EDD_DETAILED_REP_COMP.equalsIgnoreCase(component.getComponentName())
				|| MashreqFederatedReportConstants.BPM_EDD_DETAILED_REP_COMP
						.equalsIgnoreCase(component.getComponentName())) {
			return true;
		} else {
			return false;
		}
	}

	private List<ReportOutput> processReport(FederatedReportDefaultInput federatedReportDefaultInput,
			ReportContext reportContext) {
		List<ReportOutput> flexReportExecuteResponseList = new ArrayList<ReportOutput>();
		ReportComponentDTO component = federatedReportDefaultInput.getComponent();
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		if (!componentDetailList.isEmpty()) {
			for (ReportComponentDetailDTO componentDetail : componentDetailList) {
				FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
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