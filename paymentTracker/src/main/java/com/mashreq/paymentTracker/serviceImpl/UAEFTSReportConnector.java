package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.mashreq.paymentTracker.dto.UAEFTSDetailedReportInput;
import com.mashreq.paymentTracker.dto.UAEFTSNetworkDataContext;
import com.mashreq.paymentTracker.dto.UAEFTSReportContext;
import com.mashreq.paymentTracker.dto.UAEFTSReportDataContext;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.MessageType;
import com.mashreq.paymentTracker.type.PromptValueType;
import com.mashreq.paymentTracker.utility.UAEFTSTableType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class UAEFTSReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(UAEFTSReportConnector.class);
	private static final String FILENAME = "UAEFTSReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof UAEFTSDetailedReportInput) {
			UAEFTSDetailedReportInput UAEFTSDetailedReportInput = (UAEFTSDetailedReportInput) reportInput;
			return processUAEFTSDetailedReport(UAEFTSDetailedReportInput, reportContext);
		} else if (reportInput instanceof PaymentInvestigationReportInput) {
			processPaymentInvestigationReport(reportInput, reportContext);
		}
		return null;
	}

	private void processPaymentInvestigationReport(ReportInput reportInput, ReportContext reportContext) {
		PaymentInvestigationReportInput piReportInput = (PaymentInvestigationReportInput) reportInput;
		ReportComponentDTO component = piReportInput.getComponent();

		Set<ReportComponentDetailDTO> cannedReportInstanceComponentDetails = component.getReportComponentDetails();
		if (cannedReportInstanceComponentDetails != null) {

			UAEFTSReportContext uaeftsReportContext = new UAEFTSReportContext();
			// dont process enquiries if role is customer
			if (!(piReportInput.getRoleName().equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_REPORTING_ROLE)
					|| piReportInput.getRoleName()
							.equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_MATRIX_REPORTING_ROLE))) {
				// process enquiries
				processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MTQA_KEY, uaeftsReportContext, reportContext);

			}
			// process output message
			processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
					MashreqFederatedReportConstants.UAEFTS_MTINPUTMSGS_DETAILS_KEY, uaeftsReportContext, reportContext);
			if (uaeftsReportContext.getOutgoingMessage() == null) {
				processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MANUALMSGS_DETAILS_KEY, uaeftsReportContext,
						reportContext);
				if (uaeftsReportContext.getOutgoingMessage() == null) {
					processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
							MashreqFederatedReportConstants.UAEFTS_MT202_KEY, uaeftsReportContext, reportContext);
					if (uaeftsReportContext.getOutgoingMessage() == null) {
						processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
								MashreqFederatedReportConstants.UAEFTS_MT202_INPUTMSGS_DETAILS_KEY, uaeftsReportContext,
								reportContext);
					}
				}
			}

			// process input message

			processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
					MashreqFederatedReportConstants.UAEFTS_FTSMSGS_KEY, uaeftsReportContext, reportContext);
			if (uaeftsReportContext.getIncomingMessage() == null) {
				processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_INCOMING_MTFN_KEY, uaeftsReportContext, reportContext);
			}

			// process amlq

			if (uaeftsReportContext.getIncomingMessage() != null) {
				uaeftsReportContext.setCurrentData(uaeftsReportContext.getIncomingMessage());
				String amlQuery = pickAmlQuery(uaeftsReportContext.getCurrentData().getTableType());
				if (amlQuery != null) {
					processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput, amlQuery,
							uaeftsReportContext, reportContext);
				}
			}

			if (uaeftsReportContext.getOutgoingMessage() != null) {
				uaeftsReportContext.setCurrentData(uaeftsReportContext.getOutgoingMessage());
				String amlQuery = pickAmlQuery(uaeftsReportContext.getCurrentData().getTableType());
				if (amlQuery != null) {
					processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput, amlQuery,
							uaeftsReportContext, reportContext);
				}
				// run the incoming_ccn to check credit confirmed or not
				processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_INCOMING_CCN_KEY, uaeftsReportContext, reportContext);
			}

			if (uaeftsReportContext.getIncomingEnquiries() != null) {
				for (UAEFTSReportDataContext dataContext : uaeftsReportContext.getIncomingEnquiries()) {
					uaeftsReportContext.setCurrentData(dataContext);
					String amlQuery = pickAmlQuery(uaeftsReportContext.getCurrentData().getTableType());
					if (amlQuery != null) {
						processComponentDetail(component, cannedReportInstanceComponentDetails, piReportInput, amlQuery,
								uaeftsReportContext, reportContext);
					}
				}
			}

			// process context
			processUAEFTSContext(uaeftsReportContext, piReportInput, reportContext);
		}
		
	}

	private void processComponentDetail(ReportComponentDTO component, Set<ReportComponentDetailDTO> ComponentDetails,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey,
			UAEFTSReportContext uaeftsReportContext, ReportContext reportContext) {
		ReportComponentDetailDTO componentDetail = getMatchedInstanceComponentDetail(ComponentDetails,
				componentDetailKey);
		if (componentDetail != null) {
			if (MashreqFederatedReportConstants.UAEFTS_MTQA_KEY.equalsIgnoreCase(componentDetailKey)) {
				processMTQAQuery(component, componentDetail, piReportInput, componentDetailKey, reportContext,
						uaeftsReportContext);
			} else if (MashreqFederatedReportConstants.UAEFTS_MTINPUTMSGS_DETAILS_KEY
					.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MANUALMSGS_DETAILS_KEY
							.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MT202_KEY.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MT202_INPUTMSGS_DETAILS_KEY
							.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_FTSMSGS_KEY.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_INCOMING_MTFN_KEY.equalsIgnoreCase(componentDetailKey)) {
				processMessageQuery(component, componentDetail, piReportInput, componentDetailKey, reportContext,
						uaeftsReportContext);
			} else if (MashreqFederatedReportConstants.UAEFTS_AML_MTINPUTMSGS_DETAILS_KEY
					.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MANUALMSGS_DETAILS_KEY
							.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MT202_KEY.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_MT202_INPUTMSGS_DETAILS_KEY
							.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_AML_FTSMSGS_KEY.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_AML_INCOMING_MTFN_KEY.equalsIgnoreCase(componentDetailKey)
					|| MashreqFederatedReportConstants.UAEFTS_AML_MTQA_KEY.equalsIgnoreCase(componentDetailKey)) {
				processAMLQuery(component, componentDetail, piReportInput, componentDetailKey, reportContext,
						uaeftsReportContext);

			} else if (MashreqFederatedReportConstants.UAEFTS_INCOMING_CCN_KEY.equalsIgnoreCase(componentDetailKey)) {
				processIncomingCCNQuery(component, ComponentDetails, piReportInput, componentDetailKey, reportContext,
						uaeftsReportContext);
			}
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}

	}

	private void processUAEFTSContext(UAEFTSReportContext uaeftsReportContext,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		GatewayDataContext gatewayDataContext = piReportInput.getGatewayDataContext();
		processIncomingMessage(uaeftsReportContext.getIncomingMessage(), gatewayDataContext);
		processOutgoingMessage(uaeftsReportContext.getOutgoingMessage(), gatewayDataContext, piReportInput,
				reportContext);
		processIncomingEnquiries(uaeftsReportContext.getIncomingEnquiries(), gatewayDataContext);
		processOutgoingEnquiries(uaeftsReportContext.getOutgoingEnquiries(), gatewayDataContext);
	}

	private void processOutgoingEnquiries(List<UAEFTSReportDataContext> outgoingEnquiries,
			GatewayDataContext gatewayDataContext) {
		if (outgoingEnquiries != null) {
			for (UAEFTSReportDataContext enquiry : outgoingEnquiries) {
				enquiry.getInputMessage().setActivity(MashreqFederatedReportConstants.GATEWAY_MESSAGE_OUTGOING_ACTIVITY
						+ " : " + enquiry.getMesgType());
				gatewayDataContext.addOutgoingEnquiry(enquiry.getReferenceNum(), new GatewayDataMessageContext(
						enquiry.getInputMessage(), enquiry.getReferenceNum(), enquiry.getMesgType()));
			}
		}
	}

	private void processIncomingEnquiries(List<UAEFTSReportDataContext> incomingEnquiries,
			GatewayDataContext gatewayDataContext) {
		if (incomingEnquiries != null) {
			for (UAEFTSReportDataContext enquiry : incomingEnquiries) {
				PaymentInvestigationReportOutput inputMessage = enquiry.getInputMessage();
				inputMessage.setActivity(MashreqFederatedReportConstants.GATEWAY_MESSAGE_INCOMING_ACTIVITY + " : "
						+ enquiry.getMesgType());

				for (PaymentInvestigationReportOutput complianceMsg : enquiry.getComplianceMessages()) {
					if (complianceMsg != null) {
						if (complianceMsg.getActivity()
								.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY)) {
							complianceMsg.setActivity(
									MashreqFederatedReportConstants.GATEWAY_MESSAGE_SCREENING_PROCESSED_ACTIVITY + " : "
											+ enquiry.getMesgType());
						} else {
							complianceMsg.setActivity(
									MashreqFederatedReportConstants.GATEWAY_MESSAGE_SCREENING_PROCESSED_ACTIVITY + "("
											+ complianceMsg.getActivity() + ")" + " : " + enquiry.getMesgType());
						}
					}
				}
				GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
				messageContext.setMessageRef(enquiry.getReferenceNum());
				messageContext.setMessageType(enquiry.getMesgType());
				messageContext.setNetworkRecord(inputMessage);
				messageContext.setScreeningProcessedRecord(enquiry.getComplianceMessages());
				gatewayDataContext.addIncomingEnquiry(enquiry.getReferenceNum(), messageContext);
			}
		}
	}

	private void processOutgoingMessage(UAEFTSReportDataContext outgoingMessage, GatewayDataContext gatewayDataContext,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		if (outgoingMessage != null) {
			PaymentInvestigationReportOutput screeningMessage = outgoingMessage.getInputMessage();
			screeningMessage.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_ACTIVITY + " : "
					+ outgoingMessage.getMesgType());

			if (outgoingMessage != null && outgoingMessage.getComplianceMessages() != null) {
				for (PaymentInvestigationReportOutput complianceMsg : outgoingMessage.getComplianceMessages()) {
					if (complianceMsg != null) {
						if (complianceMsg.getActivity()
								.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY)) {
							complianceMsg.setActivity(
									MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY + " : "
											+ outgoingMessage.getMesgType());
						} else {
							complianceMsg.setActivity(
									MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY + "("
											+ complianceMsg.getActivity() + ")" + " : "
											+ outgoingMessage.getMesgType());
						}
					}
				}
			}
			PaymentInvestigationReportOutput networkRecord = null;
			PaymentInvestigationReportOutput nackRecord = null;
			if (!MashreqFederatedReportConstants.UAEFTS_FORMAT_ACTION_REPAIR_STATUS
					.equalsIgnoreCase(outgoingMessage.getFormatAction())) {
				networkRecord = populateProcessingData(outgoingMessage);
				// process outgoing mtfn table for ack/nack cases
				ReportComponentDTO component = piReportInput.getComponent();
				String componentDetailKey = MashreqFederatedReportConstants.UAEFTS_OUTGOING_MTFN_KEY;
				// set the component details to gateway context
				ReportComponentDTO reportComponentObj = new ReportComponentDTO();
				Set<ReportComponentDetailDTO> componentDetails = reportComponentObj.getReportComponentDetails();
				;
				ReportComponentDetailDTO componentDetail = getMatchedInstanceComponentDetail(componentDetails,
						componentDetailKey);
				List<UAEFTSNetworkDataContext> outgoingMTFNData = processOutgoingMTFNQuery(component, componentDetail,
						piReportInput, MashreqFederatedReportConstants.UAEFTS_OUTGOING_MTFN_KEY, reportContext,
						outgoingMessage);
				if (outgoingMTFNData != null) {
					UAEFTSNetworkDataContext ackDataRow = getMatchingNetworkDataContext(outgoingMTFNData, "ACK");
					UAEFTSNetworkDataContext nackDataRow = getMatchingNetworkDataContext(outgoingMTFNData, "NAK");
					if (nackDataRow != null && ackDataRow == null) {
						networkRecord.setCompletionTime(nackDataRow.getAckTime());
						networkRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_NACK);
					} else if (ackDataRow != null && nackDataRow == null) {
						networkRecord.setCompletionTime(ackDataRow.getAckTime());
						networkRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_ACK);
					} else if (ackDataRow != null && nackDataRow != null) {
						networkRecord.setCompletionTime(nackDataRow.getAckTime());
						networkRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_NACK);
						nackRecord = clonePaymentInvestigationReportOutput(networkRecord);
						nackRecord.setActivity(MashreqFederatedReportConstants.NACK_ACTIVITY);
						nackRecord.setActivityStatus(MashreqFederatedReportConstants.RINTV_MESG_ACK);
						nackRecord.setLandingTime(nackDataRow.getAckTime());
						nackRecord.setCompletionTime(ackDataRow.getAckTime());

					}
				}
			}
			GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
			messageContext.setMessageRef(outgoingMessage.getReferenceNum());
			messageContext.setMessageType(outgoingMessage.getMesgType());
			messageContext.setNetworkRecord(networkRecord);
			messageContext.setNetworkNackRecord(nackRecord);
			messageContext.setScreeningRecord(screeningMessage);
			messageContext.setScreeningProcessedRecord(outgoingMessage.getComplianceMessages());
			if (outgoingMessage.getCcnMessage() != null) {
				messageContext.setCreditConfirmedRecord(outgoingMessage.getCcnMessage());
			}
			gatewayDataContext.setOutgoingMessage(messageContext);
		}
	}

	private List<UAEFTSNetworkDataContext> processOutgoingMTFNQuery(ReportComponentDTO component,
			ReportComponentDetailDTO componentDetail, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext, UAEFTSReportDataContext outgoingMessageContext) {
		ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail, piReportInput,
				reportContext);

		List<ReportDefaultOutput> execute = queryExecutorService.executeQuery(componentDetail, context);
		return populateNetworkData(context);
	}

	private UAEFTSNetworkDataContext getMatchingNetworkDataContext(List<UAEFTSNetworkDataContext> dataContext,
			String matchedKey) {
		UAEFTSNetworkDataContext matchedDataContext = null;
		for (UAEFTSNetworkDataContext networkDataContext : dataContext) {
			if (networkDataContext.getStatus().equalsIgnoreCase(matchedKey)) {
				matchedDataContext = networkDataContext;
				break;
			}
		}
		return matchedDataContext;
	}

	private PaymentInvestigationReportOutput populateProcessingData(UAEFTSReportDataContext uaeftsContext) {
		PaymentInvestigationReportOutput reportOutput = clonePaymentInvestigationReportOutput(
				uaeftsContext.getInputMessage());
		reportOutput.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_OUTWARD_NETWORK_ACTIVITY + " : "
				+ uaeftsContext.getMesgType());
		reportOutput.setActivityStatus(uaeftsContext.getFtsStatus());
		if (uaeftsContext.getFileCreatedOn() == null) {
			reportOutput.setCompletionTime(new Timestamp(new Date().getTime()));
		} else {
			reportOutput.setCompletionTime(uaeftsContext.getFileCreatedOn());
		}
		// pick the latest compliance message from the list
		if (uaeftsContext.getComplianceMessages() != null) {
			PaymentInvestigationReportOutput compMessage = uaeftsContext.getComplianceMessages()
					.get(uaeftsContext.getComplianceMessages().size() - 1);
			reportOutput.setLandingTime(new Timestamp(compMessage.getCompletionTime().getTime()));
		}
		return reportOutput;
	}

	private void processIncomingMessage(UAEFTSReportDataContext incomingMessage,
			GatewayDataContext gatewayDataContext) {
		if (incomingMessage != null) {
			PaymentInvestigationReportOutput inputMessage = incomingMessage.getInputMessage();
			inputMessage.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_INWARD_NETWORK_ACTIVITY + " : "
					+ incomingMessage.getMesgType());
			PaymentInvestigationReportOutput screeningRecord = clonePaymentInvestigationReportOutput(inputMessage);
			screeningRecord.setActivity(MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_ACTIVITY + " : "
					+ incomingMessage.getMesgType());

			for (PaymentInvestigationReportOutput complianceMesg : incomingMessage.getComplianceMessages()) {
				if (complianceMesg != null) {
					if (complianceMesg.getActivity()
							.equalsIgnoreCase(MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY)) {
						complianceMesg.setActivity(
								MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY + " : "
										+ incomingMessage.getMesgType());
					} else {
						complianceMesg.setActivity(
								MashreqFederatedReportConstants.GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY + "("
										+ complianceMesg.getActivity() + ")" + " : " + incomingMessage.getMesgType());
					}
				}
			}
			GatewayDataMessageContext messageContext = new GatewayDataMessageContext();
			messageContext.setMessageRef(incomingMessage.getReferenceNum());
			messageContext.setMessageType(incomingMessage.getMesgType());
			messageContext.setNetworkRecord(inputMessage);
			messageContext.setScreeningRecord(screeningRecord);
			messageContext.setScreeningProcessedRecord(incomingMessage.getComplianceMessages());
			gatewayDataContext.setIncomingMessage(messageContext);
		}
	}

	private void processIncomingCCNQuery(ReportComponentDTO component, Set<ReportComponentDetailDTO> componentDetails,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			UAEFTSReportContext uaeftsReportContext) {
		UAEFTSReportDataContext uaeftsReportDataContext = uaeftsReportContext.getCurrentData();

		// before you process the component detail, if any additional prompt needs to be
		// set internally between tables has to be done before this step

		ReportComponentDetailContext context1 = null;
		for (ReportComponentDetailDTO componentDetail : componentDetails) {

			ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail, piReportInput,
					reportContext);
			updatePrompts(context, uaeftsReportDataContext, componentDetailKey);
			context1 = new ReportComponentDetailContext();
			List<ReportDefaultOutput> execute = queryExecutorService.executeQuery(componentDetail, context1);
		}

		populateIncomingCCNData(context1, uaeftsReportDataContext);
	}

	private void populateIncomingCCNData(ReportComponentDetailContext context,
			UAEFTSReportDataContext uaeftsReportDataContext) {
		List<ReportDefaultOutput> rawComponentDetailData = context.getComponentDetailData();
		if (rawComponentDetailData != null) {
			ReportDefaultOutput defaultOutput = rawComponentDetailData.get(0);
			List<Object> rowData = defaultOutput.getRowData();
			PaymentInvestigationReportOutput reportOutput = createPaymentInvestigationOutput(defaultOutput);
			reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
			reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
			reportOutput.setActivity(UtilityClass.getStringRepresentation(rowData.get(1)));
			reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(2)));
			reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_UAEFTS);
			reportOutput.setSourceRefNum(uaeftsReportDataContext.getReferenceNum());
			uaeftsReportDataContext.setCcnMessage(reportOutput);
		}
	}

	private void processAMLQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetail,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			UAEFTSReportContext uaeftsReportContext) {
		UAEFTSReportDataContext uaeftsReportDataContext = uaeftsReportContext.getCurrentData();
		if (!MashreqFederatedReportConstants.UAEFTS_FORMAT_ACTION_REPAIR_STATUS
				.equalsIgnoreCase(uaeftsReportDataContext.getFormatAction())) {
			// pick the correct aml query based on table type
			ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail, piReportInput,
					reportContext);
			// before you process the component detail, if any additional prompt needs to be
			// set internally between tables has to be done before this step
			updatePrompts(context, uaeftsReportDataContext, componentDetailKey);

			List<ReportDefaultOutput>execute = queryExecutorService.executeQuery(componentDetail, context);

			populateActivityLogMessageData(context, uaeftsReportDataContext);
		}
	}

	private List<UAEFTSNetworkDataContext> populateNetworkData(ReportComponentDetailContext context) {
		List<UAEFTSNetworkDataContext> networkDataList = new ArrayList<UAEFTSNetworkDataContext>();
		List<ReportDefaultOutput> rawComponentDetailData = context.getComponentDetailData();
		if (rawComponentDetailData != null) {
			for (ReportDefaultOutput defaultOutput : rawComponentDetailData) {
				UAEFTSNetworkDataContext networkData = new UAEFTSNetworkDataContext();
				List<Object> rowData = defaultOutput.getRowData();
				networkData.setStatus(UtilityClass.getStringRepresentation(rowData.get(0)));
				networkData.setAckTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
				networkDataList.add(networkData);
			}
		}
		return networkDataList;
	}

	private void populateActivityLogMessageData(ReportComponentDetailContext context,
			UAEFTSReportDataContext uaeftsDataContext) {
		List<ReportDefaultOutput> rawComponentDetailData = context.getComponentDetailData();
		List<PaymentInvestigationReportOutput> complianceMessages = new ArrayList<PaymentInvestigationReportOutput>();
		if (rawComponentDetailData != null) {
			PaymentInvestigationReportOutput cpcCompliance = getComplianceMessageByType(rawComponentDetailData,
					MashreqFederatedReportConstants.CPC_COMPLIANCE_WORKSTAGE, uaeftsDataContext.getInputMessage());
			PaymentInvestigationReportOutput hoCompliance = getComplianceMessageByType(rawComponentDetailData,
					MashreqFederatedReportConstants.HO_COMPLIANCE_WORKSTAGE, uaeftsDataContext.getInputMessage());
			if (cpcCompliance == null && hoCompliance == null) {
				cpcCompliance = getComplianceMessageByType(rawComponentDetailData,
						MashreqFederatedReportConstants.SAFEWATCH_DEFAULT_COMPLETEDBY,
						uaeftsDataContext.getInputMessage());
			}
			if (cpcCompliance != null) {
				if (uaeftsDataContext.getAmlTime() == null) {
					cpcCompliance.setLandingTime(
							new Timestamp(uaeftsDataContext.getInputMessage().getCompletionTime().getTime()));
				} else {
					cpcCompliance.setLandingTime(uaeftsDataContext.getAmlTime());
				}
			}
			if (hoCompliance != null && cpcCompliance == null) {
				if (uaeftsDataContext.getAmlTime() == null) {
					hoCompliance.setLandingTime(
							new Timestamp(uaeftsDataContext.getInputMessage().getCompletionTime().getTime()));
				} else {
					hoCompliance.setLandingTime(uaeftsDataContext.getAmlTime());
				}
			}
			// if HO compliance is there, copy the landing time of cpc to ho
			if (cpcCompliance != null && hoCompliance != null) {
				hoCompliance.setLandingTime(new Timestamp(cpcCompliance.getCompletionTime().getTime()));
			}
			if (cpcCompliance != null) {
				complianceMessages.add(cpcCompliance);
			}
			if (hoCompliance != null) {
				complianceMessages.add(hoCompliance);
			}
		}
		uaeftsDataContext.setComplianceMessages(complianceMessages);
	}

	private PaymentInvestigationReportOutput getComplianceMessageByType(
			List<ReportDefaultOutput> rawComponentDetailData, String messageType,
			PaymentInvestigationReportOutput originalMessage) {
		PaymentInvestigationReportOutput messageTypeOutput = null;
		for (ReportDefaultOutput defaultOutput : rawComponentDetailData) {
			List<Object> rowData = defaultOutput.getRowData();
			String workstage = UtilityClass.getStringRepresentation(rowData.get(2));
			if (workstage.equalsIgnoreCase(messageType)) {
				messageTypeOutput = clonePaymentInvestigationReportOutput(originalMessage);

				messageTypeOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				messageTypeOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(0)));
				String activityStatus = UtilityClass.getStringRepresentation(rowData.get(1));
				// handle the activity status based on do not know condition
				if (doNotKnowStatus(activityStatus)) {
					activityStatus = MashreqFederatedReportConstants.COMPLIANCE_DONT_KNOW_STATUS;
				}
				messageTypeOutput.setActivityStatus(activityStatus);
				messageTypeOutput.setWorkstage(workstage);
				Timestamp time = UtilityClass.getTimeStampRepresentation(rowData.get(3));
				messageTypeOutput.setCompletionTime(time);
				messageTypeOutput.setActivity(messageType);
				break;
			}
		}
		return messageTypeOutput;
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

	private void processMessageQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetail,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			UAEFTSReportContext uaeftsContext) {
		ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail, piReportInput,
				reportContext);

		List<ReportDefaultOutput> execute = queryExecutorService.executeQuery(componentDetail, context);
		// processComponentDetail(piReportInput.getComponent(), componentDetail,
		// context);
		populateInputMessageData(context, uaeftsContext, componentDetailKey);
	}

	private void populateInputMessageData(ReportComponentDetailContext context, UAEFTSReportContext uaeftsContext,
			String componentDetailKey) {
		UAEFTSReportDataContext dataContext = null;
		MessageType messageType = null;
		List<ReportDefaultOutput> rawComponentDetailData = context.getComponentDetailData();
		if (rawComponentDetailData != null) {
			ReportDefaultOutput defaultOutput = rawComponentDetailData.get(0);
			List<Object> rowData = defaultOutput.getRowData();
			PaymentInvestigationReportOutput reportOutput = createPaymentInvestigationOutput(defaultOutput);
			reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
			reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
			reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(2)));
			reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(3)));
			reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(4)));
			reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(5)));
			reportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(6)));
			reportOutput.setDebitAccount(UtilityClass.getStringRepresentation(rowData.get(7)));
			reportOutput.setReceiver(UtilityClass.getStringRepresentation(rowData.get(8)));
			reportOutput.setBeneficaryAccount(UtilityClass.getStringRepresentation(rowData.get(9)));
			reportOutput.setBeneficaryDetail(UtilityClass.getStringRepresentation(rowData.get(10)));
			reportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(11)));
			reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(12)));
			reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_UAEFTS);

			// based on additional fields, create one more record with these new fields
			Timestamp amlTime = UtilityClass.getTimeStampRepresentation(rowData.get(13));
			String isAmlRequired = UtilityClass.getStringRepresentation(rowData.get(14));
			String amlStatus = UtilityClass.getStringRepresentation(rowData.get(15));
			String ftsStatus = UtilityClass.getStringRepresentation(rowData.get(16));
			Timestamp fileCreatedOn = UtilityClass.getTimeStampRepresentation(rowData.get(17));
			String msgId = UtilityClass.getStringRepresentation(rowData.get(18));
			String formatAction = UtilityClass.getStringRepresentation(rowData.get(19));
			String mesgType = UtilityClass.getStringRepresentation(rowData.get(20));
			String cbUniqueFileId = UtilityClass.getStringRepresentation(rowData.get(21));
			dataContext = new UAEFTSReportDataContext();
			dataContext.setAmlTime(amlTime);
			dataContext.setIsAmlRequired(isAmlRequired);
			dataContext.setAmlStatus(amlStatus);
			dataContext.setFtsStatus(ftsStatus);
			dataContext.setFileCreatedOn(fileCreatedOn);
			dataContext.setMsgId(msgId);
			dataContext.setFormatAction(formatAction);
			dataContext.setReferenceNum(reportOutput.getSourceRefNum());
			dataContext.setCbUniqueFileId(cbUniqueFileId);

			// set mesg type
			reportOutput.setMesgType(mesgType);
			UAEFTSTableType tableType = null;
			// set the outward table type
			if (MashreqFederatedReportConstants.UAEFTS_MTINPUTMSGS_DETAILS_KEY.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.MTINPUTMSGS;
				messageType = MessageType.OUTGOING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (MashreqFederatedReportConstants.UAEFTS_MANUALMSGS_DETAILS_KEY
					.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.MANUALMSGS;
				messageType = MessageType.OUTGOING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (MashreqFederatedReportConstants.UAEFTS_MT202_KEY.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.MT202;
				messageType = MessageType.OUTGOING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (MashreqFederatedReportConstants.UAEFTS_MT202_INPUTMSGS_DETAILS_KEY
					.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.MT202INPUTMSGS;
				messageType = MessageType.OUTGOING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (MashreqFederatedReportConstants.UAEFTS_FTSMSGS_KEY.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.FTSMSGS;
				messageType = MessageType.INCOMING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (MashreqFederatedReportConstants.UAEFTS_INCOMING_MTFN_KEY.equalsIgnoreCase(componentDetailKey)) {
				tableType = UAEFTSTableType.INCOMING_MTFN;
				messageType = MessageType.INCOMING;
				mesgType = MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			}
			dataContext.setTableType(tableType);
			dataContext.setInputMessage(reportOutput);
			dataContext.setMesgType(mesgType);
			if (MashreqFederatedReportConstants.UAEFTS_FORMAT_ACTION_REPAIR_STATUS.equalsIgnoreCase(formatAction)) {
				reportOutput.setActivityStatus(MashreqFederatedReportConstants.UAEFTS_ACTIVITY_LOG_DEFAULT_STATUS);
			}
		}
		if (dataContext != null) {
			if (messageType == MessageType.INCOMING) {
				uaeftsContext.setIncomingMessage(dataContext);
			} else if (messageType == MessageType.OUTGOING) {
				uaeftsContext.setOutgoingMessage(dataContext);
			}
		}
	}

	private void processMTQAQuery(ReportComponentDTO component, ReportComponentDetailDTO componentDetail,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			UAEFTSReportContext uaeftsReportContext) {
		Set<String> processedReferenceNums = new HashSet<String>();
		Set<String> toBeProcessedRefNums = new HashSet<String>();
		toBeProcessedRefNums.add(piReportInput.getUserReferenceNum());
		List<UAEFTSReportDataContext> totalData = new ArrayList<UAEFTSReportDataContext>();
		do {
			List<UAEFTSReportDataContext> contextData = processReferenceNums(component, componentDetail, piReportInput,
					uaeftsReportContext, reportContext, toBeProcessedRefNums);
			processedReferenceNums.addAll(toBeProcessedRefNums);
			toBeProcessedRefNums = new HashSet<String>();
			if (contextData != null) {
				for (UAEFTSReportDataContext contextD : contextData) {
					addDataToMainList(contextD, totalData);
					String referenceNum = contextD.getReferenceNum();
					if (!processedReferenceNums.contains(referenceNum)) {
						toBeProcessedRefNums.add(referenceNum);
					}
					String relatedRefNum = contextD.getRelatedReferenceNum();
					if (relatedRefNum != null && !processedReferenceNums.contains(relatedRefNum)) {
						toBeProcessedRefNums.add(relatedRefNum);
					}
				}
			}
		} while (toBeProcessedRefNums.size() > 0);
		if (totalData != null) {
			List<UAEFTSReportDataContext> incomingEnquiries = new ArrayList<UAEFTSReportDataContext>();
			List<UAEFTSReportDataContext> outgoingEnquiries = new ArrayList<UAEFTSReportDataContext>();
			for (UAEFTSReportDataContext context : totalData) {
				if (context.getMesgType()
						.startsWith(MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL)) {
					outgoingEnquiries.add(context);
				} else if (context.getMesgType()
						.startsWith(MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT_INITIAL)) {
					incomingEnquiries.add(context);
				}
			}
			uaeftsReportContext.setIncomingEnquiries(incomingEnquiries);
			uaeftsReportContext.setOutgoingEnquiries(outgoingEnquiries);
			// set the appropriate reference number
			// Collections.sort(totalData, new UAEFTSReportDataContextComparator());
			piReportInput.setUserReferenceNum(totalData.get(0).getRelatedReferenceNum());
		}

	}

	private List<UAEFTSReportDataContext> processReferenceNums(ReportComponentDTO component,
			ReportComponentDetailDTO context1, PaymentInvestigationReportInput piReportInput,
			UAEFTSReportContext uaeftsReportContext, ReportContext executionContext, Set<String> toBeProcessedRefNums) {
		List<UAEFTSReportDataContext> contextList = new ArrayList<UAEFTSReportDataContext>();
		for (String referenceNum : toBeProcessedRefNums) {
			ReportComponentDetailContext detailsContext = new ReportComponentDetailContext();
			ReportComponentDetailContext context = populateReportComponentDetailContext(context1, piReportInput,
					executionContext);
			List<ReportDefaultOutput> execute = queryExecutorService.executeQuery(context1, detailsContext);
			List<UAEFTSReportDataContext> populateMTQAData = populateMTQAData(context);
			if (populateMTQAData != null) {
				contextList.addAll(populateMTQAData);

			}
		}
		return contextList;

	}

	private List<UAEFTSReportDataContext> populateMTQAData(ReportComponentDetailContext context) {
		List<UAEFTSReportDataContext> reportContext = new ArrayList<UAEFTSReportDataContext>();
		List<ReportDefaultOutput> rawComponentDetailData = context.getComponentDetailData();
		if (rawComponentDetailData != null) {
			for (ReportDefaultOutput defaultOutput : rawComponentDetailData) {
				List<Object> rowData = defaultOutput.getRowData();
				PaymentInvestigationReportOutput reportOutput = createPaymentInvestigationOutput(defaultOutput);
				reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
				reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(1)));
				reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(2)));
				reportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(3)));
				reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(4)));
				reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(5)));
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_UAEFTS);
				String relatedRefNum = (UtilityClass.getStringRepresentation(rowData.get(6)));
				String mesgType = (UtilityClass.getStringRepresentation(rowData.get(7)));
				String msgId = (UtilityClass.getStringRepresentation(rowData.get(8)));
				reportOutput.setMesgType(mesgType);
				UAEFTSReportDataContext uaeftsContext = new UAEFTSReportDataContext();
				uaeftsContext.setMesgType(mesgType);
				uaeftsContext.setReferenceNum(reportOutput.getSourceRefNum());
				uaeftsContext.setRelatedReferenceNum(relatedRefNum);
				uaeftsContext.setMsgId(msgId);
				uaeftsContext.setInputMessage(reportOutput);
				uaeftsContext.setTableType(UAEFTSTableType.MTQA);
				reportContext.add(uaeftsContext);
			}
		}
		return reportContext;
	}

	private PaymentInvestigationReportOutput createPaymentInvestigationOutput(ReportDefaultOutput defaultOutput) {
		PaymentInvestigationReportOutput output = new PaymentInvestigationReportOutput();
		Long outputdata = output.getComponentDetailId();
		return output;
	}

	private String pickAmlQuery(UAEFTSTableType tableType) {
		String amlQuery = null;
		switch (tableType) {
		case MTINPUTMSGS:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_MTINPUTMSGS_DETAILS_KEY;
			break;
		case MANUALMSGS:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_MANUALMSGS_DETAILS_KEY;
			break;
		case MT202:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_MT202_KEY;
			break;
		case MT202INPUTMSGS:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_MT202_INPUTMSGS_DETAILS_KEY;
			break;
		case FTSMSGS:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_FTSMSGS_KEY;
			break;
		case INCOMING_MTFN:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_INCOMING_MTFN_KEY;
			break;
		case MTQA:
			amlQuery = MashreqFederatedReportConstants.UAEFTS_AML_MTQA_KEY;
			break;

		}
		return amlQuery;
	}

	private void addDataToMainList(UAEFTSReportDataContext contextD, List<UAEFTSReportDataContext> totalData) {
		if (!isMatchingRecordExist(totalData, contextD)) {
			totalData.add(contextD);
		}
	}

	private boolean isMatchingRecordExist(List<UAEFTSReportDataContext> totalData, UAEFTSReportDataContext contextD) {
		boolean matchingRecordExist = false;
		for (UAEFTSReportDataContext context : totalData) {
			if (context.getReferenceNum().equalsIgnoreCase(contextD.getReferenceNum())
					&& context.getMesgType().equalsIgnoreCase(contextD.getMesgType())) {
				matchingRecordExist = true;
				break;
			}
		}
		return matchingRecordExist;
	}

	private List<? extends ReportOutput> processUAEFTSDetailedReport(
			UAEFTSDetailedReportInput uaeftsDetailedReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> outputList = new ArrayList<ReportDefaultOutput>();
		// FederatedReportPromptDTO detailedType =
		// uaeftsDetailedReportInput.getMesgTypePrompt();
		ReportComponentDTO component = uaeftsDetailedReportInput.getComponent();

		Set<ReportComponentDetailDTO> reportComponentDetailDTO = component.getReportComponentDetails();

		if (reportComponentDetailDTO != null) {
			// pick the right query based on mesgtype
			ReportComponentDetailDTO matchedComponentDetail = null;
			for (ReportComponentDetailDTO componentDetail : reportComponentDetailDTO) {
				if (componentDetail.getQueryKey().toLowerCase()
						.contains(uaeftsDetailedReportInput.getMesgTypePrompt().getPromptValue().toLowerCase())) {
					matchedComponentDetail = componentDetail;
					break;
				}
			}
			if (matchedComponentDetail != null) {
				ReportComponentDetailContext context = new ReportComponentDetailContext();
				context.setQueryKey(matchedComponentDetail.getQuery());
				List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
				prompts.add(uaeftsDetailedReportInput.getReferenceNumPrompt());
				context.setPrompts(prompts);
				context.setExecutionId(reportContext.getExecutionId());
				// needed transpose
				context.setPopulateMetadata(true);
				List<ReportDefaultOutput> context1;
				for (ReportComponentDetailDTO componentDetailDTO : reportComponentDetailDTO) {
					context1 = queryExecutorService.executeQuery(componentDetailDTO, context);
					break;
				}

				if (context.getComponentDetailData() != null) {
					outputList.addAll(context.getComponentDetailData());
				}
			}
		}
		return outputList;
	}

	private void updatePrompts(ReportComponentDetailContext context, UAEFTSReportDataContext uaeftsContext,
			String componentDetailKey) {
		// for CCN query
		if (MashreqFederatedReportConstants.UAEFTS_INCOMING_CCN_KEY.equalsIgnoreCase(componentDetailKey)) {
			FederatedReportPromptDTO cbUniqueFileIdPrompt = new FederatedReportPromptDTO();
			cbUniqueFileIdPrompt.setPromptKey(MashreqFederatedReportConstants.CBUNIQUE_FILE_ID_PROMPT_KEY);
			cbUniqueFileIdPrompt.setPromptValue(uaeftsContext.getCbUniqueFileId());
			cbUniqueFileIdPrompt.setValueType(PromptValueType.VALUE);
			context.getPrompts().add(cbUniqueFileIdPrompt);
		} // for AML queries
		else {
			FederatedReportPromptDTO msgIdPrompt = new FederatedReportPromptDTO();
			msgIdPrompt.setPromptKey(MashreqFederatedReportConstants.MSG_ID_PROMPT_KEY);
			msgIdPrompt.setPromptValue(uaeftsContext.getMsgId());
			msgIdPrompt.setValueType(PromptValueType.VALUE);
			context.getPrompts().add(msgIdPrompt);
		}
	}

}
