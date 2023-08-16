package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FircosoftReportContext;
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.GatewayDataMessageContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class FircosoftReportConnector extends ReportConnector {
	private static final Logger log = LoggerFactory.getLogger(SwiftReportConnector.class);

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof PaymentInvestigationReportInput piReportInput) {
			processPaymentInvestigationReport(piReportInput, reportContext);
		}
		return null;
	}

	private List<PaymentInvestigationReportOutput> processPaymentInvestigationReport(
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		List<PaymentInvestigationReportOutput> outputList = new ArrayList<PaymentInvestigationReportOutput>();
		ReportComponentDTO component = piReportInput.getComponent();
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		List<FircosoftReportContext> fircosoftContextList = new ArrayList<FircosoftReportContext>();
		if (!componentDetailList.isEmpty()) {
			// if all the message are processed already, we dont need to process fircosoft
			List<String> referenceNums = getReferenceNums(piReportInput.getGatewayDataContext());
			if (referenceNums.isEmpty()) {
				return outputList;
			}
			String fofaMessageKey = MashreqFederatedReportConstants.FIRCOSOFT_FOFA_MESSAGE;
			processComponent(component, piReportInput, fofaMessageKey, reportContext, fircosoftContextList);
			// if we don't get fofa message, we dont need to process hits_view
			if (!fircosoftContextList.isEmpty()) {
				processComponent(component, piReportInput, MashreqFederatedReportConstants.FIRCOSOFT_HISTORY_ALERT_VIEW,
						reportContext, fircosoftContextList);
			}
			processFircosoftContextList(fircosoftContextList, piReportInput);
		}
		return outputList;
	}

	private void processFircosoftContextList(List<FircosoftReportContext> fircosoftContextList,
			PaymentInvestigationReportInput piReportInput) {
		for (FircosoftReportContext context : fircosoftContextList) {
			String mesgType = context.getMesgType();
			for (PaymentInvestigationReportOutput complianceRecord : context.getComplianceRecords()) {
				complianceRecord.setActivity(getActivity(mesgType, complianceRecord.getActivity()));
			}
		}
		// populate the data back to gateway context
		GatewayDataContext gatewayDataContext = piReportInput.getGatewayDataContext();
		GatewayDataMessageContext incomingMessage = gatewayDataContext.getIncomingMessage();
		if (incomingMessage != null) {
			populateDataBackToContext(fircosoftContextList, incomingMessage);

		}

		GatewayDataMessageContext outgoingMessage = gatewayDataContext.getOutgoingMessage();
		if (outgoingMessage != null) {
			populateDataBackToContext(fircosoftContextList, outgoingMessage);

		}

		Map<String, GatewayDataMessageContext> incomingEnquiries = gatewayDataContext.getIncomingEnquiries();
		if (!incomingEnquiries.isEmpty()) {
			Collection<GatewayDataMessageContext> values = incomingEnquiries.values();
			for (GatewayDataMessageContext value : values) {
				populateDataBackToContext(fircosoftContextList, value);
			}
		}
	}

	private void populateDataBackToContext(List<FircosoftReportContext> fircosoftContextList,
			GatewayDataMessageContext messageContext) {
		List<PaymentInvestigationReportOutput> matchingScannedRecord = getMatchingScannedRecord(fircosoftContextList,
				messageContext);
		if (!matchingScannedRecord.isEmpty()) {
			messageContext.setScreeningProcessedRecord(matchingScannedRecord);
		}
	}

	private List<PaymentInvestigationReportOutput> getMatchingScannedRecord(
			List<FircosoftReportContext> fircosoftContextList, GatewayDataMessageContext messageContext) {
		List<PaymentInvestigationReportOutput> matchedScannedRecords = new ArrayList<PaymentInvestigationReportOutput>();
		for (FircosoftReportContext context : fircosoftContextList) {
			String messageType = messageContext.getMessageType();
			if (messageType.endsWith("N")) {
				messageType = messageType.charAt(0) + " " + messageType.substring(1, messageType.length() - 1);
			}
			if (!messageType.contains(" ")) {
				messageType = messageType.charAt(0) + " " + messageType.substring(1, messageType.length());
			}
			if (context.getReferenceNum().equalsIgnoreCase(messageContext.getMessageRef())
					&& context.getMesgType().equalsIgnoreCase(messageType)) {
				matchedScannedRecords = context.getComplianceRecords();
			}
		}
		return matchedScannedRecords;
	}

	private String getActivity(String mesgType, String activityOrigin) {
		String activity = MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY;
		if ((!MashreqFederatedReportConstants.INCOMING_PAYMENT_CODES_LIST.contains(mesgType))
				&& !(MashreqFederatedReportConstants.OUTGOING_PAYMENT_CODES_LIST.contains(mesgType))) {
			activity = MashreqFederatedReportConstants.GATEWAY_MESSAGE_SCREENING_PROCESSED_ACTIVITY;
		}
		if (activityOrigin.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY)) {
			return activity + " : " + mesgType;
		} else {
			return activity + "(" + activityOrigin + ")" + " : " + mesgType;
		}
	}

	private void processComponent(ReportComponentDTO component, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext, List<FircosoftReportContext> fircosoftContextList) {
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		ReportComponentDetailDTO componentDetail = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		List<FircosoftReportContext> fircosoftReportContextList = new ArrayList<FircosoftReportContext>();
		if (componentDetail != null) {
			if (MashreqFederatedReportConstants.FIRCOSOFT_FOFA_MESSAGE.equalsIgnoreCase(componentDetailKey)) {
				ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail,
						piReportInput, reportContext);
				updatePrompts(componentDetailKey, piReportInput, context, null);
				List<ReportDefaultOutput> outputList = queryExecutorService.executeQuery(componentDetail, context);
				List<FircosoftReportContext> fofaData = populateFofaData(outputList, piReportInput);
				if (!fofaData.isEmpty()) {
					fircosoftReportContextList.addAll(fofaData);
				}
			} else if (MashreqFederatedReportConstants.FIRCOSOFT_HISTORY_ALERT_VIEW
					.equalsIgnoreCase(componentDetailKey)) {
				for (FircosoftReportContext fircosoftReportContext : fircosoftReportContextList) {
					ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail,
							piReportInput, reportContext);
					updatePrompts(componentDetailKey, piReportInput, context, fircosoftReportContext);
					List<ReportDefaultOutput> outputList = queryExecutorService.executeQuery(componentDetail, context);
					updateFofaDataWithHitsData(fircosoftReportContext, piReportInput, outputList);
				}
			}
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}

	}

	private void updateFofaDataWithHitsData(FircosoftReportContext fircosoftReportContext,
			PaymentInvestigationReportInput piReportInput, List<ReportDefaultOutput> outputList) {

		PaymentInvestigationReportOutput fofaRecord = fircosoftReportContext.getFofaRecord();
		List<PaymentInvestigationReportOutput> complianceMessages = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputList.isEmpty()) {
			PaymentInvestigationReportOutput cpcCompliance = getComplianceMessageByType(outputList,
					MashreqFederatedReportConstants.CPC_COMPLIANCE_WORKSTAGE, fofaRecord);
			PaymentInvestigationReportOutput hoCompliance = getComplianceMessageByType(outputList,
					MashreqFederatedReportConstants.HO_COMPLIANCE_WORKSTAGE, fofaRecord);
			if (cpcCompliance == null && hoCompliance == null) {
				cpcCompliance = getComplianceMessageByType(outputList,
						MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY, fofaRecord);
				if (cpcCompliance != null) {
					cpcCompliance.setActivityStatus(MashreqFederatedReportConstants.PENDING_ACTIVITY_STATUS);
				}
			}
			// if HO compliance and cpc compliance are there, copy the largest completion
			// time to other's landing time.
			boolean cpcLatest = false;
			if (cpcCompliance != null && hoCompliance != null) {
				if (cpcCompliance.getCompletionTime().getTime() > hoCompliance.getCompletionTime().getTime()) {
					cpcCompliance.setLandingTime(new Timestamp(hoCompliance.getCompletionTime().getTime()));
					cpcLatest = true;
				} else if (cpcCompliance.getCompletionTime().getTime() < hoCompliance.getCompletionTime().getTime()) {
					hoCompliance.setLandingTime(new Timestamp(cpcCompliance.getCompletionTime().getTime()));
				}
			}
			if (cpcLatest) {
				if (hoCompliance != null) {
					complianceMessages.add(hoCompliance);
				}
				if (cpcCompliance != null) {
					complianceMessages.add(cpcCompliance);
				}
			} else {
				if (cpcCompliance != null) {
					complianceMessages.add(cpcCompliance);
				}
				if (hoCompliance != null) {
					complianceMessages.add(hoCompliance);
				}
			}
		}
		if (complianceMessages.isEmpty()) {
			PaymentInvestigationReportOutput baseRecord = clonePaymentInvestigationReportOutput(
					fircosoftReportContext.getFofaRecord());
			baseRecord.setActivityStatus(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_STATUS);
			baseRecord.setCompletedBy(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY);
			baseRecord.setWorkstage(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_WORKSTAGE);
			baseRecord.setActivity(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY);
			complianceMessages.add(baseRecord);
		}
		fircosoftReportContext.setComplianceRecords(complianceMessages);

	}

	private PaymentInvestigationReportOutput getComplianceMessageByType(List<ReportDefaultOutput> outputList,
			String messageType, PaymentInvestigationReportOutput originalMessage) {

		PaymentInvestigationReportOutput messageTypeOutput = null;
		for (ReportDefaultOutput defaultOutput : outputList) {
			List<Object> rowData = defaultOutput.getRowData();
			Timestamp lastModifiedDate = UtilityClass.getTimeStampRepresentation(rowData.get(0));
			String workstage = UtilityClass.getStringRepresentation(rowData.get(1));
			String status = UtilityClass.getStringRepresentation(rowData.get(2));
			String completedBy = UtilityClass.getStringRepresentation(rowData.get(3));
			List<String> matchedWorkStages = MashreqFederatedReportConstants.FIRCOSOFT_COMPLIANCE_WORKSTAGES
					.get(messageType);
			if (doesExist(workstage, matchedWorkStages)) {
				messageTypeOutput = populateOutputData(originalMessage, defaultOutput, messageType, workstage,
						completedBy, lastModifiedDate, status);
				break;
			}
		}
		return messageTypeOutput;

	}

	private PaymentInvestigationReportOutput populateOutputData(PaymentInvestigationReportOutput originalMessage,
			ReportDefaultOutput defaultOutput, String messageType, String workstage, String completedBy,
			Timestamp lastModifiedDate, String status) {

		PaymentInvestigationReportOutput messageTypeOutput = clonePaymentInvestigationReportOutput(originalMessage);
		messageTypeOutput.setComponentDetailId(originalMessage.getComponentDetailId());
		messageTypeOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
		messageTypeOutput.setActivity(messageType);
		messageTypeOutput.setWorkstage(workstage);
		messageTypeOutput.setCompletedBy(completedBy);
		messageTypeOutput.setCompletionTime(lastModifiedDate);
		messageTypeOutput.setActivityStatus(status);
		return messageTypeOutput;

	}

	private List<FircosoftReportContext> populateFofaData(List<ReportDefaultOutput> outputList,
			PaymentInvestigationReportInput piReportInput) {

		List<FircosoftReportContext> fircosoftList = new ArrayList<FircosoftReportContext>();
		if (!outputList.isEmpty()) {
			for (ReportDefaultOutput defaultOutput : outputList) {
				List<Object> rowData = defaultOutput.getRowData();
				FircosoftReportContext context = new FircosoftReportContext();
				PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
				reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
				reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
				reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(2)));
				reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(3)));
				reportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(4)));
				reportOutput.setReceiver(UtilityClass.getStringRepresentation(rowData.get(5)));
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_FIRCOSOFT);
				// set the reference number based on gateway payment message reference number
				String refNum = UtilityClass.getStringRepresentation(rowData.get(8));
				reportOutput.setSourceRefNum(refNum);
				String systemId = UtilityClass.getStringRepresentation(rowData.get(6));
				reportOutput.setDetectionId(systemId);
				String mesgType = UtilityClass.getStringRepresentation(rowData.get(7));
				context.setSystemId(systemId);
				context.setMesgType(mesgType);
				context.setReferenceNum(refNum);
				context.setFofaRecord(reportOutput);
				fircosoftList.add(context);
			}
		}
		return fircosoftList;

	}

	private void updatePrompts(String componentDetailKey, PaymentInvestigationReportInput piReportInput,
			ReportComponentDetailContext context, FircosoftReportContext fircosoftContext) {

		if (MashreqFederatedReportConstants.FIRCOSOFT_FOFA_MESSAGE.equalsIgnoreCase(componentDetailKey)) {
			FederatedReportPromptDTO referenceNumsPrompt = new FederatedReportPromptDTO();
			referenceNumsPrompt.setPromptKey(MashreqFederatedReportConstants.REFERENCE_NUMS_PROMPTS);
			List<String> promptsList = getReferenceNums(piReportInput.getGatewayDataContext());
			String promptValue = promptsList.stream().collect(Collectors.joining(","));
			referenceNumsPrompt.setPromptValue(promptValue);
			referenceNumsPrompt.setValueType(PromptValueType.VALUE);
			context.getPrompts().add(referenceNumsPrompt);
		} else if (MashreqFederatedReportConstants.FIRCOSOFT_HISTORY_ALERT_VIEW.equalsIgnoreCase(componentDetailKey)) {
			FederatedReportPromptDTO systemIdPrompt = new FederatedReportPromptDTO();
			systemIdPrompt.setPromptKey(MashreqFederatedReportConstants.PROMPT_SYSTEM_ID);
			systemIdPrompt.setPromptValue(fircosoftContext.getSystemId());
			systemIdPrompt.setValueType(PromptValueType.VALUE);
			context.getPrompts().add(systemIdPrompt);
		}

	}
	
	private List<String> getReferenceNums(GatewayDataContext gatewayDataContext) {

		List<String> referenceNumList = new ArrayList<String>();
		Map<String, GatewayDataMessageContext> incomingEnquiries = gatewayDataContext.getIncomingEnquiries();
		if (!incomingEnquiries.isEmpty()) {
			referenceNumList = incomingEnquiries.values().stream().map(GatewayDataMessageContext::getMessageRef)
					.collect(Collectors.toList());
		}
		GatewayDataMessageContext incomingMessage = gatewayDataContext.getIncomingMessage();
		if (incomingMessage != null) {
			if (incomingMessage.getScreeningProcessedRecord().isEmpty()) {
				referenceNumList.add(incomingMessage.getMessageRef());
			}
		}
		GatewayDataMessageContext outgoingMessage = gatewayDataContext.getOutgoingMessage();
		if (outgoingMessage != null) {
			if (outgoingMessage.getScreeningProcessedRecord().isEmpty()) {
				referenceNumList.add(outgoingMessage.getMessageRef());
			}
		}
		return referenceNumList;

	}

}