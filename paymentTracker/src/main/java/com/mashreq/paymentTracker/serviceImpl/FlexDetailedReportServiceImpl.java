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
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexDetailedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
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
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service("flexPostingDetails")
public class FlexDetailedReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

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

	@Autowired
	FlexReportConnector flexReportConnector;

	private static final Logger log = LoggerFactory.getLogger(FlexDetailedReportServiceImpl.class);
	private static final String FILENAME = "FlexFederatedReportServiceImpl";

	@Override
	public ReportInput populateBaseInputContext(ReportContext reportContext) {
		List<ReportPromptsInstanceDTO> reportPromptsList = reportContext.getReportInstance().getPromptsList();
		FlexDetailedReportInput flexAccountingDetailedFederatedReportInput = new FlexDetailedReportInput();
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(reportPromptsList,
				MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
		FederatedReportPromptDTO accountingSourcePrompt = getMatchedInstancePrompt(reportPromptsList,
				MashreqFederatedReportConstants.ACCOUNTINGSOURCEPROMPTS);
		FederatedReportPromptDTO debitAccountPrompt = getMatchedInstancePrompt(reportPromptsList,
				MashreqFederatedReportConstants.RELATEDACCOUNTPROMPTS);
		if (null != referenceNumPrompt) {
			flexAccountingDetailedFederatedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}
		if (null != accountingSourcePrompt) {
			flexAccountingDetailedFederatedReportInput.setAccountingSourcePrompt(accountingSourcePrompt);
		}
		if (null != debitAccountPrompt) {
			flexAccountingDetailedFederatedReportInput.setDebitAccountPrompt(debitAccountPrompt);
		}
		return flexAccountingDetailedFederatedReportInput;
	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData flexReportExecuteResponseData = new ReportExecuteResponseData();
		List<ReportDefaultOutput> flexReportExecuteResponseList = new ArrayList<ReportDefaultOutput>();
		Report report = new Report();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		if (null != reportInstanceDTO) {
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		List<Components> componentList = componentsDAO.findAllByreportId(report.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			componentList.stream().forEach(component -> {
				ReportComponentDTO reportComponent = populateReportComponent(component);
				if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)) {
					FlexDetailedReportInput flexDetailedReportInput = (FlexDetailedReportInput) reportInput;
					flexDetailedReportInput.setComponent(reportComponent);
					List<? extends ReportOutput> flexReportExecuteResponse = flexReportConnector
							.processReportComponent(reportInput, reportContext);
					if (!flexReportExecuteResponse.isEmpty()) {
						for (ReportOutput componentOut : flexReportExecuteResponse) {
							ReportDefaultOutput output = (ReportDefaultOutput) componentOut;
							flexReportExecuteResponseList.add(output);
						}
					}
				}
			});
			List<Map<String, Object>> rowDataMapList = reportOutputExecutor
					.populateRowData(flexReportExecuteResponseList, report);
			List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = reportOutputExecutor
					.populateColumnDef(report);
			flexReportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			flexReportExecuteResponseData.setData(rowDataMapList);
		}
		log.info(FILENAME + "processFlexReport [Response] -->" + flexReportExecuteResponseData.toString());
		return flexReportExecuteResponseData;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> reportPromptsList,
			String promptKey) {
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = reportPromptsList.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
		if (promptsOptional.isPresent()) {
			ReportPromptsInstanceDTO reportInstancePrompt = promptsOptional.get();
			if (null != reportInstancePrompt) {
				List<String> promptsList = new ArrayList<String>();
				if (null != reportInstancePrompt && null != reportInstancePrompt.getPrompt().getPromptValue()) {
					promptsList.add(reportInstancePrompt.getPrompt().getPromptValue());
				}
				if (null != reportInstancePrompt && !reportInstancePrompt.getPrompt().getValue().isEmpty()) {
					promptsList.addAll(reportInstancePrompt.getPrompt().getValue());
				}
				String promptValue = promptsList.stream().collect(Collectors.joining(","));
				federatedReportPromptDTO.setPromptKey(reportInstancePrompt.getPrompt().getKey());
				federatedReportPromptDTO.setPromptValue(promptValue);
			}
		}
		return federatedReportPromptDTO;
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

	public List<AdvanceSearchReportOutput> processFlexDetailReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		Components component = getMatchedInstanceComponent(componentList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_FLEX_COMPONENT_KEY);
		if (null != component) {
			ReportComponentDTO reportComponent = populateReportComponent(component);
			advanceSearchReportInput.setFlexComponent(reportComponent);
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

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(List<ReportDefaultOutput> flexReportExecuteResponse,
			AdvanceSearchReportInput advanceSearchReportInput) {
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

	private Components getMatchedInstanceComponent(List<Components> componentList, String componentKey) {
		Components componentObj = new Components();
		Optional<Components> componentOptional = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)
						&& component.getActive().equalsIgnoreCase(MashreqFederatedReportConstants.YES))
				.findFirst();
		if (componentOptional.isPresent()) {
			componentObj = componentOptional.get();
		}
		return componentObj;
	}

}
