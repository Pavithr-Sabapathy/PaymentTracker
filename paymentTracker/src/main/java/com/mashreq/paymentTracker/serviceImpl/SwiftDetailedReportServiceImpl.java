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
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.MessageField;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SWIFTDetailedFederatedReportDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsFederatedReportOutput;
import com.mashreq.paymentTracker.dto.StxEntryFieldViewInfo;
import com.mashreq.paymentTracker.dto.SwiftDetailsReportObjectDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class SwiftDetailedReportServiceImpl implements SwiftDetailedReportService {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;

	private static final Logger log = LoggerFactory.getLogger(SwiftDetailedReportServiceImpl.class);
	private static final String FILENAME = "SwiftDetailedReportServiceImpl";

	public ReportExecuteResponseData processSwiftDetailReport(ReportInstanceDTO reportInstanceDTO,
			ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Map<String, Object>> swiftData = null;
		/** fetch the report details based on report name **/
		Report report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		CannedReport cannedReport = cannedReportService.populateCannedReportInstance(report);
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(cannedReport.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			List<Components> componentList = componentsOptional.get();
			if (!componentList.isEmpty()) {
				for (Components component : componentList) {
					ReportComponentDTO reportComponent = populateReportComponent(component);
					if (null != reportComponent.getActive() && reportComponent.getActive().equals(CheckType.YES)) {
						SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO = new SWIFTDetailedFederatedReportDTO();
						swiftDetailedReportDTO.setComponent(reportComponent);
						swiftDetailedReportDTO = populateBaseInputContext(reportInstanceDTO.getPromptsList());
						List<SWIFTMessageDetailsFederatedReportOutput> messageDetails = processSwiftDetailedReport(
								swiftDetailedReportDTO, reportComponent, reportContext);
						swiftData = populateSwiftDetailedReportData(messageDetails);
						reportExecuteResponseCloumnDefList = reportOutputExecutor.populateColumnDef(report);
					}
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
			List<SWIFTMessageDetailsFederatedReportOutput> swiftDetailedReports) {
		List<Map<String, Object>> swiftDetailedReportDataList = new ArrayList<Map<String, Object>>();
		for (SWIFTMessageDetailsFederatedReportOutput swiftReport : swiftDetailedReports) {
			if (null != swiftReport.getKey() && null != swiftReport.getValue()) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put(MashreqFederatedReportConstants.FIELD_DESCRIPTION, swiftReport.getKey());
				mapData.put(MashreqFederatedReportConstants.FIELD_VALUE, swiftReport.getValue());
				swiftDetailedReportDataList.add(mapData);
			}
		}

		return swiftDetailedReportDataList;
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> processSwiftDetailedReport(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, ReportComponentDTO component,
			ReportContext reportContext) {
		FederatedReportPromptDTO detailedType = swiftDetailedReportDTO.getDetailedType();
		switch (detailedType.getPromptValue()) {
		case (MashreqFederatedReportConstants.DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RMESG):
		case (MashreqFederatedReportConstants.DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RINTV):
			return populateRMesgDetailData(swiftDetailedReportDTO, component, reportContext);

		}
		return null;
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> populateRMesgDetailData(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, ReportComponentDTO component,
			ReportContext reportContext) {

		MessageDetailsFederatedReportInput reportInput = new MessageDetailsFederatedReportInput();
		reportInput.setMessageSubFormatPrompt(swiftDetailedReportDTO.getMessageSubFormatPrompt());
		reportInput.setMessageTypePrompt(swiftDetailedReportDTO.getMessageTypePrompt());
		reportInput.setReferenceNumPrompt(swiftDetailedReportDTO.getReferenceNumPrompt());
		return processMessageDetailsReport(reportInput, component, reportContext);
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> processMessageDetailsReport(
			MessageDetailsFederatedReportInput messagingDetailsInput, ReportComponentDTO component,
			ReportContext reportContext) {

		Set<ReportComponentDetailDTO> componentDetailsList = component.getReportComponentDetails();
		List<SWIFTMessageDetailsFederatedReportOutput> messageDetails = new ArrayList<SWIFTMessageDetailsFederatedReportOutput>();

		SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO = new SwiftDetailsReportObjectDTO();
		if (!componentDetailsList.isEmpty()) {
			String rmesgQuery = MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG;
			processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput, rmesgQuery,
					swiftDetailsReportObjectDTO, reportContext);
			if (swiftDetailsReportObjectDTO.isMessageFound()) {
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RINTV, swiftDetailsReportObjectDTO,
						reportContext);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD,
						swiftDetailsReportObjectDTO, reportContext);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RCORR, swiftDetailsReportObjectDTO,
						reportContext);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE,
						swiftDetailsReportObjectDTO, reportContext);
				// if message fields are there, we need to process this.
				if (!swiftDetailsReportObjectDTO.getMessageFields().isEmpty()) {
					processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
							MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW,
							swiftDetailsReportObjectDTO, reportContext);
				}
				processSwiftMessageDetailContext(swiftDetailsReportObjectDTO, messageDetails);
			}
		}
		return messageDetails;
	}

	private void processSwiftMessageDetailContext(SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO,
			List<SWIFTMessageDetailsFederatedReportOutput> messageDetails) {

		// TODO
		Long compDetailId = swiftDetailsReportObjectDTO.getComponentDetailId();
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.DESCRIPTION_LABEL,
				swiftDetailsReportObjectDTO.getDescription(), compDetailId);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.DELIVERY_STATUS_LABEL,
				swiftDetailsReportObjectDTO.getDeliveryStatus(), compDetailId);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.PRIORITY_LABEL,
				swiftDetailsReportObjectDTO.getPriority(), compDetailId);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.INPUT_REFERENCE_LABEL,
				swiftDetailsReportObjectDTO.getReference(), compDetailId);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.SWIFT_INPUT_LABEL,
				swiftDetailsReportObjectDTO.getSwiftInput(), compDetailId);

		String sender = swiftDetailsReportObjectDTO.getSender();
		String senderDetails = swiftDetailsReportObjectDTO.getSenderDetails();
		String senderValue = UtilityClass.combineValuesWithBreakTag(sender, senderDetails);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.SENDER_LABEL, senderValue, compDetailId);

		String receiver = swiftDetailsReportObjectDTO.getReceiver();
		String receiverDetails = swiftDetailsReportObjectDTO.getReceiverDetails();
		String receiverValue = UtilityClass.combineValuesWithBreakTag(receiver, receiverDetails);
		addToMessageDetails(messageDetails, MashreqFederatedReportConstants.RECEIVER_LABEL, receiverValue,
				compDetailId);

		if (!swiftDetailsReportObjectDTO.getMessageFields().isEmpty()) {
			for (MessageField messageField : swiftDetailsReportObjectDTO.getMessageFields()) {
				addToMessageDetails(messageDetails, messageField.getFieldExpression(), messageField.getFieldValue(),
						compDetailId);
			}
		}
	}

	private void addToMessageDetails(List<SWIFTMessageDetailsFederatedReportOutput> messageDetails, String label,
			String value, Long compDetailId) {
		if (null != value) {
			messageDetails.add(populateFederatedReportOutput(label, value, compDetailId));
		}
	}

	private SWIFTMessageDetailsFederatedReportOutput populateFederatedReportOutput(String key, String value,
			Long compDetailId) {
		SWIFTMessageDetailsFederatedReportOutput reportOutput = new SWIFTMessageDetailsFederatedReportOutput();
		reportOutput.setKey(key);
		reportOutput.setValue(value);
		reportOutput.setComponentDetailId(compDetailId);
		return reportOutput;
	}

	private void processMessageDetailsComponentDetail(ReportComponentDTO component,
			Set<ReportComponentDetailDTO> componentDetailsList,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {

		if (!componentDetailsList.isEmpty()) {
			ReportComponentDetailDTO componentDetails = getMatchedComponentDetails(componentDetailsList, componentKey);
			if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG.equalsIgnoreCase(componentKey)) {

				processMessageDetailsRMesgQuery(componentDetails, messagingDetailsInput, componentKey,
						swiftDetailsReportObjectDTO, reportContext);
			} else if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RINTV.equalsIgnoreCase(componentKey)) {
				processMessageDetailsRIntvQuery(componentDetails, messagingDetailsInput, componentKey,
						swiftDetailsReportObjectDTO, reportContext);
			} else if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD
					.equalsIgnoreCase(componentKey)) {
				processMessageDetailsRTextFieldQuery(componentDetails, messagingDetailsInput, componentKey,
						swiftDetailsReportObjectDTO, reportContext);
			} else if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_RCORR.equalsIgnoreCase(componentKey)) {
				boolean sender = false;
				if (null != swiftDetailsReportObjectDTO.getReceiver()) {
					processMessageDetailsRCorrQuery(componentDetails, messagingDetailsInput, componentKey,
							swiftDetailsReportObjectDTO, sender, reportContext);
				}
				if (null != swiftDetailsReportObjectDTO.getSender()) {
					sender = true;
					processMessageDetailsRCorrQuery(componentDetails, messagingDetailsInput, componentKey,
							swiftDetailsReportObjectDTO, sender, reportContext);
				}

			} else if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE
					.equalsIgnoreCase(component.getComponentKey())) {
				processMessageDetailsStxMessageQuery(componentDetails, messagingDetailsInput,
						component.getComponentKey(), swiftDetailsReportObjectDTO, reportContext);
			} else if (MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW
					.equalsIgnoreCase(component.getComponentKey())) {
				processMessageDetailsStxEntryFieldViewQuery(componentDetails, messagingDetailsInput,
						component.getComponentKey(), swiftDetailsReportObjectDTO, reportContext);
			}

		}

	}

	private void processMessageDetailsStxEntryFieldViewQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {
		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportOutput> federatedReportOutputList = new ArrayList<ReportOutput>();
		FederatedReportPromptDTO messageCodesPrompt = new FederatedReportPromptDTO();

		promptsList.add(messagingDetailsInput.getMessageTypePrompt());
		String messageCodesPromptKey = MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_CODES_PROMPT_KEY;
		List<MessageField> messagingFieldList = swiftDetailsReportObjectDTO.getMessageFields();
		String promptValue = messagingFieldList.stream().map(MessageField::getFieldCode)
				.collect(Collectors.joining(","));

		messageCodesPrompt.setPromptKey(messageCodesPromptKey);
		messageCodesPrompt.setPromptValue(promptValue);
		promptsList.add(messageCodesPrompt);
		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsStxFieldEntryViewData(federatedReportOutputList, swiftDetailsReportObjectDTO);

	}

	private void processMessageDetailsStxFieldEntryViewData(List<ReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		if (!federatedReportOutputList.isEmpty()) {
			Map<String, StxEntryFieldViewInfo> fieldInfoMap = new HashMap<String, StxEntryFieldViewInfo>();
			for (ReportOutput federatedReportDefaultOutput : federatedReportOutputList) {
				List<Object> rowData = federatedReportDefaultOutput.getRowData();
				String code = UtilityClass.getStringRepresentation(rowData.get(0));
				String option = UtilityClass.getStringRepresentation(rowData.get(1));
				if ("0".equalsIgnoreCase(option)) {
					option = "";
				}
				String expression = UtilityClass.getStringRepresentation(rowData.get(2));
				Integer version = UtilityClass.getNumberRepresentation(rowData.get(3));
				StxEntryFieldViewInfo stxEntryFieldViewInfo = new StxEntryFieldViewInfo(code, option, expression,
						version);
				String key = code;
				if (null != option) {
					key = key + option;
				}
				if (fieldInfoMap.get(key) == null) {
					fieldInfoMap.put(key, stxEntryFieldViewInfo);
				}
			}
			List<MessageField> messageFields = swiftDetailsReportObjectDTO.getMessageFields();
			for (MessageField messageField : messageFields) {
				messageField.setFieldExpression(
						getMatchingExpression(fieldInfoMap, messageField, swiftDetailsReportObjectDTO));
			}
		}
	}

	private String getMatchingExpression(Map<String, StxEntryFieldViewInfo> fieldInfoMap, MessageField messageField,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		String expression = "";
		String key = messageField.getFieldCode();
		if (null != messageField.getFieldOption()) {
			key = key + messageField.getFieldOption();
		}
		StxEntryFieldViewInfo stxEntryFieldViewInfo = fieldInfoMap.get(key);
		if (stxEntryFieldViewInfo != null) {
			expression = key + MashreqFederatedReportConstants.COLON + stxEntryFieldViewInfo.getExpression();
		} else {
			StxEntryFieldViewInfo CodeOnlyStxEntryFieldViewInfo = fieldInfoMap.get(messageField.getFieldCode());
			if (CodeOnlyStxEntryFieldViewInfo != null) {
				expression = key + MashreqFederatedReportConstants.COLON
						+ CodeOnlyStxEntryFieldViewInfo.getExpression();
			}
		}
		if (expression.isEmpty()) {
			expression = key;
		}
		// if key is 32A/33B, process the value by splitting into parts
		if (MashreqFederatedReportConstants.MESSAGE_CODE_32A.equalsIgnoreCase(key)) {
			String fieldValue = messageField.getFieldValue();
			if (null != fieldValue) {
				if (fieldValue.length() >= 10) {
					String valueDate = fieldValue.substring(0, 6);
					String currency = fieldValue.substring(6, 9);
					String amount = fieldValue.substring(9);
					fieldValue = UtilityClass.combineValues(
							MashreqFederatedReportConstants.DATE_LABEL + MashreqFederatedReportConstants.COLON
									+ valueDate,
							MashreqFederatedReportConstants.CURRENCY_LABEL + MashreqFederatedReportConstants.COLON
									+ currency,
							MashreqFederatedReportConstants.AMOUNT_LABEL + MashreqFederatedReportConstants.COLON
									+ amount);
					messageField.setFieldValue(fieldValue);
				}
			}
		}
		if (MashreqFederatedReportConstants.MESSAGE_CODE_33B.equalsIgnoreCase(key)) {
			String fieldValue = messageField.getFieldValue();
			if (null != fieldValue) {
				if (fieldValue.length() >= 4) {
					String currency = fieldValue.substring(0, 3);
					String amount = fieldValue.substring(3);
					fieldValue = UtilityClass.combineValues(
							MashreqFederatedReportConstants.CURRENCY_LABEL + MashreqFederatedReportConstants.COLON
									+ currency,
							MashreqFederatedReportConstants.AMOUNT_LABEL + MashreqFederatedReportConstants.COLON
									+ amount);
					messageField.setFieldValue(fieldValue);
				}
			}
		}
		return expression;
	}

	private void processMessageDetailsStxMessageQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {
		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportOutput> federatedReportOutputList = new ArrayList<ReportOutput>();
		promptsList.add(messagingDetailsInput.getMessageTypePrompt());
		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());
		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsStxMessageData(federatedReportOutputList, swiftDetailsReportObjectDTO);
	}

	private void processMessageDetailsStxMessageData(List<ReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		if (null != federatedReportOutputList) {
			ReportOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			String swiftInput = UtilityClass.getStringRepresentation(rowData.get(1));
			swiftDetailsReportObjectDTO.setSwiftInput(swiftInput);
		}
	}

	private void processMessageDetailsRCorrQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender, ReportContext reportContext) {
		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO corrBankPrompt = new FederatedReportPromptDTO();
		List<ReportOutput> federatedReportOutputList = new ArrayList<ReportOutput>();

		corrBankPrompt.setPromptKey(MashreqFederatedReportConstants.MESSAGE_DETAILS_CORR_BANK_PROMPT_KEY);
		if (sender) {
			corrBankPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSender());
		} else {
			corrBankPrompt.setPromptValue(swiftDetailsReportObjectDTO.getReceiver());
		}
		promptsList.add(corrBankPrompt);
		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());

		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsRCorrData(federatedReportOutputList, swiftDetailsReportObjectDTO, sender);
	}

	private void processMessageDetailsRCorrData(List<ReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender) {
		if (!federatedReportOutputList.isEmpty()) {
			ReportOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			String institutionName = UtilityClass.getStringRepresentation(rowData.get(1));
			String cityName = UtilityClass.getStringRepresentation(rowData.get(2));
			String countryName = UtilityClass.getStringRepresentation(rowData.get(3));
			String corrDetails = UtilityClass.combineValuesWithBreakTag(institutionName, cityName, countryName);
			if (sender) {
				swiftDetailsReportObjectDTO.setSenderDetails(corrDetails);
			} else {
				swiftDetailsReportObjectDTO.setReceiverDetails(corrDetails);
			}
		}

	}

	private void processMessageDetailsRTextFieldQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {
		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportOutput> federatedReportOutputList = new ArrayList<ReportOutput>();
		updateMessageDetailsInternalPrompts(messagingDetailsInput, promptsList, swiftDetailsReportObjectDTO);

		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());
		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsRTextFieldData(federatedReportOutputList, swiftDetailsReportObjectDTO);

	}

	private void processMessageDetailsRTextFieldData(List<ReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		if (!federatedReportOutputList.isEmpty()) {
			List<MessageField> messageFields = new ArrayList<MessageField>();
			for (ReportOutput federatedReportDefaultOutput : federatedReportOutputList) {
				List<Object> rowData = federatedReportDefaultOutput.getRowData();
				String fieldCode = UtilityClass.getStringRepresentation(rowData.get(0));
				String fieldOption = UtilityClass.getStringRepresentation(rowData.get(1));
				String fieldValue = UtilityClass.getStringRepresentation(rowData.get(2));
				if (null != fieldValue) {
					fieldValue = fieldValue.replaceAll(MashreqFederatedReportConstants.THREE_HASH_NOTATION,
							MashreqFederatedReportConstants.BREAK_TAG);
				}
				messageFields.add(new MessageField(fieldCode, fieldOption, fieldValue));
				if (fieldCode.equalsIgnoreCase(MashreqFederatedReportConstants.PAYMENT_STATUS_CODE)) {
					if (UtilityClass.isGpiTrchEnabledMessage(swiftDetailsReportObjectDTO.getReceiver(),
							swiftDetailsReportObjectDTO.getSender())) {
						if (null != fieldValue) {
							swiftDetailsReportObjectDTO.setDeliveryStatus(deriveActivityStatus(fieldValue));
						}
					}
				}
			}
			swiftDetailsReportObjectDTO.setMessageFields(messageFields);
		}

	}

	private String deriveActivityStatus(String paymentStatus) {
		String activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_DEFAULT;
		if (null != paymentStatus) {
			if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_COMPLETED_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_COMPLETED;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_RETURNED_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_RETURNED;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_GPI_BANK_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_GPI_BANK;
			} else if (paymentStatus
					.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NON_GPI_BANK_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NON_GPI_BANK;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_IN_PROGRESS_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_IN_PROGRESS;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_DOCUMENTS_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_DOCUMENTS;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_FUNDS_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_FUNDS;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NEXT_GPI_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NEXT_GPI;
			} else if (paymentStatus
					.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI;
			} else if (paymentStatus
					.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_PAYMENT_IN_PROGRESS_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_PAYMENT_IN_PROGRESS;
			} else if (paymentStatus.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_PENDING_DOCUMENTS_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_PENDING_DOCUMENTS;
			} else if (paymentStatus
					.contains(MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_CREDIT_COVER_CODE)) {
				activityStatus = MashreqFederatedReportConstants.PAYMENT_STATUS_AWAITING_CREDIT_COVER;
			}
		}
		return activityStatus;
	}

	private void processMessageDetailsRIntvQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {

		String activityStatus = MashreqFederatedReportConstants.COMPLETED_ACTIVITY_STATUS;
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportOutput> federatedReportOutputList = new ArrayList<ReportOutput>();
		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();

		if (UtilityClass.isOutgoingPaymentMessage(messagingDetailsInput.getMessageSubFormatPrompt().getPromptValue(),
				messagingDetailsInput.getMessageTypePrompt().getPromptValue())) {

			updateMessageDetailsInternalPrompts(messagingDetailsInput, promptsList, swiftDetailsReportObjectDTO);

			context.setQueryId(componentDetails.getId());
			context.setQueryKey(componentDetails.getQueryKey());
			context.setQueryString(componentDetails.getQuery());
			context.setPrompts(promptsList);
			context.setExecutionId(reportContext.getExecutionId());

			federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
			activityStatus = processMessageDetailsRIntvData(federatedReportOutputList, swiftDetailsReportObjectDTO);

		} else if (UtilityClass.isGpiIpalaEnabledMessage(swiftDetailsReportObjectDTO.getReceiver(),
				swiftDetailsReportObjectDTO.getSender())) {
			activityStatus = MashreqFederatedReportConstants.RINTV_MESG_ACK;
		}
		swiftDetailsReportObjectDTO.setDeliveryStatus(activityStatus);
	}

	private String processMessageDetailsRIntvData(List<ReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		String activityStatus = MashreqFederatedReportConstants.RINTV_MESG_LIVE;
		if (!federatedReportOutputList.isEmpty()) {
			for (ReportOutput federatedReportOutput : federatedReportOutputList) {

				List<Object> rowDataObjectList = federatedReportOutput.getRowData();
				String intvName = UtilityClass.getStringRepresentation(rowDataObjectList.get(0));
				String mpfnName = UtilityClass.getStringRepresentation(rowDataObjectList.get(1));
				String networkDeliveryStatus = UtilityClass.getStringRepresentation(rowDataObjectList.get(3));
				if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_SI_TO_SWIFT)
						&& null != networkDeliveryStatus) {
					if (intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						return MashreqFederatedReportConstants.RINTV_MESG_ACK;
					} else if (intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_CREATED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_NACKED)) {
						return MashreqFederatedReportConstants.RINTV_MESG_NACK;
					}
				}

			}
		}
		return activityStatus;
	}

	private void updateMessageDetailsInternalPrompts(MessageDetailsFederatedReportInput messagingDetailsInput,
			List<FederatedReportPromptDTO> promptsList, SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		FederatedReportPromptDTO aidPrompt = new FederatedReportPromptDTO();
		aidPrompt.setPromptKey(MashreqFederatedReportConstants.AID_PROMPT_KEY);
		aidPrompt.setPromptValue(swiftDetailsReportObjectDTO.getAid());
		promptsList.add(aidPrompt);
		FederatedReportPromptDTO sumidhPrompt = new FederatedReportPromptDTO();
		sumidhPrompt.setPromptKey(MashreqFederatedReportConstants.S_UMIDH_PROMPT_KEY);
		sumidhPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSumidh());
		promptsList.add(sumidhPrompt);
		FederatedReportPromptDTO sumidlPrompt = new FederatedReportPromptDTO();
		sumidlPrompt.setPromptKey(MashreqFederatedReportConstants.S_UMIDL_PROMPT_KEY);
		sumidlPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSumidl());
		promptsList.add(sumidlPrompt);
	}

	private void processMessageDetailsRMesgQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, ReportContext reportContext) {

		FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportOutput> flexReportExecuteResponse = new ArrayList<ReportOutput>();
		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		promptsList.add(messagingDetailsInput.getReferenceNumPrompt());
		promptsList.add(messagingDetailsInput.getMessageSubFormatPrompt());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());
		flexReportExecuteResponse = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsRMesgData(flexReportExecuteResponse, swiftDetailsReportObjectDTO);

	}

	private void processMessageDetailsRMesgData(List<ReportOutput> flexReportExecuteResponse,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		Boolean messageFound = Boolean.FALSE;
		if (!flexReportExecuteResponse.isEmpty()) {
			messageFound = Boolean.TRUE;
			ReportOutput federatedReportDefaultOutput = flexReportExecuteResponse.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			swiftDetailsReportObjectDTO.setAid(UtilityClass.getStringRepresentation(rowData.get(0)));
			swiftDetailsReportObjectDTO.setSumidh(UtilityClass.getStringRepresentation(rowData.get(1)));
			swiftDetailsReportObjectDTO.setSumidl(UtilityClass.getStringRepresentation(rowData.get(2)));
			swiftDetailsReportObjectDTO.setDescription(UtilityClass.getStringRepresentation(rowData.get(3)));
			swiftDetailsReportObjectDTO.setPriority(UtilityClass.getStringRepresentation(rowData.get(4)));
			swiftDetailsReportObjectDTO.setReference(UtilityClass.getStringRepresentation(rowData.get(5)));
			swiftDetailsReportObjectDTO.setReceiver(UtilityClass.getStringRepresentation(rowData.get(6)));
			swiftDetailsReportObjectDTO.setSender(UtilityClass.getStringRepresentation(rowData.get(7)));
		}
		swiftDetailsReportObjectDTO.setMessageFound(messageFound);

	}

	private ReportComponentDetailDTO getMatchedComponentDetails(Set<ReportComponentDetailDTO> componentDetailsList,
			String componentKey) {
		ReportComponentDetailDTO componentDetailsObject = componentDetailsList.stream()
				.filter(component -> componentKey.equalsIgnoreCase(component.getQueryKey())).findAny().orElse(null);
		return componentDetailsObject;
	}

	private SWIFTDetailedFederatedReportDTO populateBaseInputContext(List<ReportPromptsInstanceDTO> list) {
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

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> list, String promptKey) {
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = list.stream()
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