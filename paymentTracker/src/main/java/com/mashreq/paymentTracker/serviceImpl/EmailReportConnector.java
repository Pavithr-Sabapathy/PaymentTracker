package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
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
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class EmailReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(EmailReportConnector.class);
	private static final String FILENAME = "EmailReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {

		List<PaymentInvestigationReportOutput> outputList = new ArrayList<PaymentInvestigationReportOutput>();
		PaymentInvestigationReportInput piReportInput = (PaymentInvestigationReportInput) reportInput;
		ReportComponentDTO componentObj = piReportInput.getComponent();
		if (null != componentObj) {
			Set<ReportComponentDetailDTO> componentDetailList = componentObj.getReportComponentDetails();
			if (!componentDetailList.isEmpty()) {
				if (!piReportInput.getReferenceList().isEmpty()) {
					List<PaymentInvestigationReportOutput> emailRecordsList = processComponentDetail(componentObj,
							componentDetailList, piReportInput,
							MashreqFederatedReportConstants.EMAIL_SOURCE_CONTENT_KEY, reportContext);
					if (!emailRecordsList.isEmpty()) {
						outputList.addAll(emailRecordsList);
					}
				}
			}
		}
		return outputList;

	}

	private List<PaymentInvestigationReportOutput> processComponentDetail(ReportComponentDTO componentObj,
			Set<ReportComponentDetailDTO> componentDetailList, PaymentInvestigationReportInput piReportInput,
			String componentDetailKey, ReportContext reportContext) {
		List<PaymentInvestigationReportOutput> finalOutputList = new ArrayList<PaymentInvestigationReportOutput>();
		ReportComponentDetailDTO matchedComponentDetail = getMatchedInstanceComponentDetail(componentDetailList,
				componentDetailKey);
		if (matchedComponentDetail != null) {
			ReportComponentDetailContext context = populateReportComponentDetailContext(matchedComponentDetail,
					piReportInput, reportContext);
			List<ReportDefaultOutput> outputList = queryExecutorService.executeQuery(matchedComponentDetail, context);
			if (!outputList.isEmpty()) {
				outputList.stream().forEach(defaultOutput -> {
					List<Object> rowData = defaultOutput.getRowData();
					PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
					reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
					reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
					reportOutput.setActivity(UtilityClass.getStringRepresentation(rowData.get(1)));
					reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(2)));
					reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(3)));
					reportOutput.setEmailUrl(UtilityClass.getStringRepresentation(rowData.get(4)));
					reportOutput.setSourceRefNum(MashreqFederatedReportConstants.EMAIL_SOURCE_REF_PREFIX);
					reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_EMAIL);
					finalOutputList.add(reportOutput);
				});
			}
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}
		return finalOutputList;

	}

}