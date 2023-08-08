package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.GatewayDataMessageContext;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.MessageField;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.SWIFTDetailedFederatedReportDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsReportOutput;
import com.mashreq.paymentTracker.dto.SWIFTReportContext;
import com.mashreq.paymentTracker.dto.StxEntryFieldViewInfo;
import com.mashreq.paymentTracker.dto.SwiftDetailsReportObjectDTO;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.MessageType;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.SWIFTDetailedReportType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class SwiftReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(SwiftReportConnector.class);
	private static final String FILENAME = "SwiftReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof SWIFTDetailedFederatedReportDTO) {
			SWIFTDetailedFederatedReportDTO SWIFTDetailedFederatedReportDTO = (SWIFTDetailedFederatedReportDTO) reportInput;
			return processSwiftDetailedReport(SWIFTDetailedFederatedReportDTO, reportContext);
		} else if (reportInput instanceof PaymentInvestigationReportInput piReportInput) {
			processPaymentInvestigationReport(piReportInput, reportContext);
		}
		return null;
	}

	private void processPaymentInvestigationReport(
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		ReportComponentDTO component = piReportInput.getComponent();
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		if (!componentDetailList.isEmpty()) {
			List<SWIFTReportContext> swiftContextList = new ArrayList<SWIFTReportContext>();
			String rmesgQuery = MashreqFederatedReportConstants.SWIFT_RMESG;
			// if role is customer, use the rmesg query to pull the messages only on
			// msg_trn_ref only
			if (piReportInput.getRoleName().equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_REPORTING_ROLE)
					|| piReportInput.getRoleName()
							.equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_MATRIX_REPORTING_ROLE)) {
				rmesgQuery = MashreqFederatedReportConstants.SWIFT_TRN_REF_MESG;
			}
			processComponentDetail(component, componentDetailList, piReportInput, rmesgQuery, swiftContextList,
					reportContext);
			if (!swiftContextList.isEmpty()) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.SWIFT_RTEXTFIELD, swiftContextList, reportContext);
			}
			// populate the detection id for all messages {incoming, outgoing / incoming
			// enquiries}
			if (!swiftContextList.isEmpty()) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.SWIFTINTVDETECTION, swiftContextList, reportContext);
			}
			if (!swiftContextList.isEmpty()) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.SWIFTINTV, swiftContextList, reportContext);
				processSwiftContextList(swiftContextList, piReportInput, reportContext);
			}
		}
	}

	private void processSwiftContextList(List<SWIFTReportContext> swiftContextList,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {

		GatewayDataContext gatewayDataContext = piReportInput.getGatewayDataContext();
		processIncomingMessage(swiftContextList, gatewayDataContext);
		processOutgoingMessage(swiftContextList, gatewayDataContext, piReportInput, reportContext);
		processIncomingMessage(swiftContextList, gatewayDataContext);
		processOutgoingEnquiries(swiftContextList, gatewayDataContext);
		processIncomingTrchStatusMessage(swiftContextList, gatewayDataContext);
		processOutgoingTrchStatusMessage(swiftContextList, gatewayDataContext);
		processIncomingIpalaStatusMessage(swiftContextList, gatewayDataContext);
		processOutgoingIpalaStatusMessage(swiftContextList, gatewayDataContext);

		if ((gatewayDataContext.getIncomingMessage() != null) || (gatewayDataContext.getOutgoingMessage() != null)) {
			gatewayDataContext.setSwiftDataFound(true);
		}

	}

	private void processOutgoingMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext, PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext) {
		// TODO Auto-generated method stub

		List<SWIFTReportContext> outgoingMessages = getMatchingTypeRecords(swiftContextList, MessageType.OUTGOING);
		if (!outgoingMessages.isEmpty()) {
			SWIFTReportContext swiftReportContext = outgoingMessages.get(0);
			PaymentInvestigationReportOutput nackRecord = null;
			PaymentInvestigationReportOutput rMessageRecord = swiftReportContext.getrMessageRecord();
			rMessageRecord.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_ACTIVITY + " : "
					+ swiftReportContext.getMesgType());
			rMessageRecord.setDetailedReportType(SWIFTDetailedReportType.RMESG);
			PaymentInvestigationReportOutput rIntvRecord = swiftReportContext.getRintvRecord();
			if (rIntvRecord != null) {
				rIntvRecord.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_OUTWARD_NETWORK_ACTIVITY + " : "
						+ swiftReportContext.getMesgType());
				rIntvRecord.setDetailedReportType(SWIFTDetailedReportType.RINTV);
				rIntvRecord.setComponentDetailId(rMessageRecord.getComponentDetailId());
				// try to find the network nack row. If message status is ack, check for nack
				// case
				if (rIntvRecord.getActivityStatus().equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MESG_ACK)) {
					// if record found in wdnackresult
					ReportComponentDTO component = piReportInput.getComponent();
					String swiftwdNackResult = MashreqFederatedReportConstants.Swift_NACK_RESULT;
					// set the component details to gateway context
					ReportComponentDetailDTO componentDetail = getMatchedInstanceComponentDetail(
							component.getReportComponentDetails(), swiftwdNackResult);
					Timestamp nackResultQuery = processWdNackResultQuery(component, componentDetail, piReportInput,
							swiftwdNackResult, reportContext, swiftReportContext);
					if (nackResultQuery != null) {
						nackRecord = clonePaymentInvestigationReportOutput(rIntvRecord);
						rIntvRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_NACK);
						rIntvRecord.setCompletionTime(nackResultQuery);
						nackRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_ACK);
						nackRecord.setActivity(MashreqFederatedReportConstants.NACK_ACTIVITY);
						nackRecord.setLandingTime(new Timestamp(rIntvRecord.getCompletionTime().getTime()));
					}
				}
			}
			GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
			messageContext.setMessageRef(swiftReportContext.getReferenceNum());
			messageContext.setMessageType(swiftReportContext.getMesgType());
			messageContext.setMessageSubFormat(swiftReportContext.getMesgFormat());
			messageContext.setDetectionId(swiftReportContext.getDetectionId());
			messageContext.setNetworkRecord(rIntvRecord);
			messageContext.setNetworkNackRecord(nackRecord);
			messageContext.setScreeningRecord(rMessageRecord);
			gatewayDataContext.setOutgoingMessage(messageContext);
		}

	}

	private Timestamp processWdNackResultQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetail,
			PaymentInvestigationReportInput piReportInput, String swiftwdNackResult, ReportContext reportContext,
			SWIFTReportContext swiftReportContext) {

		Timestamp nackTime = null;

		List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		referenceNumPrompt.setPromptValue(piReportInput.getUserReferenceNum());
		promptInfoList.add(referenceNumPrompt);
		promptInfoList.add(piReportInput.getCountryCodePrompt());
		promptInfoList.add(piReportInput.getTimeInDaysPrompt());
		context.setQueryString(componentDetail.getQuery());
		context.setPrompts(promptInfoList);
		context.setExecutionId(reportContext.getExecutionId());
		updatePrompts(swiftReportContext, context);
		List<ReportDefaultOutput> reportOutputList = queryExecutorService.executeQuery(componentDetail, context);

		for (ReportDefaultOutput defaultOutput : reportOutputList) {
			List<Object> rowData = defaultOutput.getRowData();
			nackTime = UtilityClass.getTimeStampRepresentation(rowData.get(0));
		}
		return nackTime;

	}

	private void processOutgoingIpalaStatusMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		List<SWIFTReportContext> outgoingEnquiries = getMatchingTypeRecords(swiftContextList,
				MessageType.OUTGOING_ENQUIRY);
		if (!outgoingEnquiries.isEmpty()) {
			List<SWIFTReportContext> gpiIpalaMessages = new ArrayList<SWIFTReportContext>();
			for (SWIFTReportContext enquiry : outgoingEnquiries) {
				if (isGpiIpalaEnabledMessage(enquiry)) {
					gpiIpalaMessages.add(enquiry);
				}
			}
			List<GatewayDataMessageContext> ipalaMessages = processGPIIpalaMessages(gatewayDataContext,
					gpiIpalaMessages);
			if (!ipalaMessages.isEmpty()) {
				gatewayDataContext.setOutgoingIpalaStatusMessages(ipalaMessages);
			}
		}

	}

	private List<GatewayDataMessageContext> processGPIIpalaMessages(GatewayDataContext gatewayDataContext,
			List<SWIFTReportContext> gpiIpalaMessages) {

		List<GatewayDataMessageContext> messageContextList = new ArrayList<GatewayDataMessageContext>();
		if (!gpiIpalaMessages.isEmpty()) {
			for (SWIFTReportContext swiftReportContext : gpiIpalaMessages) {
				String activity = MashreqFederatedReportConstants.GPI_EXTERNAL_IPALA_STATUS_ACTIVITY + " : "
						+ swiftReportContext.getMesgType();
				swiftReportContext.getrMessageRecord().setActivity(activity);
				swiftReportContext.getrMessageRecord()
						.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_ACK);
				swiftReportContext.getrMessageRecord().setDetailedReportType(SWIFTDetailedReportType.RMESG);
				GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
				messageContext.setMessageRef(swiftReportContext.getReferenceNum());
				messageContext.setMessageType(swiftReportContext.getMesgType());
				messageContext.setMessageSubFormat(swiftReportContext.getMesgFormat());
				messageContext.setNetworkRecord(swiftReportContext.getrMessageRecord());
				messageContextList.add(messageContext);
			}
		}
		return messageContextList;

	}

	private boolean isGpiIpalaEnabledMessage(String receiver, String sender) {
		boolean isGpiIpalaEnabledMessage = false;
		if ((null != receiver
				&& receiver.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_IPALA_CODE))
				|| ((null != sender
						&& sender.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_IPALA_CODE)))) {
			isGpiIpalaEnabledMessage = true;
		}
		return isGpiIpalaEnabledMessage;
	}

	private boolean isGpiEnabledMessage(SWIFTReportContext record) {
		return (isGpiIpalaEnabledMessage(record) || isGpiTrchEnabledMessage(record));
	}

	private boolean isGpiTrchEnabledMessage(SWIFTReportContext record) {
		boolean isGpiTrchEnabledMessage = false;
		PaymentInvestigationReportOutput rMessageRecord = record.getrMessageRecord();
		if ((record.getMesgType()
				.equalsIgnoreCase(MashreqFederatedReportConstants.INCOMING_PAYMENT_STATUS_MESSAGE_TYPE))
				|| (record.getMesgType()
						.equalsIgnoreCase(MashreqFederatedReportConstants.OUTGOING_PAYMENT_STATUS_MESSAGE_TYPE))) {
			isGpiTrchEnabledMessage = isGpiTrchEnabledMessage(rMessageRecord.getReceiver(), record.getSender());
		}
		return isGpiTrchEnabledMessage;
	}

	private boolean isGpiTrchEnabledMessage(String receiver, String sender) {
		boolean isGpiTrchEnabledMessage = false;
		if ((null != receiver
				&& receiver.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_TRCH_CODE))
				|| ((null != sender
						&& sender.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_TRCH_CODE)))) {
			isGpiTrchEnabledMessage = true;
		}
		return isGpiTrchEnabledMessage;
	}

	private boolean isGpiIpalaEnabledMessage(SWIFTReportContext record) {
		boolean isGpiIpalaEnabledMessage = false;
		PaymentInvestigationReportOutput rMessageRecord = record.getrMessageRecord();
		if ((record.getMesgType()
				.equalsIgnoreCase(MashreqFederatedReportConstants.INCOMING_PAYMENT_STATUS_MESSAGE_TYPE))
				|| (record.getMesgType()
						.equalsIgnoreCase(MashreqFederatedReportConstants.OUTGOING_PAYMENT_STATUS_MESSAGE_TYPE))) {
			isGpiIpalaEnabledMessage = isGpiIpalaEnabledMessage(rMessageRecord.getReceiver(), record.getSender());
		}
		return isGpiIpalaEnabledMessage;
	}

	private void processOutgoingTrchStatusMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		List<SWIFTReportContext> outgoingEnquiries = getMatchingTypeRecords(swiftContextList,
				MessageType.OUTGOING_ENQUIRY);
		if (!outgoingEnquiries.isEmpty()) {
			List<SWIFTReportContext> gpiTrchMessages = new ArrayList<SWIFTReportContext>();
			for (SWIFTReportContext enquiry : outgoingEnquiries) {
				if (isGpiTrchEnabledMessage(enquiry)) {
					gpiTrchMessages.add(enquiry);
				}
			}
			List<GatewayDataMessageContext> trchMessages = processGPITrchMessages(gatewayDataContext, gpiTrchMessages);
			if (!trchMessages.isEmpty()) {
				gatewayDataContext.setOutgoingTrchStatusMessages(trchMessages);
			}
		}

	}

	private List<GatewayDataMessageContext> processGPITrchMessages(GatewayDataContext gatewayDataContext,
			List<SWIFTReportContext> gpiTrchMessages) {

		List<GatewayDataMessageContext> messageContextList = new ArrayList<GatewayDataMessageContext>();
		if (!gpiTrchMessages.isEmpty()) {
			for (SWIFTReportContext swiftReportContext : gpiTrchMessages) {
				String activity = MashreqFederatedReportConstants.GPI_EXTERNAL_TRCH_STATUS_ACTIVITY + " : "
						+ swiftReportContext.getMesgType();
				String senderBank = deriveSenderBank(swiftReportContext);
				if (null != senderBank) {
					activity = activity + " " + senderBank;
				}
				swiftReportContext.getrMessageRecord().setActivity(activity);
				swiftReportContext.getrMessageRecord()
						.setActivityStatus(deriveActivityStatus(swiftReportContext.getPaymentStatus()));
				swiftReportContext.getrMessageRecord().setDetailedReportType(SWIFTDetailedReportType.RMESG);
				GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
				messageContext.setMessageRef(swiftReportContext.getReferenceNum());
				messageContext.setMessageType(swiftReportContext.getMesgType());
				messageContext.setMessageSubFormat(swiftReportContext.getMesgFormat());
				messageContext.setNetworkRecord(swiftReportContext.getrMessageRecord());
				messageContextList.add(messageContext);
			}
		}
		return messageContextList;

	}

	private String deriveSenderBank(SWIFTReportContext swiftReportContext) {
		String senderBank = null;
		String paymentStatus = swiftReportContext.getPaymentStatus();
		if (null != paymentStatus) {
			String[] tokens = paymentStatus.split("//");
			if (tokens.length >= 4) {
				senderBank = tokens[3];
			}
		}
		if (null != senderBank) {
			senderBank = "(" + senderBank + ")";
		}
		return senderBank;
	}

	private void processIncomingIpalaStatusMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		List<SWIFTReportContext> incomingEnquiries = getMatchingTypeRecords(swiftContextList,
				MessageType.INCOMING_ENQUIRY);
		if (!incomingEnquiries.isEmpty()) {
			List<SWIFTReportContext> gpiIpalaMessages = new ArrayList<SWIFTReportContext>();
			for (SWIFTReportContext enquiry : incomingEnquiries) {
				if (isGpiIpalaEnabledMessage(enquiry)) {
					gpiIpalaMessages.add(enquiry);
				}
			}
			List<GatewayDataMessageContext> ipalaMessages = processGPIIpalaMessages(gatewayDataContext,
					gpiIpalaMessages);
			if (!ipalaMessages.isEmpty()) {
				gatewayDataContext.setIncomingIpalaStatusMessages(ipalaMessages);
			}
		}

	}

	private void processIncomingTrchStatusMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		List<SWIFTReportContext> incomingEnquiries = getMatchingTypeRecords(swiftContextList,
				MessageType.INCOMING_ENQUIRY);
		if (!incomingEnquiries.isEmpty()) {
			List<SWIFTReportContext> gpiTrchMessages = new ArrayList<SWIFTReportContext>();
			for (SWIFTReportContext enquiry : incomingEnquiries) {
				if (isGpiTrchEnabledMessage(enquiry)) {
					gpiTrchMessages.add(enquiry);
				}
			}
			List<GatewayDataMessageContext> trchMessages = processGPITrchMessages(gatewayDataContext, gpiTrchMessages);
			if (trchMessages != null) {
				gatewayDataContext.setIncomingTrchStatusMessages(trchMessages);
			}
		}

	}

	private void processOutgoingEnquiries(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		List<SWIFTReportContext> outgoingEnquiries = getMatchingTypeRecords(swiftContextList,
				MessageType.OUTGOING_ENQUIRY);
		if (!outgoingEnquiries.isEmpty()) {
			for (SWIFTReportContext enquiry : outgoingEnquiries) {
				if (!isGpiEnabledMessage(enquiry)) {
					enquiry.getrMessageRecord()
							.setActivity(MashreqFederatedReportConstants.GATEWAY_MESSAGE_OUTGOING_ACTIVITY + " : "
									+ enquiry.getMesgType());
					enquiry.getrMessageRecord().setDetailedReportType(SWIFTDetailedReportType.RMESG);
					GatewayDataMessageContext messageContext = new GatewayDataMessageContext(
							enquiry.getrMessageRecord(), enquiry.getReferenceNum(), enquiry.getMesgType());
					messageContext.setMessageSubFormat(enquiry.getMesgFormat());
					gatewayDataContext.addOutgoingEnquiry(enquiry.getReferenceNum(), messageContext);
				}
			}
		}

	}

	private void processIncomingMessage(List<SWIFTReportContext> swiftContextList,
			GatewayDataContext gatewayDataContext) {

		SWIFTReportContext swiftReportContext = swiftContextList.get(0);
		PaymentInvestigationReportOutput rMessageRecord = swiftReportContext.getrMessageRecord();
		rMessageRecord.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_INWARD_NETWORK_ACTIVITY + " : "
				+ swiftReportContext.getMesgType());
		PaymentInvestigationReportOutput screeningRecord = clonePaymentInvestigationReportOutput(rMessageRecord);
		screeningRecord.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_ACTIVITY + " : "
				+ swiftReportContext.getMesgType());
		screeningRecord.setDetailedReportType(SWIFTDetailedReportType.RINTV);
		GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
		messageContext.setMessageRef(swiftReportContext.getReferenceNum());
		messageContext.setMessageType(swiftReportContext.getMesgType());
		messageContext.setMessageSubFormat(swiftReportContext.getMesgFormat());
		rMessageRecord.setDetailedReportType(SWIFTDetailedReportType.RMESG);
		messageContext.setNetworkRecord(rMessageRecord);
		messageContext.setScreeningRecord(screeningRecord);
		messageContext.setDetectionId(swiftReportContext.getDetectionId());
		gatewayDataContext.setIncomingMessage(messageContext);

	}

	private void processComponentDetail(ReportComponentDTO component, Set<ReportComponentDetailDTO> componentDetailList,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey,
			List<SWIFTReportContext> swiftContextList, ReportContext reportContext) {
		ReportComponentDetailDTO componentDetailObject = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		if (null != componentDetailObject) {
			if (componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.SWIFT_RMESG)
					|| componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.SWIFT_TRN_REF_MESG)) {
				processRMesgQuery(component, componentDetailObject, piReportInput, componentDetailKey, reportContext,
						swiftContextList);
			} else if (MashreqFederatedReportConstants.SWIFT_RTEXTFIELD.equalsIgnoreCase(componentDetailKey)) {
				processRTextQuery(component, componentDetailObject, piReportInput, componentDetailKey, reportContext,
						swiftContextList);
			} else if (MashreqFederatedReportConstants.SWIFTINTVDETECTION.equalsIgnoreCase(componentDetailKey)) {
				processRIntvDetectionQuery(component, componentDetailObject, piReportInput, componentDetailKey,
						reportContext, swiftContextList);
			} else if (MashreqFederatedReportConstants.SWIFTINTV.equalsIgnoreCase(componentDetailKey)) {
				processRIntvQuery(component, componentDetailObject, piReportInput, componentDetailKey, reportContext,
						swiftContextList);
			}
		} else {
			log.debug(FILENAME + "processComponentDetail Component Detail missing for " + componentDetailKey);
		}
	}

	private void processRIntvQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetailObject,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			List<SWIFTReportContext> swiftContextList) {
		List<SWIFTReportContext> outgoingMessages = getMatchingTypeRecords(swiftContextList, MessageType.OUTGOING);
		if (!outgoingMessages.isEmpty()) {
			SWIFTReportContext swiftContext = outgoingMessages.get(0);
			List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
			FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
			ReportComponentDetailContext context = new ReportComponentDetailContext();
			referenceNumPrompt.setPromptValue(piReportInput.getUserReferenceNum());
			promptInfoList.add(referenceNumPrompt);
			promptInfoList.add(piReportInput.getCountryCodePrompt());
			promptInfoList.add(piReportInput.getTimeInDaysPrompt());
			context.setQueryString(componentDetailObject.getQuery());
			context.setPrompts(promptInfoList);
			context.setExecutionId(reportContext.getExecutionId());
			updatePrompts(swiftContext, context);
			List<ReportDefaultOutput> reportOutputList = queryExecutorService.executeQuery(componentDetailObject,
					context);
			replicateRMesgDataWithRIntv(context, swiftContext, reportOutputList);
		}
	}

	private void replicateRMesgDataWithRIntv(ReportComponentDetailContext context, SWIFTReportContext swiftContext,
			List<ReportDefaultOutput> reportOutputList) {

		PaymentInvestigationReportOutput rMessageRecord = swiftContext.getrMessageRecord();
		PaymentInvestigationReportOutput clonedRecord = null;
		if (!reportOutputList.isEmpty()) {
			Timestamp maxTime = UtilityClass.getTimeStampRepresentation(reportOutputList.get(0).getRowData().get(2));
			String activityStatus = MashreqFederatedReportConstants.RINTV_MESG_LIVE;
			ReportDefaultOutput matchedDefaultOutput = null;
			for (ReportDefaultOutput defaultOutput : reportOutputList) {
				matchedDefaultOutput = defaultOutput;
				List<Object> rowData = defaultOutput.getRowData();
				String intvName = UtilityClass.getStringRepresentation(rowData.get(0));
				String mpfnName = UtilityClass.getStringRepresentation(rowData.get(1));
				Timestamp intvDateTime = UtilityClass.getTimeStampRepresentation(rowData.get(2));
				String networkDeliveryStatus = UtilityClass.getStringRepresentation(rowData.get(3));
				if (null != mpfnName && null != networkDeliveryStatus && null != intvName) {
					if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_SI_TO_SWIFT)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_ACK;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_SI_TO_SWIFT)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_CREATED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_NACKED)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_NACK;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_SI_TO_SWIFT)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_ROUTED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_REJECTED_LOCALLY)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_REJECTED_LOCALLY;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_SI_TO_SWIFT)
							&& intvName.equalsIgnoreCase(
									MashreqFederatedReportConstants.RINTV_NAME_AUTHORIZATION_NOT_PRESENT)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_REJECTED_LOCALLY)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_REJECTED_LOCALLY;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_MPC)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_REJECTED_LOCALLY;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_MPM)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_REJECTED_LOCALLY;
						maxTime = intvDateTime;
						break;
					} else if (mpfnName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MPFN_NONE)
							&& intvName.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_NAME_INSTANCE_COMPLETED)
							&& networkDeliveryStatus.equalsIgnoreCase(
									MashreqFederatedReportConstants.RAPPE_NETWORK_DELIVERY_STATUS_ACKED)) {
						activityStatus = MashreqFederatedReportConstants.RINTV_MESG_REJECTED_LOCALLY;
						maxTime = intvDateTime;
						break;
					}
				}
			}
			clonedRecord = clonePaymentInvestigationReportOutput(rMessageRecord);
			if (matchedDefaultOutput != null) {
				clonedRecord.setComponentDetailId(matchedDefaultOutput.getComponentDetailId());
			}
			clonedRecord.setActivityStatus(activityStatus);
			if (activityStatus.equalsIgnoreCase(MashreqFederatedReportConstants.RINTV_MESG_LIVE)) {
				maxTime = new Timestamp(new Date().getTime());
			}
			clonedRecord.setCompletionTime(maxTime);
		}
		if (clonedRecord != null) {
			swiftContext.setRintvRecord(clonedRecord);
		}

	}

	private List<SWIFTReportContext> getMatchingTypeRecords(List<SWIFTReportContext> swiftContextList,
			MessageType mesgType) {
		List<SWIFTReportContext> matchedTypeRecords = new ArrayList<SWIFTReportContext>();
		matchedTypeRecords = swiftContextList.stream()
				.filter(swiftContext -> swiftContext.getMessageType().equals(mesgType)).collect(Collectors.toList());
		return matchedTypeRecords;
	}

	private void processRIntvDetectionQuery(ReportComponentDTO component,
			ReportComponentDetailDTO componentDetailObject, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext, List<SWIFTReportContext> swiftContextList) {
		swiftContextList.stream().forEach(swiftContext -> {
			if (swiftContext.getMessageType() == MessageType.INCOMING
					|| swiftContext.getMessageType() == MessageType.OUTGOING
					|| swiftContext.getMessageType() == MessageType.INCOMING_ENQUIRY) {
				List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
				FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
				ReportComponentDetailContext context = new ReportComponentDetailContext();
				referenceNumPrompt.setPromptValue(piReportInput.getUserReferenceNum());
				promptInfoList.add(referenceNumPrompt);
				promptInfoList.add(piReportInput.getCountryCodePrompt());
				promptInfoList.add(piReportInput.getTimeInDaysPrompt());
				context.setQueryString(componentDetailObject.getQuery());
				context.setPrompts(promptInfoList);
				context.setExecutionId(reportContext.getExecutionId());
				updatePrompts(swiftContext, context);
				List<ReportDefaultOutput> reportOutputList = queryExecutorService.executeQuery(componentDetailObject,
						context);
				updateRMesgDataWithRIntvDetection(reportOutputList, context, swiftContext);
			}
		});

	}

	private void updateRMesgDataWithRIntvDetection(List<ReportDefaultOutput> reportOutputList,
			ReportComponentDetailContext context, SWIFTReportContext swiftContext) {

		String detectionId = null;
		for (ReportDefaultOutput defaultOutput : reportOutputList) {
			List<Object> rowData = defaultOutput.getRowData();
			detectionId = UtilityClass.getStringRepresentation(rowData.get(0));
		}
		if (detectionId != null) {
			swiftContext.setDetectionId(detectionId);
		}

	}

	private void processRTextQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetailObject,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			List<SWIFTReportContext> swiftContextList) {
		swiftContextList.stream().forEach(swiftContext -> {
			List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
			FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
			ReportComponentDetailContext context = new ReportComponentDetailContext();
			referenceNumPrompt.setPromptValue(piReportInput.getUserReferenceNum());
			promptInfoList.add(referenceNumPrompt);
			promptInfoList.add(piReportInput.getCountryCodePrompt());
			promptInfoList.add(piReportInput.getTimeInDaysPrompt());
			context.setQueryString(componentDetailObject.getQuery());
			context.setPrompts(promptInfoList);
			context.setExecutionId(reportContext.getExecutionId());
			updatePrompts(swiftContext, context);
			List<ReportDefaultOutput> reportOutputList = queryExecutorService.executeQuery(componentDetailObject,
					context);
			updateRMesgDataWithRTextField(reportOutputList, context, swiftContext);
		});
	}

	private PaymentInvestigationReportOutput updateRMesgDataWithRTextField(List<ReportDefaultOutput> reportOutputList,
			ReportComponentDetailContext context, SWIFTReportContext swiftContext) {

		PaymentInvestigationReportOutput rMessageRecord = swiftContext.getrMessageRecord();
		String debitAccount = null;
		String beneficaryAccount = null;
		String paymentStatus = null;
		for (ReportDefaultOutput defaultOutput : reportOutputList) {
			List<Object> rowData = defaultOutput.getRowData();
			String fieldCode = UtilityClass.getStringRepresentation(rowData.get(0));
			String value = UtilityClass.getStringRepresentation(rowData.get(1));
			if (fieldCode.equalsIgnoreCase(MashreqFederatedReportConstants.DEBIT_ACCOUNT_SWIFT_CODE)) {
				debitAccount = value;
			} else if (fieldCode.equalsIgnoreCase(MashreqFederatedReportConstants.BENEFICARY_ACCOUNT_SWIFT_CODE)) {
				beneficaryAccount = value;
			} else if (fieldCode.equalsIgnoreCase(MashreqFederatedReportConstants.PAYMENT_STATUS_CODE)) {
				paymentStatus = value;
			}
		}
		if (null != debitAccount && debitAccount.length() >= 24) {
			debitAccount = debitAccount.substring(12, 24);
		}
		rMessageRecord.setDebitAccount(debitAccount);
		rMessageRecord.setBeneficaryAccount(beneficaryAccount);
		swiftContext.setPaymentStatus(paymentStatus);
		return rMessageRecord;

	}

	private void updatePrompts(SWIFTReportContext swiftContext, ReportComponentDetailContext context) {

		FederatedReportPromptDTO aidPrompt = new FederatedReportPromptDTO();
		aidPrompt.setPromptKey(MashreqFederatedReportConstants.AID_PROMPT_KEY);
		aidPrompt.setPromptValue(swiftContext.getAid());
		aidPrompt.setValueType(PromptValueType.VALUE);
		context.getPrompts().add(aidPrompt);
		FederatedReportPromptDTO sumidhPrompt = new FederatedReportPromptDTO();
		sumidhPrompt.setPromptKey(MashreqFederatedReportConstants.S_UMIDH_PROMPT_KEY);
		sumidhPrompt.setPromptValue(swiftContext.getsUmidh());
		sumidhPrompt.setValueType(PromptValueType.VALUE);
		context.getPrompts().add(sumidhPrompt);
		FederatedReportPromptDTO sumidlPrompt = new FederatedReportPromptDTO();
		sumidlPrompt.setPromptKey(MashreqFederatedReportConstants.S_UMIDL_PROMPT_KEY);
		sumidlPrompt.setPromptValue(swiftContext.getsUmidl());
		sumidlPrompt.setValueType(PromptValueType.VALUE);
		context.getPrompts().add(sumidlPrompt);

	}

	private void processRMesgQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetailObject,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			List<SWIFTReportContext> swiftContextList) {
		Set<String> processedReferenceNums = new HashSet<String>();
		Set<String> toBeProcessedRefNums = new HashSet<String>();
		toBeProcessedRefNums.add(piReportInput.getUserReferenceNum());
		do {
			List<SWIFTReportContext> swiftReportContextList = processReferenceNums(component, componentDetailObject,
					piReportInput, componentDetailKey, reportContext, toBeProcessedRefNums);
			processedReferenceNums.addAll(toBeProcessedRefNums);
			toBeProcessedRefNums = new HashSet<String>();
			if (!swiftReportContextList.isEmpty()) {
				swiftReportContextList.stream().forEach(swiftReportContext -> {
					addDataToMainList(swiftReportContext, swiftContextList);
				});

				for (SWIFTReportContext contextD : swiftContextList) {
					addDataToMainList(contextD, swiftContextList);
					String referenceNum = contextD.getReferenceNum();
					if (!processedReferenceNums.contains(referenceNum)) {
						toBeProcessedRefNums.add(referenceNum);
					}
					String relatedRefNum = contextD.getRelatedReferenceNum();
					if (null != swiftContextList && !processedReferenceNums.contains(relatedRefNum)) {
						toBeProcessedRefNums.add(relatedRefNum);
					}
				}
			}

		} while (toBeProcessedRefNums.size() > 0);
	}

	private void addDataToMainList(SWIFTReportContext contextD, List<SWIFTReportContext> swiftContextList) {
		if (!isMatchingRecordExist(swiftContextList, contextD)) {
			swiftContextList.add(contextD);
		}
	}

	private boolean isMatchingRecordExist(List<SWIFTReportContext> contextList, SWIFTReportContext contextD) {
		boolean matchingRecordExist = false;
		for (SWIFTReportContext context : contextList) {
			if (context.getReferenceNum().equalsIgnoreCase(contextD.getReferenceNum())
					&& context.getMesgType().equalsIgnoreCase(contextD.getMesgType())) {
				matchingRecordExist = true;
				break;
			}
		}
		return matchingRecordExist;
	}

	private List<SWIFTReportContext> processReferenceNums(ReportComponentDTO component,
			ReportComponentDetailDTO componentDetailObject, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext, Set<String> toBeProcessedRefNums) {
		List<SWIFTReportContext> contextList = new ArrayList<SWIFTReportContext>();
		List<FederatedReportPromptDTO> promptInfoList = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO referenceNumPrompt = piReportInput.getReferenceNumPrompt();
		toBeProcessedRefNums.stream().forEach(referenceNumber -> {
			ReportComponentDetailContext context = new ReportComponentDetailContext();
			referenceNumPrompt.setPromptValue(referenceNumber);
			promptInfoList.add(referenceNumPrompt);
			promptInfoList.add(piReportInput.getCountryCodePrompt());
			promptInfoList.add(piReportInput.getTimeInDaysPrompt());
			context.setQueryString(componentDetailObject.getQuery());
			context.setPrompts(promptInfoList);
			context.setExecutionId(reportContext.getExecutionId());
			List<ReportDefaultOutput> queryResponse = queryExecutorService.executeQuery(componentDetailObject, context);
			List<SWIFTReportContext> rMesgList = populateRMesgData(queryResponse);
			if (!queryResponse.isEmpty()) {
				contextList.addAll(rMesgList);
			}
		});
		return contextList;
	}

	private List<SWIFTReportContext> populateRMesgData(List<ReportDefaultOutput> reportOutputList) {
		List<SWIFTReportContext> swiftReportContextList = new ArrayList<SWIFTReportContext>();
		if (!reportOutputList.isEmpty()) {
			reportOutputList.stream().forEach(reportOutput -> {
				List<Object> rowData = reportOutput.getRowData();
				PaymentInvestigationReportOutput piReportOutput = new PaymentInvestigationReportOutput();
				piReportOutput.setComponentDetailId(reportOutput.getComponentDetailId());
				piReportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
				piReportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
				piReportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(2)));
				piReportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(3)));
				piReportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(4)));
				piReportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(5)));
				piReportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(6)));
				piReportOutput.setReceiver(UtilityClass.getStringRepresentation(rowData.get(7)));
				piReportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(8)));
				piReportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(9)));
				piReportOutput.setSource(MashreqFederatedReportConstants.SWIFT_SOURCE_SYSTEM);
				String aid = UtilityClass.getStringRepresentation(rowData.get(10));
				String sUmidl = UtilityClass.getStringRepresentation(rowData.get(11));
				String sUmidh = UtilityClass.getStringRepresentation(rowData.get(12));
				String mesgType = UtilityClass.getStringRepresentation(rowData.get(13));
				String mesgFormat = UtilityClass.getStringRepresentation(rowData.get(14));
				String relatedRefNum = UtilityClass.getStringRepresentation(rowData.get(15));
				String sender = UtilityClass.getStringRepresentation(rowData.get(16));
				piReportOutput.setAid(aid);
				piReportOutput.setUmidl(sUmidl);
				piReportOutput.setUmidh(sUmidh);
				piReportOutput.setMesgType(mesgType);
				piReportOutput.setMessageSubFormat(mesgFormat);
				SWIFTReportContext swiftContext = new SWIFTReportContext();
				swiftContext.setAid(aid);
				swiftContext.setsUmidl(sUmidl);
				swiftContext.setsUmidh(sUmidh);
				swiftContext.setMesgType(mesgType);
				swiftContext.setMesgFormat(mesgFormat);
				swiftContext.setrMessageRecord(piReportOutput);
				swiftContext.setReferenceNum(piReportOutput.getSourceRefNum());
				swiftContext.setRelatedReferenceNum(relatedRefNum);
				swiftContext.setSender(sender);
				swiftReportContextList.add(swiftContext);

			});
		}
		return swiftReportContextList;
	}

	private ReportComponentDetailDTO getMatchedInstanceComponentDetail(
			Set<ReportComponentDetailDTO> componentDetailList, String componentDetailKey) {
		ReportComponentDetailDTO componentDetailObject = componentDetailList.stream()
				.filter(componentDetails -> componentDetails.getQueryKey().equalsIgnoreCase(componentDetailKey))
				.findFirst().orElse(null);
		return componentDetailObject;
	}

	private List<SWIFTMessageDetailsReportOutput> processSwiftDetailedReport(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, ReportContext reportContext) {
		FederatedReportPromptDTO detailedType = swiftDetailedReportDTO.getDetailedType();
		ReportComponentDTO component = swiftDetailedReportDTO.getComponent();
		switch (detailedType.getPromptValue()) {
		case (MashreqFederatedReportConstants.DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RMESG):
		case (MashreqFederatedReportConstants.DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RINTV):
			return populateRMesgDetailData(swiftDetailedReportDTO, component, reportContext);

		}
		return null;
	}

	private List<SWIFTMessageDetailsReportOutput> populateRMesgDetailData(
			SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO, ReportComponentDTO component,
			ReportContext reportContext) {

		MessageDetailsFederatedReportInput reportInput = new MessageDetailsFederatedReportInput();
		reportInput.setMessageSubFormatPrompt(swiftDetailedReportDTO.getMessageSubFormatPrompt());
		reportInput.setMessageTypePrompt(swiftDetailedReportDTO.getMessageTypePrompt());
		reportInput.setReferenceNumPrompt(swiftDetailedReportDTO.getReferenceNumPrompt());
		return processMessageDetailsReport(reportInput, component, reportContext);
	}

	private List<SWIFTMessageDetailsReportOutput> processMessageDetailsReport(
			MessageDetailsFederatedReportInput messagingDetailsInput, ReportComponentDTO component,
			ReportContext reportContext) {

		Set<ReportComponentDetailDTO> componentDetailsList = component.getReportComponentDetails();
		List<SWIFTMessageDetailsReportOutput> messageDetails = new ArrayList<SWIFTMessageDetailsReportOutput>();

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
			List<SWIFTMessageDetailsReportOutput> messageDetails) {

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

	private void addToMessageDetails(List<SWIFTMessageDetailsReportOutput> messageDetails, String label, String value,
			Long compDetailId) {
		if (null != value) {
			messageDetails.add(populateFederatedReportOutput(label, value, compDetailId));
		}
	}

	private SWIFTMessageDetailsReportOutput populateFederatedReportOutput(String key, String value, Long compDetailId) {
		SWIFTMessageDetailsReportOutput reportOutput = new SWIFTMessageDetailsReportOutput();
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
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportDefaultOutput> federatedReportOutputList = new ArrayList<ReportDefaultOutput>();
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

	private void processMessageDetailsStxFieldEntryViewData(List<ReportDefaultOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		if (!federatedReportOutputList.isEmpty()) {
			Map<String, StxEntryFieldViewInfo> fieldInfoMap = new HashMap<String, StxEntryFieldViewInfo>();
			for (ReportDefaultOutput federatedReportDefaultOutput : federatedReportOutputList) {
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
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportDefaultOutput> federatedReportOutputList = new ArrayList<ReportDefaultOutput>();
		promptsList.add(messagingDetailsInput.getMessageTypePrompt());
		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());
		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsStxMessageData(federatedReportOutputList, swiftDetailsReportObjectDTO);
	}

	private void processMessageDetailsStxMessageData(List<ReportDefaultOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		if (null != federatedReportOutputList) {
			ReportDefaultOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
			List<Object> rowData = federatedReportDefaultOutput.getRowData();
			String swiftInput = UtilityClass.getStringRepresentation(rowData.get(1));
			swiftDetailsReportObjectDTO.setSwiftInput(swiftInput);
		}
	}

	private void processMessageDetailsRCorrQuery(ReportComponentDetailDTO componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentKey,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender, ReportContext reportContext) {
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		FederatedReportPromptDTO corrBankPrompt = new FederatedReportPromptDTO();
		List<ReportDefaultOutput> federatedReportOutputList = new ArrayList<ReportDefaultOutput>();

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

	private void processMessageDetailsRCorrData(List<ReportDefaultOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO, boolean sender) {
		if (!federatedReportOutputList.isEmpty()) {
			ReportDefaultOutput federatedReportDefaultOutput = federatedReportOutputList.get(0);
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
		ReportComponentDetailContext context = new ReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportDefaultOutput> federatedReportOutputList = new ArrayList<ReportDefaultOutput>();
		updateMessageDetailsInternalPrompts(messagingDetailsInput, promptsList, swiftDetailsReportObjectDTO);

		context.setQueryId(componentDetails.getId());
		context.setQueryKey(componentDetails.getQueryKey());
		context.setQueryString(componentDetails.getQuery());
		context.setPrompts(promptsList);
		context.setExecutionId(reportContext.getExecutionId());
		federatedReportOutputList = queryExecutorService.executeQuery(componentDetails, context);
		processMessageDetailsRTextFieldData(federatedReportOutputList, swiftDetailsReportObjectDTO);

	}

	private void processMessageDetailsRTextFieldData(List<ReportDefaultOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		if (!federatedReportOutputList.isEmpty()) {
			List<MessageField> messageFields = new ArrayList<MessageField>();
			for (ReportDefaultOutput federatedReportDefaultOutput : federatedReportOutputList) {
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
		List<ReportDefaultOutput> federatedReportOutputList = new ArrayList<ReportDefaultOutput>();
		ReportComponentDetailContext context = new ReportComponentDetailContext();

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

	private String processMessageDetailsRIntvData(List<ReportDefaultOutput> federatedReportOutputList,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {

		String activityStatus = MashreqFederatedReportConstants.RINTV_MESG_LIVE;
		if (!federatedReportOutputList.isEmpty()) {
			for (ReportDefaultOutput federatedReportOutput : federatedReportOutputList) {

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

		ReportComponentDetailContext context = new ReportComponentDetailContext();
		List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
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

	private void processMessageDetailsRMesgData(List<ReportDefaultOutput> flexReportExecuteResponse,
			SwiftDetailsReportObjectDTO swiftDetailsReportObjectDTO) {
		Boolean messageFound = Boolean.FALSE;
		if (!flexReportExecuteResponse.isEmpty()) {
			messageFound = Boolean.TRUE;
			ReportDefaultOutput federatedReportDefaultOutput = flexReportExecuteResponse.get(0);
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
}