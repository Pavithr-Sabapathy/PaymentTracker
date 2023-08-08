package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.GatewayDataMessageContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.SafeWatchReportContext;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class SafeWatchReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(SwiftReportConnector.class);
	private static final String FILENAME = "SwiftReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof PaymentInvestigationReportInput piReportInput) {
			processPaymentInvestigationReport(piReportInput, reportContext);
		}
		return null;
	}

	private void processPaymentInvestigationReport(PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext) {
		List<SafeWatchReportContext> safeWatchList = new ArrayList<SafeWatchReportContext>();
		ReportComponentDTO reportComponentObj = piReportInput.getComponent();
		List<String> detectionIdList = populateDetectionList(piReportInput.getGatewayDataContext());
		if (!detectionIdList.isEmpty()) {
			processComponent(reportComponentObj, piReportInput, MashreqFederatedReportConstants.SAFE_WATCH_DETECTION,
					reportContext, safeWatchList);
			// if we don't get safewatch list, we dont need to process alert history
			if (!safeWatchList.isEmpty()) {
				processComponent(reportComponentObj, piReportInput,
						MashreqFederatedReportConstants.SAFE_WATCH_ALERT_HISTORY, reportContext, safeWatchList);
			}
			processSafeWatchContextList(safeWatchList, piReportInput);
		}
	}

	private void processSafeWatchContextList(List<SafeWatchReportContext> safeWatchList,
			PaymentInvestigationReportInput piReportInput) {

		for (SafeWatchReportContext context : safeWatchList) {
			String mesgType = context.getMesgType();
			for (PaymentInvestigationReportOutput complianceRecord : context.getComplianceRecords()) {
				complianceRecord.setActivity(getActivity(mesgType, complianceRecord.getActivity()));
				String activityStatus = complianceRecord.getActivityStatus();
				if (doNotKnowStatus(activityStatus)) {
					complianceRecord.setActivityStatus(MashreqFederatedReportConstants.COMPLIANCE_DONT_KNOW_STATUS);
				}
				if (MashreqFederatedReportConstants.EXTERNAL_ACTIVITY_STATUS.equalsIgnoreCase(activityStatus)
						|| MashreqFederatedReportConstants.NEW_ACTIVITY_STATUS.equalsIgnoreCase(activityStatus)) {
					((PaymentInvestigationReportOutput) complianceRecord)
							.setActivityStatus(MashreqFederatedReportConstants.PENDING_ACTIVITY_STATUS);
				}
			}
		}
		// populate the data back to gateway context
		GatewayDataContext gatewayDataContext = piReportInput.getGatewayDataContext();
		GatewayDataMessageContext incomingMessage = gatewayDataContext.getIncomingMessage();
		if (incomingMessage != null) {
			populateDataBackToContext(safeWatchList, incomingMessage);

		}

		GatewayDataMessageContext outgoingMessage = gatewayDataContext.getOutgoingMessage();
		if (outgoingMessage != null) {
			populateDataBackToContext(safeWatchList, outgoingMessage);

		}

		Map<String, GatewayDataMessageContext> incomingEnquiries = gatewayDataContext.getIncomingEnquiries();
		if (!incomingEnquiries.isEmpty()) {
			Collection<GatewayDataMessageContext> values = incomingEnquiries.values();
			for (GatewayDataMessageContext value : values) {
				populateDataBackToContext(safeWatchList, value);
			}
		}

	}

	public static boolean doNotKnowStatus(String activityStatus) {
		boolean doNotKnowStatus = false;
		for (String doNotknowStatusVar : MashreqFederatedReportConstants.COMPLIANCE_DONT_KNOW_STATUS_LIST) {
			if (activityStatus.toLowerCase().contains(doNotknowStatusVar.toLowerCase())) {
				doNotKnowStatus = true;
				break;
			}
		}
		return doNotKnowStatus;
	}

	private void populateDataBackToContext(List<SafeWatchReportContext> safeWatchList,
			GatewayDataMessageContext messageContext) {
		List<PaymentInvestigationReportOutput> matchingScannedRecord = getMatchingScannedRecord(safeWatchList,
				messageContext);
		if (!matchingScannedRecord.isEmpty()) {
			messageContext.setScreeningProcessedRecord(matchingScannedRecord);
		}
	}

	public static String getActivity(String mesgType, String activityOrigin) {
		String activity = MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY;
		if ((!MashreqFederatedReportConstants.INCOMING_PAYMENT_CODES_LIST.contains(mesgType)) && !(MashreqFederatedReportConstants.OUTGOING_PAYMENT_CODES_LIST.contains(mesgType))) {
			activity = MashreqFederatedReportConstants.GATEWAY_MESSAGE_SCREENING_PROCESSED_ACTIVITY;
		}
		if (activityOrigin.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY)) {
			return activity + " : " + mesgType;
		} else {
			return activity + "(" + activityOrigin + ")" + " : " + mesgType;
		}
	}

	private List<PaymentInvestigationReportOutput> getMatchingScannedRecord(List<SafeWatchReportContext> safeWatchList,
			GatewayDataMessageContext messageContext) {
		List<PaymentInvestigationReportOutput> matchedScannedRecords = new ArrayList<PaymentInvestigationReportOutput>();
		for (SafeWatchReportContext context : safeWatchList) {
			if (context.getReferenceNum().equalsIgnoreCase(messageContext.getMessageRef())
					&& context.getMesgType().equalsIgnoreCase(messageContext.getMessageType())) {
				matchedScannedRecords = context.getComplianceRecords();
			}
		}
		return matchedScannedRecords;
	}

	private void processComponent(ReportComponentDTO reportComponentObj, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext, List<SafeWatchReportContext> safeWatchList) {

		Set<ReportComponentDetailDTO> componentDetailList = reportComponentObj.getReportComponentDetails();
		ReportComponentDetailDTO componentDetail = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		if (null != componentDetail) {
			if (MashreqFederatedReportConstants.SAFE_WATCH_DETECTION.equalsIgnoreCase(componentDetailKey)) {
				ReportComponentDetailContext reportComponentDetailContext = populateReportComponentDetailContext(
						componentDetail, piReportInput, reportContext);
				updatePrompts(componentDetailKey, piReportInput, reportComponentDetailContext, null);
				List<ReportDefaultOutput> reportOutput = queryExecutorService.executeQuery(componentDetail,
						reportComponentDetailContext);
				List<SafeWatchReportContext> safeWatchReportList = populateDetectionContext(reportOutput,
						piReportInput);
				if (!safeWatchReportList.isEmpty()) {
					safeWatchList.addAll(safeWatchReportList);
				}

			} else if (MashreqFederatedReportConstants.SAFE_WATCH_ALERT_HISTORY.equalsIgnoreCase(componentDetailKey)) {
				safeWatchList.stream().forEach(SafeWatch -> {
					ReportComponentDetailContext reportComponentDetailContext = populateReportComponentDetailContext(
							componentDetail, piReportInput, reportContext);
					updatePrompts(componentDetailKey, piReportInput, reportComponentDetailContext, SafeWatch);
					List<ReportDefaultOutput> reportOutput = queryExecutorService.executeQuery(componentDetail,
							reportComponentDetailContext);
					updateDetectionDataWithAlertData(reportOutput, SafeWatch, piReportInput);
				});
			}
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}
	}

	private void updateDetectionDataWithAlertData(List<ReportDefaultOutput> reportOutput,
			SafeWatchReportContext safeWatch, PaymentInvestigationReportInput piReportInput) {

		PaymentInvestigationReportOutput detectionRecord = safeWatch.getDetectionRecord();
		List<PaymentInvestigationReportOutput> complianceMessages = new ArrayList<PaymentInvestigationReportOutput>();

		if (!reportOutput.isEmpty()) {
			List<ReportDefaultOutput> hoData = populateCPCAndHOComplianceData(reportOutput,
					MashreqFederatedReportConstants.HO_COMPLIANCE_WORKSTAGE);
			List<ReportDefaultOutput> cpcData = populateCPCAndHOComplianceData(reportOutput,
					MashreqFederatedReportConstants.CPC_COMPLIANCE_WORKSTAGE);
			PaymentInvestigationReportOutput cpcCompliance = getComplianceMessageByType(cpcData,
					MashreqFederatedReportConstants.CPC_COMPLIANCE_WORKSTAGE, detectionRecord);
			PaymentInvestigationReportOutput hoCompliance = getComplianceMessageByType(hoData,
					MashreqFederatedReportConstants.HO_COMPLIANCE_WORKSTAGE, detectionRecord);
			if (cpcCompliance == null && hoCompliance == null) {
				cpcCompliance = getComplianceMessageByType(reportOutput,
						MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY, detectionRecord);
			}
			// if HO compliance is there, copy the landing time of cpc to ho
			if (cpcCompliance != null && hoCompliance != null) {
				if (cpcCompliance.getCompletionTime() != null) {
					hoCompliance.setLandingTime(new Timestamp(cpcCompliance.getCompletionTime().getTime()));
				}
			}
			if (cpcCompliance != null) {
				complianceMessages.add(cpcCompliance);
			}
			if (hoCompliance != null) {
				complianceMessages.add(hoCompliance);
			}
		}
		if (!complianceMessages.isEmpty()) {
			PaymentInvestigationReportOutput baseRecord = clonePaymentInvestigationReportOutput(
					safeWatch.getDetectionRecord());
			baseRecord.setActivityStatus(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_STATUS);
			baseRecord.setCompletedBy(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY);
			baseRecord.setWorkstage(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_WORKSTAGE);
			baseRecord.setActivity(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY);
			complianceMessages.add(baseRecord);
		}
		safeWatch.setComplianceRecords(complianceMessages);

	}

	private PaymentInvestigationReportOutput getComplianceMessageByType(List<ReportDefaultOutput> reportOutput,
			String messageType, PaymentInvestigationReportOutput originalMessage) {

		PaymentInvestigationReportOutput messageTypeOutput = null;
		String status = MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_STATUS;
		String workstage = "";
		String completedBy = "";
		Timestamp lastModifiedDate = null;
		for (ReportDefaultOutput defaultOutput : reportOutput) {
			List<Object> rowData = defaultOutput.getRowData();
			String modifType = UtilityClass.getStringRepresentation(rowData.get(1));
			String newValue = UtilityClass.getStringRepresentation(rowData.get(2));
			if (MashreqFederatedReportConstants.SAFEWATCH_STATUS_MODIF_TYPES.contains(modifType)) {
				if (status.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_STATUS)) {
					status = newValue;
					lastModifiedDate = UtilityClass.getTimeStampRepresentation(rowData.get(0));
				}
			} else if (modifType
					.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_WORKSTAGE_COMPLETEDBY_MODIF_TYPE)) {
				workstage = newValue;
			} else if (modifType
					.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_WORKSTAGE_COMPLETEDBY_NEW_MODIF_TYPE)) {
				workstage = MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY;
				status = MashreqFederatedReportConstants.SAFEWATCH_WORKSTAGE_COMPLETEDBY_NEW_MODIF_TYPE;
				lastModifiedDate = UtilityClass.getTimeStampRepresentation(rowData.get(0));
			}
			List<String> matchedWorkStages = MashreqFederatedReportConstants.SAFEWATCH_COMPLIANCE_WORKSTAGES
					.get(messageType);
			if (doesExist(workstage, matchedWorkStages)) {
				completedBy = UtilityClass.getStringRepresentation(rowData.get(3));
				if (lastModifiedDate == null) {
					continue;
				}
				messageTypeOutput = populateOutputData(originalMessage, defaultOutput, messageType, workstage,
						completedBy, lastModifiedDate, status);
				break;
			}
		}
		// all rows scanned done
		if (messageTypeOutput == null && reportOutput.size() > 0) {
			messageTypeOutput = populateOutputData(originalMessage, reportOutput.get(0), messageType, workstage,
					completedBy, lastModifiedDate, status);
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

	private List<ReportDefaultOutput> populateCPCAndHOComplianceData(List<ReportDefaultOutput> reportOutput,
			String messageType) {

		int counter = 0;
		int latestHoIndex = -1;
		int latestcpcIndex = -1;
		for (ReportDefaultOutput defaultOutput : reportOutput) {
			List<Object> rowData = defaultOutput.getRowData();
			String modifType = UtilityClass.getStringRepresentation(rowData.get(1));
			String newValue = UtilityClass.getStringRepresentation(rowData.get(2));
			if (modifType
					.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_WORKSTAGE_COMPLETEDBY_MODIF_TYPE)) {
				if (newValue.equalsIgnoreCase(MashreqFederatedReportConstants.HO_COMPLIANCE_CHECKER_WORKSTAGE)) {
					latestHoIndex = counter;
				} else if (newValue
						.equalsIgnoreCase(MashreqFederatedReportConstants.CPC_COMPLIANCE_CHECKER_WORKSTAGE)) {
					latestcpcIndex = counter;
				}
			}
			counter++;
		}

		List<ReportDefaultOutput> filteredData = new ArrayList<ReportDefaultOutput>();
		if (messageType.equalsIgnoreCase(MashreqFederatedReportConstants.HO_COMPLIANCE_WORKSTAGE)) {
			if (latestHoIndex != -1) {
				filteredData = reportOutput.subList(0, latestHoIndex + 1);
			}
		} else if (messageType.equalsIgnoreCase(MashreqFederatedReportConstants.CPC_COMPLIANCE_WORKSTAGE)) {
			if (latestcpcIndex != -1) {
				if (latestHoIndex != -1) {
					filteredData = reportOutput.subList(latestHoIndex + 1, reportOutput.size());// ;,
																								// latestcpcIndex
																								// +
																								// 1);
				} else {
					filteredData = reportOutput;// .subList(0, latestcpcIndex + 1);
				}
			}
		} else {
			filteredData = reportOutput;
		}

		return filteredData;

	}

	private List<SafeWatchReportContext> populateDetectionContext(List<ReportDefaultOutput> outputList,
			PaymentInvestigationReportInput piReportInput) {
		List<SafeWatchReportContext> safeWatchList = new ArrayList<SafeWatchReportContext>();
		if (!outputList.isEmpty()) {
			outputList.stream().forEach(defaultOutput -> {
				PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
				List<Object> rowData = defaultOutput.getRowData();
				SafeWatchReportContext context = new SafeWatchReportContext();
				reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
				reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
				reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(2)));
				reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(3)));
				reportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(4)));
				reportOutput.setReceiver(UtilityClass.getStringRepresentation(rowData.get(5)));
				reportOutput.setSource(MashreqFederatedReportConstants.SAFE_WATCH_DETECTION);
				// set the reference number based on gateway payment message reference number
				String refNum = UtilityClass.getStringRepresentation(rowData.get(8));
				reportOutput.setSourceRefNum(refNum);
				String detectionId = UtilityClass.getStringRepresentation(rowData.get(6));
				reportOutput.setDetectionId(detectionId);
				String mesgType = UtilityClass.getStringRepresentation(rowData.get(7));
				context.setDetectionId(detectionId);
				context.setMesgType(mesgType);
				context.setReferenceNum(refNum);
				context.setDetectionRecord(reportOutput);
				safeWatchList.add(context);

			});
		}
		return safeWatchList;
	}

	private void updatePrompts(String componentDetailKey, PaymentInvestigationReportInput piReportInput,
			ReportComponentDetailContext reportComponentDetailContext, SafeWatchReportContext safeWatchContext) {

		FederatedReportPromptDTO detectionIdPrompt = new FederatedReportPromptDTO();
		detectionIdPrompt.setPromptKey(MashreqFederatedReportConstants.PROMPT_DETECTION_ID);
		if (MashreqFederatedReportConstants.SAFE_WATCH_DETECTION.equalsIgnoreCase(componentDetailKey)) {
			List<String> detectionIdList = populateDetectionList(piReportInput.getGatewayDataContext());
			String promptValue = detectionIdList.stream().collect(Collectors.joining(","));
			detectionIdPrompt.setPromptValue(promptValue);
		} else if (MashreqFederatedReportConstants.SAFE_WATCH_ALERT_HISTORY.equalsIgnoreCase(componentDetailKey)) {
			detectionIdPrompt.setPromptValue(safeWatchContext.getDetectionId());
		}
		detectionIdPrompt.setValueType(PromptValueType.VALUE);
		reportComponentDetailContext.getPrompts().add(detectionIdPrompt);
	}

	private ReportComponentDetailDTO getMatchedInstanceComponentDetail(
			Set<ReportComponentDetailDTO> componentDetailList, String componentDetailKey) {
		return componentDetailList.stream()
				.filter(ComponentDetail -> ComponentDetail.getQueryKey().equalsIgnoreCase(componentDetailKey)).findAny()
				.orElse(null);

	}

	private List<String> populateDetectionList(GatewayDataContext gatewayDataContext) {
		List<String> detectionIdList = new ArrayList<String>();
		Map<String, GatewayDataMessageContext> gateWayDataMessageMap = gatewayDataContext.getIncomingEnquiries();
		GatewayDataMessageContext incomingMessage = gatewayDataContext.getIncomingMessage();
		GatewayDataMessageContext outgoingMessage = gatewayDataContext.getOutgoingMessage();

		detectionIdList = gateWayDataMessageMap.values().stream().map(GatewayDataMessageContext::getDetectionId)
				.collect(Collectors.toList());
		if (incomingMessage != null) {
			if (incomingMessage.getDetectionId() != null) {
				detectionIdList.add(incomingMessage.getDetectionId());
			}
		}
		if (outgoingMessage != null) {
			if (outgoingMessage.getDetectionId() != null) {
				detectionIdList.add(outgoingMessage.getDetectionId());
			}
		}
		return detectionIdList;
	}
}