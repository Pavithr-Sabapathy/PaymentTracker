package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.MessageField;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.SWIFTDetailedFederatedReportDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsFederatedReportOutput;
import com.mashreq.paymentTracker.dto.StxEntryFieldViewInfo;
import com.mashreq.paymentTracker.dto.SwiftDetailedReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.SwiftDetailsReportObjectDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
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
	LinkReportService linkReportService;

	private static final Logger log = LoggerFactory.getLogger(SwiftDetailedReportServiceImpl.class);
	private static final String FILENAME = "SwiftDetailedReportServiceImpl";

	public SwiftDetailedReportExecuteResponseData processSwiftDetailReport(String reportName,
			ReportProcessingRequest reportProcessingRequest) {
		SwiftDetailedReportExecuteResponseData responseData = new SwiftDetailedReportExecuteResponseData();
		ReportExecuteResponseMetaDTO reportExecutionMetaDTO = new ReportExecuteResponseMetaDTO();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Map<String, Object>> swiftData = null ;
		Reports reportObject = reportConfigurationService.fetchReportByName(reportName);
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(reportObject.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportObject.getId());
		} else {
			Date startTime = new Date();
			List<Components> componentsList = componentsOptional.get();
			for (Components component : componentsList) {
				if (component.getActive().equals(CheckType.YES.toString())) {
					SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO = populateBaseInputContext(
							reportObject.getPromptList(), reportProcessingRequest.getPrompts());
					// TODO
					// swiftDetailedReportDTO.setComponent(component);
					List<SWIFTMessageDetailsFederatedReportOutput> messageDetails = processSwiftDetailedReport(
							swiftDetailedReportDTO, component);
					swiftData = populateSwiftDetailedReportData(messageDetails);
					reportExecuteResponseCloumnDefList = populateColumnDef(reportObject);
				}

			}
			responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
			responseData.setData(swiftData);
			Date endTime = new Date();
			reportExecutionMetaDTO.setStartTime(startTime.toString());
			reportExecutionMetaDTO.setEndTime(endTime.toString());
			reportExecutionMetaDTO.setReportId(reportName);
			responseData.setMeta(reportExecutionMetaDTO);
		}
		return responseData;
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> processSwiftDetailedReport(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, Components component) {
		FederatedReportPromptDTO detailedType = swiftDetailedReportDTO.getDetailedType();
		switch (detailedType.getPromptValue()) {
		case "RMESG":
		case "RINTV":
			return populateRMesgDetailData(swiftDetailedReportDTO, component);

		}
		return null;
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> populateRMesgDetailData(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, Components component) {
		MessageDetailsFederatedReportInput reportInput = new MessageDetailsFederatedReportInput();
		reportInput.setMessageSubFormatPrompt(swiftDetailedReportDTO.getMessageSubFormatPrompt());
		reportInput.setMessageTypePrompt(swiftDetailedReportDTO.getMessageTypePrompt());
		reportInput.setReferenceNumPrompt(swiftDetailedReportDTO.getReferenceNumPrompt());
		return processMessageDetailsReport(reportInput, component);
	}

	private List<SWIFTMessageDetailsFederatedReportOutput> processMessageDetailsReport(
			MessageDetailsFederatedReportInput messagingDetailsInput, Components component) {
		List<ComponentDetails> componentDetailsList = component.getComponentDetailsList();
		List<SWIFTMessageDetailsFederatedReportOutput> messageDetails = new ArrayList<SWIFTMessageDetailsFederatedReportOutput>();

		SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO = new SwiftDetailsReportObjectDTO();
		if (!componentDetailsList.isEmpty()) {
			String rmesgQuery = ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG;
			processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput, rmesgQuery,
					swiftDetailsReportObjectDTO);
			if (swiftDetailsReportObjectDTO.isMessageFound()) {
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RINTV, swiftDetailsReportObjectDTO);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD, swiftDetailsReportObjectDTO);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RCORR, swiftDetailsReportObjectDTO);
				processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
						ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE, swiftDetailsReportObjectDTO);
				// if message fields are there, we need to process this.
				if (!swiftDetailsReportObjectDTO.getMessageFields().isEmpty()) {
					processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput,
							ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW,
							swiftDetailsReportObjectDTO);
				}
				processSwiftMessageDetailContext(swiftDetailsReportObjectDTO, messageDetails);
			}
		}
		return messageDetails;
	}

	private void processSwiftMessageDetailContext(SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO,
			List<SWIFTMessageDetailsFederatedReportOutput> messageDetails) {
		// TODO
		Long compDetailId = 0L;
		addToMessageDetails(messageDetails, ApplicationConstants.DESCRIPTION_LABEL,
				swiftDetailsReportObjectDTO.getDescription(), compDetailId);
		addToMessageDetails(messageDetails, ApplicationConstants.DELIVERY_STATUS_LABEL,
				swiftDetailsReportObjectDTO.getDeliveryStatus(), compDetailId);
		addToMessageDetails(messageDetails, ApplicationConstants.PRIORITY_LABEL,
				swiftDetailsReportObjectDTO.getPriority(), compDetailId);
		addToMessageDetails(messageDetails, ApplicationConstants.INPUT_REFERENCE_LABEL,
				swiftDetailsReportObjectDTO.getReference(), compDetailId);
		addToMessageDetails(messageDetails, ApplicationConstants.SWIFT_INPUT_LABEL,
				swiftDetailsReportObjectDTO.getSwiftInput(), compDetailId);

		String sender = swiftDetailsReportObjectDTO.getSender();
		String senderDetails = swiftDetailsReportObjectDTO.getSenderDetails();
		String senderValue = UtilityClass.combineValuesWithBreakTag(sender, senderDetails);
		addToMessageDetails(messageDetails, ApplicationConstants.SENDER_LABEL, senderValue, compDetailId);

		String receiver = swiftDetailsReportObjectDTO.getReceiver();
		String receiverDetails = swiftDetailsReportObjectDTO.getReceiverDetails();
		String receiverValue = UtilityClass.combineValuesWithBreakTag(receiver, receiverDetails);
		addToMessageDetails(messageDetails, ApplicationConstants.RECEIVER_LABEL, receiverValue, compDetailId);

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

	private void processMessageDetailsComponentDetail(Components component, List<ComponentDetails> componentDetailsList,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		if (!componentDetailsList.isEmpty()) {
			ComponentDetails componentDetails = getMatchedComponentDetails(componentDetailsList, componentDetailKey);
			if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG.equalsIgnoreCase(componentDetailKey)) {

				processMessageDetailsRMesgQuery(component, componentDetails, messagingDetailsInput, componentDetailKey,
						swiftDetailsReportObjectDTO);
			} else if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RINTV.equalsIgnoreCase(componentDetailKey)) {
				processMessageDetailsRIntvQuery(component, componentDetails, messagingDetailsInput, componentDetailKey,
						swiftDetailsReportObjectDTO);
			} else if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD.equalsIgnoreCase(componentDetailKey)) {
				processMessageDetailsRTextFieldQuery(component, componentDetails, messagingDetailsInput,
						componentDetailKey, swiftDetailsReportObjectDTO);
			} else if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RCORR.equalsIgnoreCase(componentDetailKey)) {
				boolean sender = false;
				if (null != swiftDetailsReportObjectDTO.getReceiver()) {
					processMessageDetailsRCorrQuery(component, componentDetails, messagingDetailsInput,
							componentDetailKey, swiftDetailsReportObjectDTO, sender);
				}
				if (null != swiftDetailsReportObjectDTO.getSender()) {
					sender = true;
					processMessageDetailsRCorrQuery(component, componentDetails, messagingDetailsInput,
							componentDetailKey, swiftDetailsReportObjectDTO, sender);
				}

			} else if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE
					.equalsIgnoreCase(componentDetailKey)) {
				processMessageDetailsStxMessageQuery(component, componentDetails, messagingDetailsInput,
						componentDetailKey, swiftDetailsReportObjectDTO);
			} else if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW
					.equalsIgnoreCase(componentDetailKey)) {
				processMessageDetailsStxEntryFieldViewQuery(component, componentDetails, messagingDetailsInput,
						componentDetailKey, swiftDetailsReportObjectDTO);
			}

		}
	}

	private void processMessageDetailsStxEntryFieldViewQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		prompts.add(messagingDetailsInput.getMessageTypePrompt());
		String messageCodesPromptKey = ApplicationConstants.MESSAGE_DETAILS_MESSAGE_CODES_PROMPT_KEY;
		List<MessageField> messagingFieldList = swiftDetailsReportObjectDTO.getMessageFields();
		String promptValue = messagingFieldList.stream().map(MessageField::getFieldCode)
				.collect(Collectors.joining(","));
		FederatedReportPromptDTO messageCodesPrompt = new FederatedReportPromptDTO();
		messageCodesPrompt.setPromptKey(messageCodesPromptKey);
		messageCodesPrompt.setPromptValue(promptValue);
		prompts.add(messageCodesPrompt);
		List<FederatedReportOutput> federatedReportOutputList = processComponentDetail(component, componentDetails,
				prompts);
		processMessageDetailsStxFieldEntryViewData(federatedReportOutputList, swiftDetailsReportObjectDTO);
	}

	private void processMessageDetailsStxFieldEntryViewData(List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		if (!federatedReportOutputList.isEmpty()) {
			Map<String, StxEntryFieldViewInfo> fieldInfoMap = new HashMap<String, StxEntryFieldViewInfo>();
			for (FederatedReportOutput federatedReportDefaultOutput : federatedReportOutputList) {
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
			expression = key + ApplicationConstants.COLON + stxEntryFieldViewInfo.getExpression();
		} else {
			StxEntryFieldViewInfo CodeOnlyStxEntryFieldViewInfo = fieldInfoMap.get(messageField.getFieldCode());
			if (CodeOnlyStxEntryFieldViewInfo != null) {
				expression = key + ApplicationConstants.COLON + CodeOnlyStxEntryFieldViewInfo.getExpression();
			}
		}
		if (null == expression) {
			expression = key;
		}
		// if key is 32A/33B, process the value by splitting into parts
		if (ApplicationConstants.MESSAGE_CODE_32A.equalsIgnoreCase(key)) {
			String fieldValue = messageField.getFieldValue();
			if (null != fieldValue) {
				if (fieldValue.length() >= 10) {
					String valueDate = fieldValue.substring(0, 6);
					String currency = fieldValue.substring(6, 9);
					String amount = fieldValue.substring(9);
					fieldValue = UtilityClass.combineValues(
							ApplicationConstants.DATE_LABEL + ApplicationConstants.COLON + valueDate,
							ApplicationConstants.CURRENCY_LABEL + ApplicationConstants.COLON + currency,
							ApplicationConstants.AMOUNT_LABEL + ApplicationConstants.COLON + amount);
					messageField.setFieldValue(fieldValue);
				}
			}
		}
		if (ApplicationConstants.MESSAGE_CODE_33B.equalsIgnoreCase(key)) {
			String fieldValue = messageField.getFieldValue();
			if (null != fieldValue) {
				if (fieldValue.length() >= 4) {
					String currency = fieldValue.substring(0, 3);
					String amount = fieldValue.substring(3);
					fieldValue = UtilityClass.combineValues(
							ApplicationConstants.CURRENCY_LABEL + ApplicationConstants.COLON + currency,
							ApplicationConstants.AMOUNT_LABEL + ApplicationConstants.COLON + amount);
					messageField.setFieldValue(fieldValue);
				}
			}
		}
		return expression;
	}

	private void processMessageDetailsStxMessageQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		prompts.add(messagingDetailsInput.getMessageTypePrompt());
		List<FederatedReportOutput> federatedReportOutputList = processComponentDetail(component, componentDetails,
				prompts);
		processMessageDetailsStxMessageData(federatedReportOutputList, swiftDetailsReportObjectDTO);
	}

	private void processMessageDetailsStxMessageData(List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		if (null != federatedReportOutputList) {
			FederatedReportOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			String swiftInput = UtilityClass.getStringRepresentation(rowData.get(1));
			swiftDetailsReportObjectDTO.setSwiftInput(swiftInput);
		}
	}

	private void processMessageDetailsRCorrQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender) {
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO corrBankPrompt = new FederatedReportPromptDTO();
		corrBankPrompt.setPromptKey(ApplicationConstants.MESSAGE_DETAILS_CORR_BANK_PROMPT_KEY);
		if (sender) {
			corrBankPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSender());
		} else {
			corrBankPrompt.setPromptValue(swiftDetailsReportObjectDTO.getReceiver());
		}
		prompts.add(corrBankPrompt);
		List<FederatedReportOutput> federatedReportOutputList = processComponentDetail(component, componentDetails,
				prompts);
		processMessageDetailsRCorrData(federatedReportOutputList, swiftDetailsReportObjectDTO, sender);
	}

	private void processMessageDetailsRCorrData(List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender) {
		// TODO
		String corrDetails = null;
		if (!federatedReportOutputList.isEmpty()) {
			FederatedReportOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			String institutionName = UtilityClass.getStringRepresentation(rowData.get(1));
			String cityName = UtilityClass.getStringRepresentation(rowData.get(2));
			String countryName = UtilityClass.getStringRepresentation(rowData.get(3));
			// String corrDetails = combineValuesWithBreakTag(institutionName, cityName,
			// countryName);
			if (sender) {
				swiftDetailsReportObjectDTO.setSenderDetails(corrDetails);
			} else {
				swiftDetailsReportObjectDTO.setReceiverDetails(corrDetails);
			}
		}
	}

	private void processMessageDetailsRTextFieldQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		updateMessageDetailsInternalPrompts(messagingDetailsInput, promptsList, swiftDetailsReportObjectDTO);
		List<FederatedReportOutput> federatedReportOutputList = processComponentDetail(component, componentDetails,
				promptsList);
		processMessageDetailsRTextFieldData(federatedReportOutputList, swiftDetailsReportObjectDTO);

	}

	private void processMessageDetailsRTextFieldData(List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		if (!federatedReportOutputList.isEmpty()) {
			List<MessageField> messageFields = new ArrayList<MessageField>();
			for (FederatedReportOutput federatedReportDefaultOutput : federatedReportOutputList) {
				List<Object> rowData = federatedReportDefaultOutput.getRowData();
				String fieldCode = UtilityClass.getStringRepresentation(rowData.get(0));
				String fieldOption = UtilityClass.getStringRepresentation(rowData.get(1));
				String fieldValue = UtilityClass.getStringRepresentation(rowData.get(2));
				if (null != fieldValue) {
					fieldValue = fieldValue.replaceAll(ApplicationConstants.THREE_HASH_NOTATION,
							ApplicationConstants.BREAK_TAG);
				}
				messageFields.add(new MessageField(fieldCode, fieldOption, fieldValue));
				if (fieldCode.equalsIgnoreCase(ApplicationConstants.PAYMENT_STATUS_CODE)) {
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
		String activityStatus = ApplicationConstants.PAYMENT_STATUS_DEFAULT;
		if (null != paymentStatus) {
			if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_COMPLETED_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_COMPLETED;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_RETURNED_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_RETURNED;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_FORWARDED_GPI_BANK_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_FORWARDED_GPI_BANK;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_FORWARDED_NON_GPI_BANK_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_FORWARDED_NON_GPI_BANK;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_IN_PROGRESS_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_IN_PROGRESS;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_AWAITING_DOCUMENTS_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_AWAITING_DOCUMENTS;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_AWAITING_FUNDS_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_AWAITING_FUNDS;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_FORWARDED_NEXT_GPI_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_FORWARDED_NEXT_GPI;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_PAYMENT_IN_PROGRESS_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_PAYMENT_IN_PROGRESS;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_PENDING_DOCUMENTS_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_PENDING_DOCUMENTS;
			} else if (paymentStatus.contains(ApplicationConstants.PAYMENT_STATUS_AWAITING_CREDIT_COVER_CODE)) {
				activityStatus = ApplicationConstants.PAYMENT_STATUS_AWAITING_CREDIT_COVER;
			}
		}
		return activityStatus;
	}

	private void processMessageDetailsRIntvQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		String activityStatus = ApplicationConstants.COMPLETED_ACTIVITY_STATUS;
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();

		if (UtilityClass.isOutgoingPaymentMessage(messagingDetailsInput.getMessageSubFormatPrompt().getPromptValue(),
				messagingDetailsInput.getMessageTypePrompt().getPromptValue())) {

			updateMessageDetailsInternalPrompts(messagingDetailsInput, promptsList, swiftDetailsReportObjectDTO);
			List<FederatedReportOutput> federatedReportOutputList = processComponentDetail(component, componentDetails,
					promptsList);

			activityStatus = processMessageDetailsRIntvData(federatedReportOutputList, swiftDetailsReportObjectDTO);

		} else if (UtilityClass.isGpiIpalaEnabledMessage(swiftDetailsReportObjectDTO.getReceiver(),
				swiftDetailsReportObjectDTO.getSender())) {
			activityStatus = ApplicationConstants.RINTV_MESG_ACK;
		}
		swiftDetailsReportObjectDTO.setDeliveryStatus(activityStatus);
	}

	private void updateMessageDetailsInternalPrompts(MessageDetailsFederatedReportInput messagingDetailsInput,
			List<FederatedReportPromptDTO> promptsList, SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		FederatedReportPromptDTO aidPrompt = new FederatedReportPromptDTO();
		aidPrompt.setPromptKey(ApplicationConstants.AID_PROMPT_KEY);
		aidPrompt.setPromptValue(swiftDetailsReportObjectDTO.getAid());
		promptsList.add(aidPrompt);
		FederatedReportPromptDTO sumidhPrompt = new FederatedReportPromptDTO();
		sumidhPrompt.setPromptKey(ApplicationConstants.S_UMIDH_PROMPT_KEY);
		sumidhPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSumidh());
		promptsList.add(sumidhPrompt);
		FederatedReportPromptDTO sumidlPrompt = new FederatedReportPromptDTO();
		sumidhPrompt.setPromptKey(ApplicationConstants.S_UMIDL_PROMPT_KEY);
		sumidhPrompt.setPromptValue(swiftDetailsReportObjectDTO.getSumidl());
		promptsList.add(sumidlPrompt);
		promptsList.add(messagingDetailsInput.getReferenceNumPrompt());
		promptsList.add(messagingDetailsInput.getMessageSubFormatPrompt());

	}

	private String processMessageDetailsRIntvData(List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		String activityStatus = ApplicationConstants.RINTV_MESG_LIVE;
		if (!federatedReportOutputList.isEmpty()) {
			for (FederatedReportOutput federatedReportOutput : federatedReportOutputList) {

				List<Object> rowDataObjectList = federatedReportOutput.getRowData();
				String intvName = UtilityClass.getStringRepresentation(rowDataObjectList.get(0));
				String mpfnName = UtilityClass.getStringRepresentation(rowDataObjectList.get(1));
				String networkDeliveryStatus = UtilityClass.getStringRepresentation(rowDataObjectList.get(3));
				if (mpfnName.equalsIgnoreCase(ApplicationConstants.RINTV_MPFN_SI_TO_SWIFT)
						&& null != networkDeliveryStatus) {
					if (intvName.equalsIgnoreCase(ApplicationConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus
									.equalsIgnoreCase(ApplicationConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						return ApplicationConstants.RINTV_MESG_ACK;
					} else if (intvName.equalsIgnoreCase(ApplicationConstants.RINTV_NAME_INSTANCE_CREATED)
							&& networkDeliveryStatus
									.equalsIgnoreCase(ApplicationConstants.RAPPE_NETWORK_DELIVERY_STATUS_NACKED)) {
						return ApplicationConstants.RINTV_MESG_NACK;
					}
				}

			}
		}
		return activityStatus;
	}

	private ComponentDetails getMatchedComponentDetails(List<ComponentDetails> componentDetailsList,
			String componentDetailKey) {

		ComponentDetails componentDetailsObject = componentDetailsList.stream()
				.filter(component -> componentDetailKey.equalsIgnoreCase(component.getQueryKey())).findAny()
				.orElse(null);
		return componentDetailsObject;
	}

	private void processMessageDetailsRMesgQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<FederatedReportOutput> federatedReportOutputList = new ArrayList<FederatedReportOutput>();
		promptsList.add(messagingDetailsInput.getReferenceNumPrompt());
		promptsList.add(messagingDetailsInput.getMessageSubFormatPrompt());
		federatedReportOutputList = processComponentDetail(component, componentDetails, promptsList);
		processMessageDetailsRMesgData(federatedReportOutputList, swiftDetailsReportObjectDTO);
	}

	private SwiftDetailsReportObjectDTO processMessageDetailsRMesgData(
			List<FederatedReportOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		Boolean messageFound = Boolean.FALSE;
		if (!federatedReportOutputList.isEmpty()) {
			messageFound = Boolean.TRUE;
			FederatedReportOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
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
		return swiftDetailsReportObjectDTO;
	}

	private List<FederatedReportOutput> processComponentDetail(Components component, ComponentDetails componentDetails,
			List<FederatedReportPromptDTO> promptsList) {
		// TODO Auto-generated method stub
		// Ramalashmi - Common for logic... Establish connection replace the promtps
		// value and execute the query
		String queryString = component.getComponentDetailsList().get(0).getQuery().replaceAll("~", "");
		StringBuilder queryBuilder = new StringBuilder();
		List<FederatedReportOutput> federatedReportOutputList = new ArrayList<FederatedReportOutput>();
		promptsList.forEach(prompts -> {
			if (null != prompts.getPromptKey() && queryString.indexOf(prompts.getPromptKey()) > 0) {
				if (null != prompts.getPromptValue()) {
					String promptsValue = prompts.getPromptValue();
					queryBuilder.append(queryString.replace(prompts.getPromptKey(),promptsValue));
				}
			}
		});

		try {
			Class.forName(ApplicationConstants.DRIVER_CLASS_NAME);
			Connection connection;
			connection = DriverManager.getConnection(ApplicationConstants.SWIFT_DATABASE_URL,
					ApplicationConstants.DATABASE_USERNAME, ApplicationConstants.DATABASE_PASSWORD);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
			if (resultSet != null) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (resultSet.next()) {
					FederatedReportOutput federatedReportOutput = new FederatedReportOutput();
					List<Object> rowData = new ArrayList<Object>();
					List<String> columnLabelList = new ArrayList<String>();
					for (int index = 1; index <= columnCount; index++) {
						Object colValue = resultSet.getObject(index);
						rowData.add(colValue);
						String columnLabel = metaData.getColumnLabel(index);
						columnLabelList.add(columnLabel);
					}
					federatedReportOutput.setRowData(rowData);
					federatedReportOutput.setColumnLabels(columnLabelList);
					federatedReportOutputList.add(federatedReportOutput);
				}
			}
			connection.close();
			return federatedReportOutputList;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return federatedReportOutputList;
	}

	private SWIFTDetailedFederatedReportDTO populateBaseInputContext(List<Prompts> reportInstancePromptsList,
			List<PromptsProcessingRequest> uiPromptsRequest) {
		SWIFTDetailedFederatedReportDTO SWIFTDetailedFederatedReportDTO = new SWIFTDetailedFederatedReportDTO();
		Map<String, PromptsProcessingRequest> promptsRequestMapping = uiPromptsRequest.stream()
				.collect(Collectors.toMap(PromptsProcessingRequest::getKey, Function.identity()));
		List<FederatedReportPromptDTO> federatedReportPromptDTOList = new ArrayList<FederatedReportPromptDTO>();
		promptsRequestMapping.forEach((promptKey, promptValue) -> {
			FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
			federatedReportPromptDTO.setPromptKey(promptKey);
			federatedReportPromptDTO.setPromptValue(promptValue.getPromptValue());
			federatedReportPromptDTOList.add(federatedReportPromptDTO);
		});

		federatedReportPromptDTOList.stream().forEach(fedratedReportPrompt -> {
			if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.AID_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setAidPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.S_UMIDH_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setUmidhPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.S_UMIDL_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setUmidlPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setDetailedType(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setReferenceNumPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setMessageTypePrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setMessageSubFormatPrompt(fedratedReportPrompt);
			}
		});
		return SWIFTDetailedFederatedReportDTO;
	}

	private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(Reports reportObject) {
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = new ArrayList<ReportExecuteResponseColumnDefDTO>();
		ReportExecuteResponseColumnDefDTO reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
		List<Metrics> metricsList = reportObject.getMetricsList();
		metricsList.stream().forEach(metrics -> {
			reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
			reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef);
		});
		try {
			LinkedReportRequestDTO linkedReportRequestDTO = linkReportService
					.fetchLinkedReportByReportId(reportObject.getId());

			if (null != linkedReportRequestDTO) {
				Long sourceMetricValue = linkedReportRequestDTO.getSourceMetricId();
				reportExecuteResponseCloumnDef.setLinkExists(null != sourceMetricValue ? true : false);
			}
		} catch (JpaSystemException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		}
		return reportExecuteResponseCloumnDefList;
	}

	private List<Map<String, Object>> populateSwiftDetailedReportData(List<SWIFTMessageDetailsFederatedReportOutput> swiftDetailedReports) {
		List<Map<String, Object>> swiftDetailedReportDataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapData = new HashMap<String, Object>();
		swiftDetailedReports.stream().forEach(swiftReport -> {
			mapData.put(swiftReport.getKey(), swiftReport.getValue());
			
		});
		swiftDetailedReportDataList.add(mapData);
		return swiftDetailedReportDataList;
	}

	@Override
	public APIResponse populateSuccessAPIRespone(SwiftDetailedReportExecuteResponseData swiftDetailedReport) {
		APIResponse swiftDetailedReportApiResponse = new APIResponse();
		swiftDetailedReportApiResponse.setData(swiftDetailedReport);
		swiftDetailedReportApiResponse.setMessage("Report Execution Success");
		swiftDetailedReportApiResponse.setStatus(Boolean.TRUE);
		return swiftDetailedReportApiResponse;
	}

}