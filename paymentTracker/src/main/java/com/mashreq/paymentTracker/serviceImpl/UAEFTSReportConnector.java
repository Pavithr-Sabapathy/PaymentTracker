package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.CannedReportInstanceComponentDetail;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportInstanceComponentDTO;
import com.mashreq.paymentTracker.dto.UAEFTSDetailedReportInput;
import com.mashreq.paymentTracker.dto.UAEFTSReportContext;
import com.mashreq.paymentTracker.dto.UAEFTSReportDataContext;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;

@Service
public class UAEFTSReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(UAEFTSReportConnector.class);
	private static final String FILENAME = "UAEFTSReportConnector";

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
		Set<ReportComponentDetailDTO> componentDetailList = component.getReportComponentDetails();
		if (!componentDetailList.isEmpty()) {
			UAEFTSReportContext uaeftsReportContext = new UAEFTSReportContext();

			if (piReportInput.getRoleName().equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_REPORTING_ROLE)
					|| piReportInput.getRoleName()
							.equalsIgnoreCase(MashreqFederatedReportConstants.CUSTOMER_MATRIX_REPORTING_ROLE)) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MTQA_KEY, uaeftsReportContext, reportContext);
			}
			processComponentDetail(component, componentDetailList, piReportInput,
					MashreqFederatedReportConstants.UAEFTS_MTINPUTMSGS_DETAILS_KEY, uaeftsReportContext, reportContext);
			if (uaeftsReportContext.getOutgoingMessage() == null) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MANUALMSGS_DETAILS_KEY, uaeftsReportContext,
						reportContext);

			}
			if (uaeftsReportContext.getOutgoingMessage() == null) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MT202_KEY, uaeftsReportContext, reportContext);
			}
			if (uaeftsReportContext.getOutgoingMessage() == null) {
				processComponentDetail(component, componentDetailList, piReportInput,
						MashreqFederatedReportConstants.UAEFTS_MT202_INPUTMSGS_DETAILS_KEY, uaeftsReportContext,
						reportContext);
			}
		}

	}

	private void processComponentDetail(ReportComponentDTO component, Set<ReportComponentDetailDTO> componentDetailList,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey,
			UAEFTSReportContext uaeftsReportContext, ReportContext reportContext) {
		ReportInstanceComponentDTO componentDetail = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		if (componentDetail != null) {
			if (MashreqFederatedReportConstants.UAEFTS_AML_MTQA_KEY.equalsIgnoreCase(componentDetailKey)) {
				processMTQAQuery(component, componentDetailList, componentDetail, piReportInput, reportContext,
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
			}

		}

	}

	private void processMTQAQuery(ReportComponentDTO component, Set<ReportComponentDetailDTO> componentDetailList,
			ReportInstanceComponentDTO componentDetail, PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext, UAEFTSReportContext uaeftsReportContext) {
		Set<String> processedReferenceNums = new HashSet<String>();
		Set<String> toBeProcessedRefNums = new HashSet<String>();
		toBeProcessedRefNums.add(piReportInput.getUserReferenceNum());
		List<UAEFTSReportDataContext> totalData = new ArrayList<UAEFTSReportDataContext>();
		do {
			List<UAEFTSReportDataContext> contextData = processReferenceNums(component, componentDetail, piReportInput,
					reportContext, uaeftsReportContext, toBeProcessedRefNums);
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
			ReportInstanceComponentDTO componentDetail, PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext, UAEFTSReportContext uaeftsReportContext, Set<String> toBeProcessedRefNums) {
		// TODO Auto-generated method stub
		return null;
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

	private ReportInstanceComponentDTO getMatchedInstanceComponentDetail(
			Set<ReportComponentDetailDTO> componentDetailList, String componentDetailKey) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<? extends ReportOutput> processUAEFTSDetailedReport(
			UAEFTSDetailedReportInput uaeftsDetailedReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> outputList = new ArrayList<ReportDefaultOutput>();
		FederatedReportPromptDTO detailedType = uaeftsDetailedReportInput.getMesgTypePrompt();
		ReportComponentDTO component = uaeftsDetailedReportInput.getComponent();
		List<CannedReportInstanceComponentDetail> cannedReportInstanceComponentDetails = populateCannedReportInstanceDetails(
				component.getId());
		if (cannedReportInstanceComponentDetails != null) {
			// pick the right query based on mesgtype
			CannedReportInstanceComponentDetail matchedComponentDetail = null;
			for (CannedReportInstanceComponentDetail componentDetail : cannedReportInstanceComponentDetails) {
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
				processComponentDetail(component, matchedComponentDetail, context);
				if (context.getComponentDetailData() != null) {
					outputList.addAll(context.getComponentDetailData());
				}
			}
		}
		return outputList;
	}

	private List<CannedReportInstanceComponentDetail> populateCannedReportInstanceDetails(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	private void processComponentDetail(ReportComponentDTO component,
			CannedReportInstanceComponentDetail matchedComponentDetail, ReportComponentDetailContext context) {
		// TODO Auto-generated method stub

	}
}
