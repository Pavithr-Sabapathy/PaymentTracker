package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SWIFTDetailedFederatedReportDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsReportOutput;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;

@Service("swiftDetails")
public class SwiftDetailedReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	CannedReportService cannedReportService;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;

	@Autowired
	SwiftReportConnector swiftReportConnector;

	private static final Logger log = LoggerFactory.getLogger(SwiftDetailedReportServiceImpl.class);
	private static final String FILENAME = "SwiftDetailedReportServiceImpl";

	@Override
	protected ReportInput populateBaseInputContext(ReportContext reportContext) {
		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> list = reportInstance.getPromptsList();
		SWIFTDetailedFederatedReportDTO swiftDetailedFederatedReportDTO = new SWIFTDetailedFederatedReportDTO();
		FederatedReportPromptDTO aidPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.AID_PROMPT_KEY);
		FederatedReportPromptDTO umidhPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.S_UMIDH_PROMPT_KEY);
		FederatedReportPromptDTO umidlPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.S_UMIDL_PROMPT_KEY);
		FederatedReportPromptDTO SWIFTDetailedTypePrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY);
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY);
		FederatedReportPromptDTO messageTypePrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY);
		FederatedReportPromptDTO messageSubFormatFormat = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY);
		if (null != aidPrompt) {
			swiftDetailedFederatedReportDTO.setAidPrompt(aidPrompt);
		}
		if (null != umidhPrompt) {
			swiftDetailedFederatedReportDTO.setUmidhPrompt(umidhPrompt);
		}
		if (null != umidlPrompt) {
			swiftDetailedFederatedReportDTO.setUmidlPrompt(umidlPrompt);
		}
		if (null != SWIFTDetailedTypePrompt) {
			swiftDetailedFederatedReportDTO.setDetailedType(SWIFTDetailedTypePrompt);
		}
		if (null != referenceNumPrompt) {
			swiftDetailedFederatedReportDTO.setReferenceNumPrompt(referenceNumPrompt);
		}
		if (null != messageTypePrompt) {
			swiftDetailedFederatedReportDTO.setMessageTypePrompt(messageTypePrompt);
		}
		if (null != messageSubFormatFormat) {
			swiftDetailedFederatedReportDTO.setMessageSubFormatPrompt(messageSubFormatFormat);
		}
		return swiftDetailedFederatedReportDTO;

	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Map<String, Object>> swiftData = null;
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		List<SWIFTMessageDetailsReportOutput> swiftMessagingDetailsOutputList = new ArrayList<SWIFTMessageDetailsReportOutput>();
		/** fetch the report details based on report name **/
		Report report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		CannedReport cannedReport = cannedReportService.populateCannedReportInstance(report);
		List<Components> componentList = componentsDAO.findAllByreportId(cannedReport.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			for (Components component : componentList) {
				ReportComponentDTO reportComponent = populateReportComponent(component);
				if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)) {
					SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO = (SWIFTDetailedFederatedReportDTO) reportInput;
					swiftDetailedReportDTO.setComponent(reportComponent);
					List<? extends ReportOutput> messageDetails = swiftReportConnector
							.processReportComponent(reportInput, reportContext);
					if (!messageDetails.isEmpty()) {
						for (ReportOutput reportOutput : messageDetails) {
							SWIFTMessageDetailsReportOutput SWIFTMessageDetailsReportOutput = (SWIFTMessageDetailsReportOutput) reportOutput;
							swiftMessagingDetailsOutputList.add(SWIFTMessageDetailsReportOutput);
						}
					}
					swiftData = populateSwiftDetailedReportData(swiftMessagingDetailsOutputList);
					reportExecuteResponseCloumnDefList = reportOutputExecutor.populateColumnDef(report);
				}
			}
			responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			responseData.setData(swiftData);
			log.info(FILENAME + "[processSwiftDetailReport Response]-->" + responseData.toString());
		}
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

	private List<Map<String, Object>> populateSwiftDetailedReportData(
			List<SWIFTMessageDetailsReportOutput> swiftDetailedReports) {
		List<Map<String, Object>> swiftDetailedReportDataList = new ArrayList<Map<String, Object>>();
		for (SWIFTMessageDetailsReportOutput swiftReport : swiftDetailedReports) {
			if (null != swiftReport.getKey() && null != swiftReport.getValue()) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put(MashreqFederatedReportConstants.FIELD_DESCRIPTION, swiftReport.getKey());
				mapData.put(MashreqFederatedReportConstants.FIELD_VALUE, swiftReport.getValue());
				swiftDetailedReportDataList.add(mapData);
			}
		}

		return swiftDetailedReportDataList;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> list, String promptKey) {
		ReportPromptsInstanceDTO reportInstancePrompt = new ReportPromptsInstanceDTO();
		List<String> promptsList = new ArrayList<String>();
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = list.stream()
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