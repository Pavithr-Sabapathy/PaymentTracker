package com.mashreq.paymentTracker.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.mashreq.paymentTracker.dto.FederatedReportQueryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceComponentDTO;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsFederatedReportOutput;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.utility.CheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("messageDeatails")
public class MessageDetailsFederatedReportServiceImpl extends ReportControllerServiceImpl
		implements ReportControllerService {
	private static final Logger log = LoggerFactory.getLogger(MessageDetailsFederatedReportServiceImpl.class);
	private static final String FILENAME = "MessageDetailsFederatedReportServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;
	@Autowired
	CannedReportService cannedReportService;
	@Autowired
	private ComponentsDAO componentsDAO;

	@Override
	protected ReportInput populateBaseInputContext(ReportContext reportContext) {

		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> list = reportInstance.getPromptsList();
		MessageDetailsFederatedReportInput messageDetailsFederatedReportInput = new MessageDetailsFederatedReportInput();

		FederatedReportPromptDTO messageThroughPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_THROUGH_PROMPT_KEY);
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY);
		FederatedReportPromptDTO messageTypePrompt = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY);
		FederatedReportPromptDTO messageSubFormatFormat = getMatchedInstancePrompt(list,
				MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY);
		if (null != messageThroughPrompt) {
			messageDetailsFederatedReportInput.setMessageThroughPrompt(messageThroughPrompt);
		}
		if (null != referenceNumPrompt) {
			messageDetailsFederatedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}
		if (null != messageTypePrompt) {
			messageDetailsFederatedReportInput.setMessageTypePrompt(messageTypePrompt);
		}
		if (null != messageSubFormatFormat) {
			messageDetailsFederatedReportInput.setMessageSubFormatPrompt(messageSubFormatFormat);
		}
		return messageDetailsFederatedReportInput;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> list,
			String messageDetailsMessageThroughPromptKey) {
		ReportPromptsInstanceDTO reportInstancePrompt = new ReportPromptsInstanceDTO();
		List<String> promptsList = new ArrayList<String>();
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = list.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(messageDetailsMessageThroughPromptKey))
				.findAny();
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

	@Override
	protected ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Map<String, Object>> messageData = null;
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
				if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)) {

					MessageDetailsFederatedReportInput reportInputContext = (MessageDetailsFederatedReportInput) reportInput;
					FederatedReportQueryData federatedReportQueryData = (FederatedReportQueryData) reportInput;
					List<ReportInstanceComponentDTO> reportInstanceComponentDTO = new ArrayList<ReportInstanceComponentDTO>();

					processComponents(reportInputContext, reportContext, reportInstanceComponentDTO, reportInstanceDTO,
							federatedReportQueryData);
				}
			}
			responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			responseData.setData(messageData);
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

	private void processComponents(MessageDetailsFederatedReportInput reportInputContext, ReportContext reportContext,
			List<ReportInstanceComponentDTO> reportInstanceComponentDTO, ReportInstanceDTO reportInstanceDTO,
			FederatedReportQueryData federatedReportQueryData) {

		if (null != reportInstanceComponentDTO) {
			// pick the right component
			FederatedReportPromptDTO messageThroughPrompt = reportInputContext.getMessageThroughPrompt();
			String messageThrough = messageThroughPrompt.getPromptValue();
			if (messageThrough.equalsIgnoreCase(MashreqFederatedReportConstants.MESSAGE_THROUGH_SWIFT)) {
				processSwiftMessage(reportInputContext, reportContext, reportInstanceComponentDTO,
						federatedReportQueryData, reportInstanceDTO);
			} else if (messageThrough.equalsIgnoreCase(MashreqFederatedReportConstants.MESSAGE_THROUGH_UAEFTS)) {
				processUaeftsMessage(reportInputContext, reportContext, reportInstanceComponentDTO,
						federatedReportQueryData, reportInstanceDTO);
			}
		}
	}

	private void processUaeftsMessage(MessageDetailsFederatedReportInput reportInputContext,
			ReportContext reportContext, List<ReportInstanceComponentDTO> reportInstanceComponentDTO,
			FederatedReportQueryData federatedReportQueryData, ReportInstanceDTO reportInstanceDTO) {

		ReportInstanceComponentDTO instanceComponent = getMatchedInstanceComponent(reportInstanceComponentDTO,
				MashreqFederatedReportConstants.PROCESSING_SYSTEM_MESSAGE);
		if (instanceComponent != null) {
			reportInputContext.setComponent(instanceComponent);
			List<SWIFTMessageDetailsFederatedReportOutput> reportComponentData = cannedReportService
					.processMessageDetailsReport(reportInputContext, reportInstanceComponentDTO, reportContext);
			if (!reportComponentData.isEmpty()) {
				populateSWIFTDataToObjectForm(reportComponentData, federatedReportQueryData);
//				transformReportData(federatedReportQueryData.getQueryData(), getCannedReportInstanceRetrievalService()
//						.getCannedReportInstanceMetrics(reportInstanceDTO.getId()));
			}
		}

	}

	private ReportInstanceComponentDTO getMatchedInstanceComponent(
			List<ReportInstanceComponentDTO> reportInstanceComponentDTO, String processingSystemMessage) {
		ReportInstanceComponentDTO matchedComponent = null;
		for (ReportInstanceComponentDTO rep : reportInstanceComponentDTO) {
			if (rep.getComponentKey().equalsIgnoreCase(processingSystemMessage) && CheckType.YES == rep.getActive()) {
				matchedComponent = rep;
			}
		}

		return matchedComponent;
	}

	private void populateSWIFTDataToObjectForm(List<SWIFTMessageDetailsFederatedReportOutput> componentOutput,
			FederatedReportQueryData federatedReportQueryData) {
		List<ReportOutput> data = new ArrayList<ReportOutput>();
		for (SWIFTMessageDetailsFederatedReportOutput componentOut : componentOutput) {
			SWIFTMessageDetailsFederatedReportOutput output = componentOut;
			ReportOutput defaultOutput = createFederatedDefaultOutput(output);
			List<Object> rowData = new ArrayList<Object>();
			rowData.add(output.getKey());
			rowData.add(output.getValue());
			defaultOutput.setRowData(rowData);
			data.add(defaultOutput);
		}
		federatedReportQueryData.setQueryData(data);

	}

	private ReportOutput createFederatedDefaultOutput(SWIFTMessageDetailsFederatedReportOutput baseOutput) {
		ReportOutput output = new ReportOutput();
		output.setComponentDetailId(baseOutput.getComponentDetailId());
		return output;
	}

	private void processSwiftMessage(MessageDetailsFederatedReportInput reportInputContext, ReportContext reportContext,
			List<ReportInstanceComponentDTO> reportInstanceComponentDTO,
			FederatedReportQueryData federatedReportQueryData, ReportInstanceDTO reportInstanceDTO) {

		ReportInstanceComponentDTO instanceComponent = getMatchedInstanceComponent(reportInstanceComponentDTO,
				MashreqFederatedReportConstants.PROCESSING_SYSTEM_MESSAGE);
		if (instanceComponent != null) {
			reportInputContext.setComponent(instanceComponent);
			List<SWIFTMessageDetailsFederatedReportOutput> reportComponentData = cannedReportService
					.processMessageDetailsReport(reportInputContext, reportInstanceComponentDTO, reportContext);
			if (!reportComponentData.isEmpty()) {
				populateSWIFTDataToObjectForm(reportComponentData, federatedReportQueryData);
//				transformReportData(federatedReportQueryData.getQueryData(), getCannedReportInstanceRetrievalService()
//						.getCannedReportInstanceMetrics(reportInstanceDTO.getId()));
			}
		}
	}
}
