package com.mashreq.paymentTracker.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.PaymentInvestigationGatewayService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;

public class PaymentTrackerReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	@Autowired
	PaymentInvestigationGatewayService paymentInvestigationGatewayService;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	private static final Logger log = LoggerFactory.getLogger(PaymentTrackerReportServiceImpl.class);
	private static final String FILENAME = "PaymentTrackerReportServiceImpl";

	@Override
	protected PaymentInvestigationReportInput populateBaseInputContext(ReportContext reportContext) {
		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> promptsList = new ArrayList<ReportPromptsInstanceDTO>();
		if (null != reportInstance) {
			promptsList = reportInstance.getPromptsList();
		}
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.PAYMENT_TRACKER_REFERENCE_NUM_PROMPT_KEY);
		FederatedReportPromptDTO countryCodePrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.PAYMENT_TRACKER_COUNTRY_CODE_PROMPT_KEY);
		FederatedReportPromptDTO retrieveTillPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.PAYMENT_TRACKER_RETRIEVE_TILL_PROMPT_KEY);
		PaymentInvestigationReportInput input = new PaymentInvestigationReportInput();
		if (referenceNumPrompt != null) {
			String promptValue = referenceNumPrompt.getPromptValue();
			if (promptValue.startsWith(MashreqFederatedReportConstants.EDMS_REF_PREFIX)
					&& promptValue.endsWith(MashreqFederatedReportConstants.EDMS_REF_SUFFIX)
					&& promptValue.length() == 25) {
				promptValue = promptValue.substring(6, 21);
				referenceNumPrompt.setPromptValue(promptValue);
			}
			input.setReferenceNumPrompt(referenceNumPrompt);
		} else {
			log.debug("Prompt doesn't exist for "
					+ MashreqFederatedReportConstants.PAYMENT_TRACKER_REFERENCE_NUM_PROMPT_KEY);
		}
		if (countryCodePrompt != null) {
			input.setCountryCodePrompt(countryCodePrompt);
		} else {
			log.debug("Prompt doesn't exist for "
					+ MashreqFederatedReportConstants.PAYMENT_TRACKER_COUNTRY_CODE_PROMPT_KEY);
		}
		if (retrieveTillPrompt != null) {
			String timeValue = retrieveTillPrompt.getPromptValue();
			String timeInDays = MashreqFederatedReportConstants.RETRIEVE_TILL_PROMPT_DEFAULT_VALUE;
			if (null != timeValue) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
				try {
					Date promptDate = sdf.parse(timeValue);
					Date currentDate = new Date();
					int diffInDays = (int) ((currentDate.getTime() - promptDate.getTime()) / (1000 * 60 * 60 * 24));
					if (diffInDays >= 0) {
						timeInDays = diffInDays + "";
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(timeInDays));
				timeValue = sdf.format(cal.getTime());
				// update the prompt instance with final value so that it can be shown later in
				// reports
				Optional<ReportPromptsInstanceDTO> retrieveTillInstancePrompt = promptsList.stream()
						.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(
								MashreqFederatedReportConstants.PAYMENT_TRACKER_RETRIEVE_TILL_PROMPT_KEY))
						.findFirst();

				if (retrieveTillInstancePrompt.isPresent()) {
					ReportPromptsInstanceDTO retrieveTillPromptObj = retrieveTillInstancePrompt.get();
					/*
					 * YET to work on this retrieveTillPromptObj.setValue(timeValue);
					 * retrieveTillPromptObj.setValueDisplay(timeValue);
					 * getCannedReportInstanceManagementService()
					 * .updateCannedReportInstancePrompt(retrieveTillInstancePrompt);
					 */
				}
			}
			retrieveTillPrompt.setPromptValue(timeInDays);
			input.setTimeInDaysPrompt(retrieveTillPrompt);
		} else {
			log.debug("Prompt doesn't exist for "
					+ MashreqFederatedReportConstants.PAYMENT_TRACKER_RETRIEVE_TILL_PROMPT_KEY);
		}
		input.setUserReferenceNum(referenceNumPrompt.getPromptValue());
		input.setOriginalUserReferenceNum(input.getUserReferenceNum());
		return input;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> promptsList,
			String advanceSearchFromDatePromptKey) {
		// TODO Auto-generated method stub

		FederatedReportPromptDTO promptInfo = new FederatedReportPromptDTO();
		ReportPromptsInstanceDTO matchedPrompt = null;
		for (ReportPromptsInstanceDTO prompt : promptsList) {
			if (prompt.getPrompt().getKey().equalsIgnoreCase(advanceSearchFromDatePromptKey)) {
				matchedPrompt = prompt;
				break;
			}
		}
		if (matchedPrompt != null) {
			promptInfo.setPromptKey(matchedPrompt.getPrompt().getKey());
			promptInfo.setPromptValue(matchedPrompt.getPrompt().getPromptValue());
			promptInfo.setValueList(matchedPrompt.getPrompt().getValue());
		}
		return promptInfo;
	}

	@Override
	protected ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		List<PaymentInvestigationReportOutput> reportOutput = new ArrayList<PaymentInvestigationReportOutput>();
		// process each component based on the rules and once all components are
		// processed, we need to re-arrange the data once gathered from all components
		// some of the components might need to hit in parallel and wait for the result
		// to hit next component because of the dependency
		// convert the data to object form and then transform the data using common
		// method for formats etc.
		// handle the inactive components, we should not add the data from them to final
		// list
		// plus we need to handle exception gracefully and still proceed with the next
		// systems
		// for each component, once it is processed, add the component detail execution
		// time to actual query execution time at the end
		// start processing components, we need to proceed with next component by
		// gracefully handle exception of any system here
		Report report = new Report();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		if (null != reportInstanceDTO) {
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		List<Components> componentList = componentsDAO.findAllByreportId(report.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			PaymentInvestigationReportInput paymentInvestigationReportInput = (PaymentInvestigationReportInput) reportInput;
			paymentInvestigationGatewayService.processGateway(paymentInvestigationReportInput, componentList,
					reportContext);
			return null;
		}

	}
}