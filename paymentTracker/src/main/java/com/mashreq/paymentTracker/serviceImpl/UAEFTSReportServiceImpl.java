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
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.UAEFTSDetailedReportInput;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service("Uaefts")
public class UAEFTSReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	private static final Logger log = LoggerFactory.getLogger(UAEFTSReportServiceImpl.class);
	private static final String FILENAME = "UAEFTSReportServiceImpl";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;

	public List<AdvanceSearchReportOutput> processAdvanceSearchReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<ReportOutput> flexReportExecuteResponse = new ArrayList<ReportOutput>();
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
			log.info(FILENAME + "[processAdvanceSearchReport Response] -->" + advanceSearchReportOutputList.toString());
		}
		return advanceSearchReportOutputList;
	}

	private AdvanceSearchReportOutput populateDataForAdvanceSearch(List<ReportOutput> flexReportExecuteResponse,
			AdvanceSearchReportInput advanceSearchReportInput) {
		AdvanceSearchReportOutput advanceSearchFederatedReportOutput = new AdvanceSearchReportOutput();
		Map<String, AdvanceSearchReportOutput> flexMatrixBasedUaeftsTransactions = advanceSearchReportInput
				.getFlexMatrixBasedUaeftsTransactions();
		if (!flexReportExecuteResponse.isEmpty()) {
			for (ReportOutput federatedReportOutput : flexReportExecuteResponse) {
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
		Components componentObj = new Components();
		Optional<Components> componentOptional = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)
						&& component.getActive().equalsIgnoreCase(MashreqFederatedReportConstants.YES))
				.findFirst();
		if (componentOptional.isPresent())
			componentObj = componentOptional.get();
		return componentObj;

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
	public ReportInput populateBaseInputContext(ReportContext reportContext) {
		UAEFTSDetailedReportInput UAEFTSDetailedReportInput = new UAEFTSDetailedReportInput();
		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> promptsList = reportInstance.getPromptsList();
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

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData reportExecuteResponseData = new ReportExecuteResponseData();
		List<ReportOutput> UAEFTSReportOutputList = new ArrayList<ReportOutput>();
		ReportComponentDetailDTO matchedComponentDetail = new ReportComponentDetailDTO();
		Report report = new Report();
		ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
		if (null != reportContext.getReportInstance()) {
			reportInstanceDTO = reportContext.getReportInstance();
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		List<Components> componentList = componentsDAO.findAllByreportId(report.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			UAEFTSDetailedReportInput UAEFTSDetailedReportInput = (UAEFTSDetailedReportInput) reportInput;
			for (Components component : componentList) {
				ReportComponentDTO reportComponent = populateReportComponent(component);
				UAEFTSDetailedReportInput.setComponent(reportComponent);

				matchedComponentDetail = reportComponent.getReportComponentDetails().stream()
						.filter(componentDetail -> componentDetail.getQueryKey().toLowerCase()
								.contains(UAEFTSDetailedReportInput.getMesgTypePrompt().getPromptValue().toLowerCase()))
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

			List<Map<String, Object>> rowDataMapList = reportOutputExecutor.populateRowData(UAEFTSReportOutputList,
					report);
			List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = reportOutputExecutor
					.populateColumnDef(report);
			reportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			reportExecuteResponseData.setData(rowDataMapList);
		}
		return reportExecuteResponseData;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> reportPromptsList,
			String promptKey) {
		ReportPromptsInstanceDTO reportInstancePrompt = new ReportPromptsInstanceDTO();
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		List<String> promptsList = new ArrayList<String>();
		Optional<ReportPromptsInstanceDTO> promptsOptional = reportPromptsList.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
		if (promptsOptional.isPresent()) {
			reportInstancePrompt = promptsOptional.get();
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

		return federatedReportPromptDTO;
	}

}
