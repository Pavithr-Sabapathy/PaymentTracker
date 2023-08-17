package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.GatewayDataMessageContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.exception.ExceptionCodes;
import com.mashreq.paymentTracker.exception.ReportConnectorException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.service.PaymentInvestigationGatewayService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.EDMSProcessType;
import com.mashreq.paymentTracker.utility.CheckType;

public class PaymentInvestigationGatewayServiceImpl implements PaymentInvestigationGatewayService {

	private static final Logger log = LoggerFactory.getLogger(PaymentInvestigationGatewayServiceImpl.class);
	private static final String FILENAME = "PaymentInvestigationGatewayServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	FlexReportConnector flexReportConnector;

	@Autowired
	UAEFTSReportConnector uaeftsReportConnector;

	@Autowired
	SwiftReportConnector swiftReportConnector;

	@Autowired
	SafeWatchReportConnector safeWatchReportConnector;

	@Autowired
	FircosoftReportConnector fircosoftReportConnector;

	@Override
	public void processGateway(PaymentInvestigationReportInput paymentInvestigationReportInput,
			List<Components> componentList, ReportContext reportContext,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		GatewayDataContext gatewayDataContext = new GatewayDataContext();
		paymentInvestigationReportInput.setGatewayDataContext(gatewayDataContext);
		try {
			processComponent(paymentInvestigationReportInput, componentList, reportContext,
					MashreqFederatedReportConstants.COMPONENT_SWIFT_KEY, reportOutputList);
		} catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_SWIFT);
		}
		if (!gatewayDataContext.isGatewayDataFound()) {
			try {
				processComponent(paymentInvestigationReportInput, componentList, reportContext,
						MashreqFederatedReportConstants.COMPONENT_UAEFTS_KEY, reportOutputList);
			} catch (ReportConnectorException exception) {
				populateFailedSystemsData(paymentInvestigationReportInput, exception,
						MashreqFederatedReportConstants.SOURCE_SYSTEM_UAEFTS);
			}
		}
		// if record found from swift
		if (gatewayDataContext.isSwiftDataFound()) {
			try {
				processComponent(paymentInvestigationReportInput, componentList, reportContext,
						MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY, reportOutputList);
			} catch (ReportConnectorException exception) {
				populateFailedSystemsData(paymentInvestigationReportInput, exception,
						MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY);
			}
		}
		if (gatewayDataContext.isGatewayDataFound()) {
			// handle the fircosoft system.right now the condition is if compliance
			// information is already there, we will not process that message.
			try {
				processComponent(paymentInvestigationReportInput, componentList, reportContext,
						MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY, reportOutputList);
			} catch (ReportConnectorException exception) {
				populateFailedSystemsData(paymentInvestigationReportInput, exception,
						MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY);
			}

		}
		if (gatewayDataContext.isGatewayDataFound()) {
			// if data has been found from gateways, add that to the output
			adjustAndAddGatewayDataToOutput(gatewayDataContext, reportOutputList, paymentInvestigationReportInput);
		}
	}

	private void adjustAndAddGatewayDataToOutput(GatewayDataContext gatewayDataContext,
			List<PaymentInvestigationReportOutput> reportOutputList,
			PaymentInvestigationReportInput paymentInvestigationReportInput) {

		// on outgoing message, update the processing row
		GatewayDataMessageContext outgoingMessage = gatewayDataContext.getOutgoingMessage();
		if (outgoingMessage != null) {
			if (outgoingMessage.getNetworkRecord() != null
					&& !outgoingMessage.getScreeningProcessedRecord().isEmpty()) {
				PaymentInvestigationReportOutput complianceRecord = outgoingMessage.getLatestComplianceRecord();
				// probably compliance record could have no completion date because it is still
				// stuck there
				if (complianceRecord.getCompletionTime() != null) {
					outgoingMessage.getNetworkRecord()
							.setLandingTime(new Timestamp(complianceRecord.getCompletionTime().getTime()));
				}
			}
		}
		// add all the data from all sections to report output
		if (outgoingMessage != null) {
			// check whether compliance row is real violation or new, if yes, then don't add
			// network row.
			if (checkForRealViolation(outgoingMessage.getLatestComplianceRecord())) {
				addGateWayDataContextToReportOutputForRealViolationCase(outgoingMessage, reportOutputList);
			} else {
				addGateWayDataContextToReportOutput(outgoingMessage, reportOutputList);
				addScreeningGateWayDataContextToReportOutput(outgoingMessage, reportOutputList);
			}
			// set the core ref to context
			paymentInvestigationReportInput.setCoreReferenceNum(outgoingMessage.getMessageRef());
		}
		GatewayDataMessageContext incomingMessage = gatewayDataContext.getIncomingMessage();
		if (incomingMessage != null) {
			addGateWayDataContextToReportOutput(incomingMessage, reportOutputList);
			addScreeningGateWayDataContextToReportOutput(incomingMessage, reportOutputList);
			// if outgoing message also found, it means this falls into category of outward
			// coming backto mashreq, we dont need to set source ref
			if (outgoingMessage == null) {
				paymentInvestigationReportInput.setSourceReferenceNum(incomingMessage.getMessageRef());
			}
		}
		List<GatewayDataMessageContext> incomingTrchStatusMessage = gatewayDataContext.getIncomingTrchStatusMessages();
		if (!incomingTrchStatusMessage.isEmpty()) {
			for (GatewayDataMessageContext trchMessage : incomingTrchStatusMessage) {
				addGateWayDataContextToReportOutput(trchMessage, reportOutputList);
			}
		}

		List<GatewayDataMessageContext> outgoingTrchStatusMessage = gatewayDataContext.getOutgoingTrchStatusMessages();
		if (!outgoingTrchStatusMessage.isEmpty()) {
			for (GatewayDataMessageContext trchMessage : outgoingTrchStatusMessage) {
				addGateWayDataContextToReportOutput(trchMessage, reportOutputList);
			}
		}

		List<GatewayDataMessageContext> incomingIpalaStatusMessages = gatewayDataContext
				.getIncomingIpalaStatusMessages();
		if (!incomingIpalaStatusMessages.isEmpty()) {
			for (GatewayDataMessageContext ipalaMessage : incomingIpalaStatusMessages) {
				addGateWayDataContextToReportOutput(ipalaMessage, reportOutputList);
			}
		}

		List<GatewayDataMessageContext> outgoingIpalaStatusMessages = gatewayDataContext
				.getOutgoingIpalaStatusMessages();
		if (!outgoingIpalaStatusMessages.isEmpty()) {
			for (GatewayDataMessageContext ipalaMessage : outgoingIpalaStatusMessages) {
				addGateWayDataContextToReportOutput(ipalaMessage, reportOutputList);
			}
		}

		Map<String, GatewayDataMessageContext> incomingEnquiries = gatewayDataContext.getIncomingEnquiries();
		if (!incomingEnquiries.isEmpty()) {
			Collection<GatewayDataMessageContext> values = incomingEnquiries.values();
			for (GatewayDataMessageContext messageContext : values) {
				addGateWayDataContextToReportOutput(messageContext, reportOutputList);
			}
		}
		Map<String, GatewayDataMessageContext> outgoingEnquiries = gatewayDataContext.getOutgoingEnquiries();
		if (!outgoingEnquiries.isEmpty()) {
			Collection<GatewayDataMessageContext> values = outgoingEnquiries.values();
			for (GatewayDataMessageContext messageContext : values) {
				addGateWayDataContextToReportOutput(messageContext, reportOutputList);
			}
		}

	}

	private void addGateWayDataContextToReportOutputForRealViolationCase(GatewayDataMessageContext messageContext,
			List<PaymentInvestigationReportOutput> reportOutputList) {

		if (messageContext.getScreeningRecord() != null) {
			reportOutputList.add(messageContext.getScreeningRecord());
		}
		if (!messageContext.getScreeningProcessedRecord().isEmpty()) {
			reportOutputList.addAll(messageContext.getScreeningProcessedRecord());
		}

	}

	private void addScreeningGateWayDataContextToReportOutput(GatewayDataMessageContext gateWayDataMessageContextObj,
			List<PaymentInvestigationReportOutput> reportOutputList) {

		if (!gateWayDataMessageContextObj.getScreeningProcessedRecord().isEmpty()) {
			if (gateWayDataMessageContextObj.getScreeningRecord() != null) {
				reportOutputList.add(gateWayDataMessageContextObj.getScreeningRecord());
			}
			if (!gateWayDataMessageContextObj.getScreeningProcessedRecord().isEmpty()) {
				reportOutputList.addAll(gateWayDataMessageContextObj.getScreeningProcessedRecord());
			}
		} else {
			if (gateWayDataMessageContextObj.getScreeningRecord() != null) {
				reportOutputList.add(gateWayDataMessageContextObj.getScreeningRecord());
			}
			if (gateWayDataMessageContextObj.getScreeningRecord() != null) {
				if (!checkForRealViolation(gateWayDataMessageContextObj.getScreeningRecord())) {
					reportOutputList.add(populateNoViolationOutput(gateWayDataMessageContextObj,
							gateWayDataMessageContextObj.getMessageType()));
				}
			}
		}

	}

	private PaymentInvestigationReportOutput populateNoViolationOutput(GatewayDataMessageContext messageContext,
			String mesgType) {
		PaymentInvestigationReportOutput baseRecord = messageContext.getNetworkRecord();
		if (MashreqFederatedReportConstants.OUTGOING_PAYMENT_CODES_LIST.contains(mesgType)) {
			baseRecord = messageContext.getScreeningRecord();
		}
		PaymentInvestigationReportOutput safeWatchData = clonePaymentInvestigationReportOutput(baseRecord);
		safeWatchData.setComponentDetailId(-1L);
		safeWatchData.setActivity(SafeWatchReportConnector.getActivity(mesgType,
				MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY));
		safeWatchData.setActivityStatus(MashreqFederatedReportConstants.NO_VIOLATION_ACTIVITY_STATUS);
		safeWatchData.setSource(MashreqFederatedReportConstants.COMPLIANCE_SOURCE_SYSTEM);
		return safeWatchData;
	}

	public PaymentInvestigationReportOutput clonePaymentInvestigationReportOutput(
			PaymentInvestigationReportOutput toBeCloned) {
		// TODO Auto-generated method stub
		PaymentInvestigationReportOutput piReportOutput = new PaymentInvestigationReportOutput();
		piReportOutput.setComponentDetailId(toBeCloned.getComponentDetailId());
		piReportOutput.setActivityStatus(toBeCloned.getActivityStatus());
		piReportOutput.setAmount(toBeCloned.getAmount());
		piReportOutput.setBeneficaryAccount(toBeCloned.getBeneficaryAccount());
		piReportOutput.setBeneficaryDetail(toBeCloned.getBeneficaryDetail());
		piReportOutput.setCompletedBy(toBeCloned.getCompletedBy());
		piReportOutput.setCompletionTime(toBeCloned.getCompletionTime());
		piReportOutput.setCurrency(toBeCloned.getCurrency());
		piReportOutput.setDebitAccount(toBeCloned.getDebitAccount());
		piReportOutput.setLandingTime(toBeCloned.getLandingTime());
		piReportOutput.setReceiver(toBeCloned.getReceiver());
		piReportOutput.setSource(toBeCloned.getSource());
		piReportOutput.setSourceRefNum(toBeCloned.getSourceRefNum());
		piReportOutput.setValueDate(toBeCloned.getValueDate());
		piReportOutput.setWorkstage(toBeCloned.getWorkstage());
		piReportOutput.setMesgType(toBeCloned.getMesgType());
		piReportOutput.setDetectionId(toBeCloned.getDetectionId());
		piReportOutput.setAccountingSource(toBeCloned.getAccountingSource());
		if (toBeCloned.getDetailedReportType() != null) {
			piReportOutput.setDetailedReportType(toBeCloned.getDetailedReportType());
		}
		piReportOutput.setAid(toBeCloned.getAid());
		piReportOutput.setUmidh(toBeCloned.getUmidh());
		piReportOutput.setUmidl(toBeCloned.getUmidl());
		piReportOutput.setEmailUrl(toBeCloned.getEmailUrl());
		piReportOutput.setMessageSubFormat(toBeCloned.getMessageSubFormat());
		piReportOutput.setGovCheck(toBeCloned.getGovCheck());
		piReportOutput.setGovCheckReference(toBeCloned.getGovCheckReference());
		return piReportOutput;

	}

	private boolean checkForRealViolation(PaymentInvestigationReportOutput screeningProcessedRecord) {
		boolean realViolationRecord = false;
		if (screeningProcessedRecord != null) {
			if ((MashreqFederatedReportConstants.REAL_VIOLATION_ACTIVITY_STATUS
					.equalsIgnoreCase(screeningProcessedRecord.getActivityStatus()))
					|| (MashreqFederatedReportConstants.NEW_ACTIVITY_STATUS
							.equalsIgnoreCase(screeningProcessedRecord.getActivityStatus()))
					|| (MashreqFederatedReportConstants.PENDING_ACTIVITY_STATUS
							.equalsIgnoreCase(screeningProcessedRecord.getActivityStatus()))) {
				realViolationRecord = true;
			}
		}
		return realViolationRecord;
	}

	private void addGateWayDataContextToReportOutput(GatewayDataMessageContext messageContext,
			List<PaymentInvestigationReportOutput> reportOutputList) {

		if (messageContext.getNetworkRecord() != null) {
			reportOutputList.add(messageContext.getNetworkRecord());
		}
		if (messageContext.getNetworkNackRecord() != null) {
			reportOutputList.add(messageContext.getNetworkNackRecord());
		}
		if (messageContext.getCreditConfirmedRecord() != null) {
			reportOutputList.add(messageContext.getCreditConfirmedRecord());
		}
		// if we get no violation which means we didn't get record from safewatch in
		// that case we should not add screening processed and sent for screening rows

	}

	public List<? extends ReportOutput> processComponent(
			PaymentInvestigationReportInput paymentInvestigationReportInput, List<Components> componentList,
			ReportContext reportContext, String componentKey, List<PaymentInvestigationReportOutput> reportOutputList)
			throws ReportConnectorException {
		List<? extends ReportOutput> reportOutput = new ArrayList<ReportOutput>();
		Components matchedComponentsObj = getMatchedComponent(componentList, componentKey);
		ReportConnector reportConnector = getMatchedReportService(componentKey);
		if (matchedComponentsObj != null && reportConnector != null) {
			ReportComponentDTO matchedComponentsDTO = populateReportComponent(matchedComponentsObj);
			paymentInvestigationReportInput.setComponent(matchedComponentsDTO);
			reportOutput = reportConnector.processReportComponent(paymentInvestigationReportInput, reportContext);
		} else {
			log.debug("Component Missing/Matched Connector Missing for key " + componentKey);
		}
		return reportOutput;
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

	private ReportConnector getMatchedReportService(String connectorKey) {
		ReportConnector reportConnector = null;
		if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_FLEX_KEY)) {
			reportConnector = flexReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_SWIFT_KEY)) {
			reportConnector = swiftReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_UAEFTS_KEY)) {
			reportConnector = uaeftsReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY)) {
			reportConnector = safeWatchReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY)) {
			reportConnector = fircosoftReportConnector;
		}
		return reportConnector;

	}

	private Components getMatchedComponent(List<Components> componentList, String componentKey) {
		Components components = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)).findFirst()
				.orElse(null);
		return components;
	}

	@Override
	public void processChannels(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) {
		processMatrixSystem(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
		if (!paymentInvestigationReportInput.isChannelDataFound()) {
			paymentInvestigationReportInput.setEdmsProcessType(EDMSProcessType.FTO);
			processEDMS(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
			if (!paymentInvestigationReportInput.isChannelDataFound()) {
				processSnapp(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
				if (!paymentInvestigationReportInput.isChannelDataFound()) {
					processMOL(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
				}
			}
		}
	}

	private void processMOL(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) {
		try {
			List<? extends ReportOutput> molComponentData = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MOL_KEY, reportOutputList);
			molComponentData.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!molComponentData.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		} catch (Exception exception) {

		}

	}

	private void processSnapp(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) {
		try {
			List<? extends ReportOutput> snappComponentData = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_SNAPP_KEY,
					reportOutputList);
			snappComponentData.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!snappComponentData.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		} catch (Exception exception) {

		}

	}

	private void processEDMS(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) {
		try {
			List<? extends ReportOutput> edmsOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_EMDS_KEY, reportOutputList);
			edmsOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!edmsOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		} catch (Exception exception) {
		}
	}

	private void processMatrixSystem(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) {
		try {
			List<? extends ReportOutput> matrixPaymentOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MATRIX_PAYMENT_KEY,
					reportOutputList);
			matrixPaymentOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!matrixPaymentOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
				paymentInvestigationReportInput.getMatrixReportContext().setMatrixDataFound(true);
			}
		} catch (Exception exception) {

		}

		try {
			List<? extends ReportOutput> matrixPortalOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MATRIX_PORTAL_KEY,
					reportOutputList);
			matrixPortalOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!matrixPortalOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
				paymentInvestigationReportInput.getMatrixReportContext().setMatrixDataFound(true);
			}
		} catch (Exception exception) {
		}
	}

	public void populateFailedSystemsData(PaymentInvestigationReportInput reportInputContext,
			ReportConnectorException exception, String sourceSystem) throws Exception {

		PaymentInvestigationReportOutput output = new PaymentInvestigationReportOutput();
		String activity = null;
		if (ExceptionCodes.REPORT_EXECUTION_QUERY_EXECUTION_FAILURE == exception.getCode()) {
			activity = MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_MESSAGE;
			if (MashreqFederatedReportConstants.APPLY_COLOR_NOTATION) {
				activity = "<font color=" + MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_COLOR + ">" + activity
						+ "</font>";
				sourceSystem = "<font color=" + MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_COLOR + ">"
						+ sourceSystem + "</font>";
			}

		} else if (ExceptionCodes.REPORT_EXECUTION_INPUT_VALIDATION_FAILED == exception.getCode()) {
			activity = MashreqFederatedReportConstants.INSUFFICIENT_INPUT_MESSAGE;
			if (MashreqFederatedReportConstants.APPLY_COLOR_NOTATION) {
				activity = "<font color=" + MashreqFederatedReportConstants.INSUFFICIENT_INPUT_COLOR + ">" + activity
						+ "</font>";
				sourceSystem = "<font color=" + MashreqFederatedReportConstants.INSUFFICIENT_INPUT_COLOR + ">"
						+ sourceSystem + "</font>";
			}
		} else {
			throw new Exception();
		}
		output.setActivity(activity);
		output.setSource(sourceSystem);
		reportInputContext.getFailedSystemOutputs().add(output);
	}

}